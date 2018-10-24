// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.eventjournal;

import io.vlingo.common.Completes;

/**
 * The reader for a specific named EventStream&lt;T&gt; within the EventJournal&lt;T&gt;, which is provided by
 * its eventStreamReader(). This reader can read all existing Event&lt;T&gt; instances of a given
 * EventStream&lt;T&gt;, or from a specific EventStream&lt;T&gt; version. If snapshots are used for the
 * given EventStream&lt;T&gt;, the result may include a snapshot State&lt;T&gt; instance if one is available
 * within the given EventStream&lt;T&gt; version span (fromStreamVersion to the stream end).
 * 
 * @param <T> the concrete type of EventStream&lt;T&gt; stored and read, which maybe be String, byte[], or Object
 */
public interface EventStreamReader<T> {

  /**
   * The stream version of the first event in every stream.
   */
  static final int FirstStreamVersion = 1;

  /**
   * Eventually answers the full EventStream&lt;T&gt; of the stream with streamName; or if
   * a State&lt;T&gt; snapshot is available, only the snapshot and any Event&lt;T&gt; instances
   * that exist since the snapshot.
   * 
   * @param streamName the String name of the EventStream&lt;T&gt; to answer
   * 
   * @return the Completes&lt;EventStream&lt;T&gt;&gt; of the full stream, or a snapshot with remaining stream
   */
  Completes<EventStream<T>> streamFor(final String streamName);

  /**
   * Eventually answers the EventStream&lt;T&gt; of the stream with streamName, starting with
   * fromStreamVersion until the end of the stream. Any existing snapshot within this
   * sub-stream is ignored. This method enables reading the entire stream without snapshot
   * optimizations (e.g. reader.streamFor(id, 1) reads the entire stream of id).
   * 
   * @param streamName the String name of the EventStream&lt;T&gt; to answer
   * @param fromStreamVersion the int version from which to begin reading, inclusive
   * 
   * @return the Completes&lt;EventStream&lt;T&gt;&gt; of the full stream
   */
  Completes<EventStream<T>> streamFor(final String streamName, final int fromStreamVersion);
}
