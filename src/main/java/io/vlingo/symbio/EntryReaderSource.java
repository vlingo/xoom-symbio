// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import io.vlingo.common.Completes;
import io.vlingo.reactivestreams.Elements;
import io.vlingo.reactivestreams.Source;
import io.vlingo.symbio.store.EntryReader;

/**
 * Reads {@code Entry} instances from a {@code Journal}, {@code ObjectStore},
 * or {@code StateStore}, by means of a given {@code EntryReader<T>}. You must
 * provide a {@code maximumEntries} to the constructor, or use one of the two
 * methods that requires a {@code maximumElements}.
 *
 * @param <T> the T type of Entry and Elements being read
 */
public class EntryReaderSource<T extends Entry<T>> implements Source<T> {
  private final int maximumEntries;
  private final EntryReader<T> reader;
  private final Completes<Elements<T>> repeatableCompletes;

  /**
   * Constructs my state.
   * @param reader the {@code EntryReader<T>} from which to read entry elements
   * @param maximumEntries the int default number of maximum elements to read
   * @param repeatableCompletes the {@code Completes<Elements<T>>} that supports repeated completions
   */
  public EntryReaderSource(
          final EntryReader<T> reader,
          final int maximumEntries,
          final Completes<Elements<T>> repeatableCompletes) {

    this.reader = reader;
    this.maximumEntries = maximumEntries <= 0 ? 1 : maximumEntries;
    this.repeatableCompletes = repeatableCompletes;
  }

  /**
   * Constructs my state with a maximum number of entries to read on each {@code next(...)} of 1.
   * @param reader the {@code EntryReader<T>} from which to read entry elements
   * @param repeatableCompletes the {@code Completes<Elements<T>>} that supports repeated completions
   */
  public EntryReaderSource(
          final EntryReader<T> reader,
          final Completes<Elements<T>> repeatableCompletes) {

    this(reader, 1, repeatableCompletes);
  }

  /**
   * @see io.vlingo.reactivestreams.Source#next()
   */
  @Override
  public Completes<Elements<T>> next() {
    return next(maximumEntries);
  }

  /**
   * @see io.vlingo.reactivestreams.Source#next(int)
   */
  @Override
  public Completes<Elements<T>> next(final int maximumElements) {
    final Completes<Elements<T>> answer = repeatableCompletes.repeat();

    reader.readNext(maximumElements).andFinally(entries -> {
      return Completes.withSuccess(Elements.of(entries.toArray()));
    });

    return answer;
  }

  /**
   * @see io.vlingo.reactivestreams.Source#next(long)
   */
  @Override
  public Completes<Elements<T>> next(final long index) {
    return next(index, maximumEntries);
  }

  /**
   * @see io.vlingo.reactivestreams.Source#next(long, int)
   */
  @Override
  public Completes<Elements<T>> next(final long index, final int maximumElements) {
    final Completes<Elements<T>> answer = repeatableCompletes.repeat();

    reader.readNext(String.valueOf(index), maximumElements).andFinally(entries -> {
      return Completes.withSuccess(Elements.of(entries.toArray()));
    });

    return answer;
  }

  /**
   * @see io.vlingo.reactivestreams.Source#isSlow()
   */
  @Override
  public Completes<Boolean> isSlow() {
    return Completes.withSuccess(true);
  }
}
