// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.journal;

import java.util.List;

import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.symbio.State;

/**
 * The entries and possible snapshot of a full or partial stream of a given named stream.
 *
 * @param <T> the concrete type of the stream of {@code Entry<T>}, which maybe be {@code String}, {@code byte[]}, or {@code Object}
 */
public class EntityStream<T> {

  /**
   * The most recent {@code State<T>} snapshot, if any.
   */
  public final State<T> snapshot;

  /**
   * The {@code List<Entry<T>>} of the entries of the named stream, and possibly just a sub-stream.
   */
  public final List<BaseEntry<T>> entries;

  /**
   * The String name of the stream, which is generally a global unique identity
   * of an associated entity/aggregate.
   */
  public final String streamName;

  /**
   * The version of the stream, which indicates the 1-based sequence of the
   * last of all my entries listed herein. All entry streams start at version
   * 1 and end with the total number of all its entries, e.g. entries.size().
   */
  public final int streamVersion;

  /**
   * Construct a new Stream.
   *
   * @param streamName the {@code String} name of this stream, which is generally a global unique identity
   * of an associated entity/aggregate
   * @param streamVersion the {@code int} version of the stream
   * @param entries the {@code List<Entry<T>>} of all entries in the named stream or some sub-stream
   * @param snapshot the {@code State<T>} of a persisted state, or an empty {@code State<T>} if none
   */
  public EntityStream(final String streamName, final int streamVersion, final List<BaseEntry<T>> entries, final State<T> snapshot) {
    this.streamName = streamName;
    this.streamVersion = streamVersion;
    this.entries = entries;
    this.snapshot = snapshot;
  }

  /**
   * Answers whether or not I hold a non-empty snapshot.
   *
   * @return boolean
   */
  public boolean hasSnapshot() {
    return snapshot != null && !snapshot.isEmpty();
  }

  /**
   * Answer my size, which is the number of entries.
   * @return int
   */
  public int size() {
    return entries.size();
  }

  @Override
  public String toString() {
    return "EntityStream["
            + "streamName=" + streamName
            + " streamVersion=" + streamVersion
            + " entries=" + entries
            + " snapshot=" + snapshot
            + "]";
  }
}
