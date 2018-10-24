// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.eventjournal;

import java.util.List;

import io.vlingo.common.Completes;
import io.vlingo.symbio.Event;
import io.vlingo.symbio.State;

/**
 * The top-level journal used within a Bounded Context (microservice) to store all of
 * its Event&lt;T&gt; instances for EventSourced (and CommandSourced) components. Each use of
 * the journal appends some number of Event&lt;T&gt; instances and perhaps a single snapshot State&lt;T&gt;.
 * The journal may also be queried for an EventJournalReader&lt;T&gt; and a EventStreamReader&lt;T&gt;.
 * Assuming that all successfully appended Event&lt;T&gt; instances should be dispatched in some way
 * after each write transaction, you should register an EventJournalListener&lt;T&gt; when first
 * creating your EventJournal&lt;T&gt;.
 *
 * @param <T> the concrete type of Event&lt;T&gt; stored, which maybe be String, byte[], or Object
 */
public interface EventJournal<T> {

  /**
   * Appends the single Event&lt;T&gt; to the end of the journal and creates an association
   * to streamName with streamVersion. The Event&lt;T&gt; is not expected to have a valid id, and
   * currently any such is ignored and internally assigned. If there is a registered
   * EventJournalListener&lt;T&gt;, it will be informed of the newly appended Event&lt;T&gt; and it
   * will have an assigned valid id.
   * 
   * @param streamName the String name of the stream to append
   * @param streamVersion the int version of the stream to append
   * @param event the Event&lt;T&gt; to append
   */
  void append(final String streamName, final int streamVersion, final Event<T> event);

  /**
   * Appends the single Event&lt;T&gt; to the end of the journal and creates an association
   * to streamName with streamVersion. The Event&lt;T&gt; is not expected to have a valid id, and
   * currently any such is ignored and internally assigned. Also the State&lt;T&gt; snapshot of the
   * current state to which this Event&lt;T&gt; was applied is persisted. The event and snapshot
   * are consistently persisted together or neither at all. If there is a registered
   * EventJournalListener&lt;T&gt;, it will be informed of the newly appended Event&lt;T&gt; with an
   * assigned valid id and the new State&lt;T&gt; snapshot.
   * 
   * @param streamName the String name of the stream to append
   * @param streamVersion the int version of the stream to append
   * @param event the Event&lt;T&gt; to append
   * @param snapshot the State&lt;T&gt; state constituting the current state of the stream as of event
   */
  void appendWith(final String streamName, final int streamVersion, final Event<T> event, final State<T> snapshot);

  /**
   * Appends all Event&lt;T&gt; instances in events to the end of the journal and creates an
   * association to streamName with streamVersion. The Event&lt;T&gt; is not expected to have
   * a valid id, and currently any such is ignored and internally assigned. If there is
   * a registered EventJournalListener&lt;T&gt;, it will be informed of the newly appended Event&lt;T&gt;
   * instances and each will have an assigned valid id.
   * 
   * @param streamName the String name of the stream to append
   * @param fromStreamVersion the int version of the stream to start appending, and increasing for each of events
   * @param events the List&lt;Event&lt;T&gt;&gt; to append
   */
  void appendAll(final String streamName, final int fromStreamVersion, final List<Event<T>> events);

  /**
   * Appends all Event&lt;T&gt; instances in events to the end of the journal and creates an
   * association to streamName with streamVersion. The Event&lt;T&gt; is not expected to have
   * a valid id, and currently any such is ignored and internally assigned. Also the
   * State&lt;T&gt; snapshot of the current state to which this Event&lt;T&gt; instances were applied
   * is persisted. The events and snapshot are consistently persisted together or none at
   * all.  If there is a registered EventJournalListener&lt;T&gt;, it will be informed of the newly
   * appended Event&lt;T&gt; instances and each will have an assigned valid id, and the new
   * State&lt;T&gt; snapshot.
   * 
   * @param streamName the String name of the stream to append
   * @param fromStreamVersion the int version of the stream to start appending, and increasing for each of events
   * @param events the List&lt;Event&lt;T&gt;&gt; to append
   * @param snapshot the State&lt;T&gt; state constituting the current state of the stream as of the last of events
   */
  void appendAllWith(final String streamName, final int fromStreamVersion, final List<Event<T>> events, final State<T> snapshot);

  /**
   * Eventually answers the EventJournalReader&lt;T&gt; named name for this journal. If
   * the reader named name does not yet exist, it is first created. Readers
   * with different names enables reading from different positions and for different
   * reasons. For example, some readers may be interested in publishing Event&lt;T&gt;
   * instances messaging while others may be projecting and building pipelines
   * of new streams.
   * 
   * @param name the String name of the EventJournalReader&lt;T&gt; to answer
   * 
   * @return Completes&lt;EventJournalReader&lt;T&gt;&gt;
   */
  Completes<EventJournalReader<T>> eventJournalReader(final String name);

  /**
   * Eventually answers the EventStreamReader&lt;T&gt; named name for this journal. If
   * the reader named name does not yet exist, it is first created. Readers
   * with different names enables reading from different streams and for different
   * reasons. For example, some streams may be very busy while others are not.
   * 
   * @param name the String name of the EventJournalReader&lt;T&gt; to answer
   * 
   * @return Completes&lt;EventStreamReader&lt;T&gt;&gt;
   */
  Completes<EventStreamReader<T>> eventStreamReader(final String name);
}
