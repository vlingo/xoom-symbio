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
import io.vlingo.symbio.BaseEntry;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.EntryBundle;

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
   * @param entryReader the {@code EntryReader<T>} from which to read entry elements
   * @param entryAdapterProvider the EntryAdapterProvider used to turn Entry instances into {@code Source<?>} instances
   * @param flowElementsRate the long maximum elements to read at once
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
      final List<EntryBundle> next = new ArrayList<>();

      for (int index = 0; index < flowElementsRate && !cache.isEmpty(); ++index) {
        final T entry = cache.poll();
        // This little trick gets a PersistentEntry to a BaseEntry: entry.withId(entry.id())
        final T normalized = (entry instanceof BaseEntry) ? entry : (T) entry.withId(entry.id());
        final io.vlingo.symbio.Source source = entryAdapterProvider.asSource(normalized);
        next.add(new EntryBundle(entry, source));
      }
      final Elements elements = Elements.of(arrayFrom(next));
      return completes().with(elements);
    }
    return completes().with(Elements.empty());
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
    return completes().with(false);
  }

  //====================================
  // Scheduled
  //====================================

  @Override
  public void intervalSignal(final Scheduled<Object> scheduled, final Object data) {
    if (cache.isEmpty() && !reading) {
      reading = true;
      final int max = flowElementsRate > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) flowElementsRate;
      entryReader.readNext(max).andThenConsume(entries -> { cache.addAll(entries); reading = false; });
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

  private EntryBundle[] arrayFrom(final List<EntryBundle> sources) {
    return sources.toArray(new EntryBundle[sources.size()]);
  }
}
