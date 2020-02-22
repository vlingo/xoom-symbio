// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import io.vlingo.actors.Actor;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Completes;
import io.vlingo.common.Scheduled;
import io.vlingo.reactivestreams.Elements;
import io.vlingo.reactivestreams.Source;
import io.vlingo.reactivestreams.Stream;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapterProvider;

/**
 * Reads {@code Entry} instances from a {@code Journal}, {@code ObjectStore},
 * or {@code StateStore}, by means of a given {@code EntryReader<T>}. You must
 * provide a {@code maximumEntries} to the constructor, or use one of the two
 * methods that requires a {@code maximumElements}.
 *
 * @param <T> the T type of Entry and Elements being read
 */
public class EntryReaderSource<T extends Entry<?>> extends Actor implements Source<T>, Scheduled<Object> {
  private final Deque<T> cache;
  private final Cancellable cancellable;
  private final EntryReader<T> entryReader;
  private final long flowElementsRate;
  private boolean reading;
  private final EntryAdapterProvider entryAdapterProvider;

  /**
   * Constructs my default state.
   * @param reader the {@code EntryReader<T>} from which to read entry elements
   * @param name the String name of the reader
   * @param entryAdapterProvider the EntryAdapterProvider used to turn Entry instances into Source<?> instances
   */
  @SuppressWarnings("unchecked")
  public EntryReaderSource(
          final EntryReader<T> entryReader,
          final EntryAdapterProvider entryAdapterProvider,
          final long flowElementsRate) {

    this.entryReader = entryReader;
    this.entryAdapterProvider = entryAdapterProvider;
    this.flowElementsRate = flowElementsRate;
    this.cache = new ArrayDeque<>();

    this.cancellable = scheduler().schedule(selfAs(Scheduled.class), null, 0, Stream.FastProbeInterval);
  }

  /**
   * @see io.vlingo.reactivestreams.Source#next()
   */
  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Completes<Elements<T>> next() {
    if (!cache.isEmpty()) {
      final List<io.vlingo.symbio.Source> next = new ArrayList<>();

      for (int index = 0; index < flowElementsRate; ++index) {
        final io.vlingo.symbio.Source source = entryAdapterProvider.asEntry(null, index, null);
        next.add(source);
      }
      final Elements elements = Elements.of(arrayFrom(next));
      return Completes.withSuccess(elements);
    }
    return Completes.withFailure(Elements.empty());
  }

  /**
   * @see io.vlingo.reactivestreams.Source#next(int)
   */
  @Override
  public Completes<Elements<T>> next(final int maximumElements) {
    return next();
  }

  /**
   * @see io.vlingo.reactivestreams.Source#next(long)
   */
  @Override
  public Completes<Elements<T>> next(final long index) {
    return next();
  }

  /**
   * @see io.vlingo.reactivestreams.Source#next(long, int)
   */
  @Override
  public Completes<Elements<T>> next(final long index, final int maximumElements) {
    return next();
  }

  /**
   * @see io.vlingo.reactivestreams.Source#isSlow()
   */
  @Override
  public Completes<Boolean> isSlow() {
    return Completes.withSuccess(false);
  }

  //====================================
  // Scheduled
  //====================================

  @Override
  public void intervalSignal(final Scheduled<Object> scheduled, final Object data) {
    if (cache.isEmpty() && !reading) {
      reading = true;
      entryReader.readNext(100).andThenConsume(entries -> { cache.addAll(entries); reading = false; });
    }
  }

  //====================================
  // Internal implementation
  //====================================

  /**
   * @see io.vlingo.actors.Actor#stop()
   */
  @Override
  public void stop() {
    cancellable.cancel();

    super.stop();
  }

  @SuppressWarnings("rawtypes")
  private io.vlingo.symbio.Source[] arrayFrom(final List<?> sources) {
    return sources.toArray(new OpaqueSource[sources.size()]);
  }

  @SuppressWarnings("rawtypes")
  private static class OpaqueSource extends io.vlingo.symbio.Source { }
}
