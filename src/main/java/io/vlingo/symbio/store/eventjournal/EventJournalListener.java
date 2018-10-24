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
 * A listener for all appends made to a given EventJournal&lt;T&gt; that must be registered
 * upon creation of the given EventJournal&lt;T&gt; instance. The registration is therefore
 * implementation dependent.
 *
 * @param <T> the concrete type of Event&lt;T&gt; stored and read, which maybe be String, byte[], or Object
 */
public interface EventJournalListener<T> {

  /**
   * Indicates that event was just successfully appended.
   * 
   * @param event the Event&lt;T&gt; that was just successfully appended
   */
  void appended(final Event<T> event);

  /**
   * Indicates that within the same transaction the event was just successfully
   * appended and the snapshot alone with it.
   * 
   * @param event the Event&lt;T&gt; that was just successfully appended
   * @param snapshot the current State&lt;T&gt; of the stream for which the event was appended
   */
  void appendedWith(final Event<T> event, final State<T> snapshot);

  /**
   * Indicates that all events were just successfully appended.
   * 
   * @param events the List&lt;Event&lt;T&gt;&gt; that were just successfully appended
   */
  void appendedAll(final List<Event<T>> events);

  /**
   * Indicates that within the same transaction the event was just successfully
   * appended and the snapshot alone with it.
   * 
   * @param events the List&lt;Event&lt;T&gt;&gt; that were just successfully appended
   * @param snapshot the current State&lt;T&gt; of the stream for which the event was appended
   */
  void appendedAllWith(final List<Event<T>> events, final State<T> snapshot);
}
