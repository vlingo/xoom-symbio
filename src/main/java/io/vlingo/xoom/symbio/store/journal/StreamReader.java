// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.journal;

import io.vlingo.xoom.common.Completes;

/**
 * The reader for a specific named {@code Stream<T>} within the {@code Journal<T>}, which is provided by
 * its {@code streamReader()}. This reader can read all existing {@code Entry<T>} instances of a given
 * {@code Stream<T>}, or from a specific {@code Stream<T>} version. If snapshots are used for the
 * given {@code Stream<T>}, the result may include a snapshot {@code State<T>} instance if one is available
 * within the given {@code Stream<T>} version span ({@code fromStreamVersion} to the stream end).
 * 
 * @param <T> the concrete type of {@code Stream<T>} stored and read, which maybe be {@code String}, {@code byte[]}, or {@code Object}
 */
public interface StreamReader<T> {

  /**
   * The stream version of the first entry in every stream.
   */
  static final int FirstStreamVersion = 1;

  /**
   * Eventually answers the full {@code Stream<T>} of the stream with {@code streamName}; or if
   * a {@code State<T>} snapshot is available, only the snapshot and any {@code Entry<T>} instances
   * that exist since the snapshot.
   * 
   * @param streamName the String name of the {@code Stream<T>} to answer
   * 
   * @return the {@code Completes<Stream<T>>} of the full stream, or a snapshot with remaining stream
   */
  Completes<EntityStream<T>> streamFor(final String streamName);

  /**
   * Eventually answers the {@code Stream<T>} of the stream with {@code streamName}, starting with
   * {@code fromStreamVersion} until the end of the stream. Any existing snapshot within this
   * sub-stream is ignored. This method enables reading the entire stream without snapshot
   * optimizations (e.g. {@code reader.streamFor(id, 1)} reads the entire stream of {@code id}).
   * 
   * @param streamName the {@code String} name of the {@code Stream<T>} to answer
   * @param fromStreamVersion the {@code int} version from which to begin reading, inclusive
   * 
   * @return the {@code Completes<Stream<T>>} of the full stream
   */
  Completes<EntityStream<T>> streamFor(final String streamName, final int fromStreamVersion);
}
