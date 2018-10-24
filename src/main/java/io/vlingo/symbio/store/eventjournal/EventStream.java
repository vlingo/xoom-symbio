// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.eventjournal;

import java.util.List;

import io.vlingo.symbio.Event;
import io.vlingo.symbio.State;

/**
 * The events and possible snapshot of a full or partial event stream of a given named stream.
 * 
 * @param <T> the concrete type of the stream of Event&lt;T&gt;, which maybe be String, byte[], or Object
 */
public class EventStream<T> {

  /**
   * The most recent State&lt;T&gt; snapshot, if any.
   */
  public final State<T> snapshot;

  /**
   * The List&lt;Event&lt;T&gt;&gt; of the events of the named stream, and possibly just a sub-stream.
   */
  public final List<Event<T>> events;

  /**
   * The String name of the stream, which is generally a global unique identity
   * of an associated entity/aggregate.
   */
  public final String streamName;

  /**
   * The version of the stream, which indicates the 1-based sequence of the
   * last of all my events listed herein. All event streams start at version
   * 1 and end with the total number of all its events, e.g. events.size().
   */
  public final int streamVersion;

  /**
   * Construct a new EventStream.
   * 
   * @param streamName the String name of this stream, which is generally a global unique identity
   * of an associated entity/aggregate
   * @param streamVersion the int version of the stream
   * @param events the List&lt;Event&lt;T&gt;&gt; of all events in the named stream or some sub-stream
   * @param snapshot the State&lt;T&gt; of a persisted state, or an empty State&lt;T&gt; if none
   */
  public EventStream(final String streamName, final int streamVersion, final List<Event<T>> events, final State<T> snapshot) {
    this.streamName = streamName;
    this.streamVersion = streamVersion;
    this.events = events;
    this.snapshot = snapshot;
  }

  /**
   * Answers whether or not I hold a non-empty snapshot.
   * 
   * @return boolean
   */
  public boolean hasSnapshot() {
    return !snapshot.isEmpty();
  }
}
