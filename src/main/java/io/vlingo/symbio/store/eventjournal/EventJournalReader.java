// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.eventjournal;

import io.vlingo.common.Completes;
import io.vlingo.symbio.Event;

/**
 * The reader for a given EventJournal&lt;T&gt;, which is provided by its eventJournalReader().
 * This reads sequentially over all Event&lt;T&gt; instances in the entire journal, from the
 * first written Event&lt;T&gt; to the current last written Event&lt;T&gt;, and is prepared to read
 * all newly appended Event&lt;T&gt; instances beyond that point when they become available.
 * 
 * @param <T> the concrete type of Event&lt;T&gt; stored and read, which maybe be String, byte[], or Object
 */
public interface EventJournalReader<T> {

  /**
   * A means to seek to the first id position of the journal. This constant
   * must be honored by all journal implementations regardless of internal
   * id type.
   */
  static final String Beginning = "<";

  /**
   * A means to seek past the last id position of the journal. This constant
   * must be honored by all journal implementations regardless of internal
   * id type.
   */
  static final String End = ">";

  /**
   * A means to query the current id position of the journal without repositioning.
   * This constant must be honored by all journal implementations regardless of
   * internal id type.
   */
  static final String Query = "=";

  /**
   * Eventually answers the name of this reader.
   * 
   * @return the Completes&lt;String&gt; reader's name
   */
  Completes<String> name();

  /**
   * Eventually answers the next available Event&lt;T&gt; instance or null if none is currently available.
   * The next Event&lt;T&gt; instance is relative to the one previously read by the same reader
   * instance, or the first Event&lt;T&gt; instance in the EventJournal&lt;T&gt; if none have previously
   * been read. Note that this is the least efficient read, because only one Event&lt;T&gt; will
   * be answered, but it may be useful for test purposes or an EventJournal&lt;T&gt; that is
   * appended to slowly.
   * 
   * @return the Completes&lt;Event&lt;T&gt;&gt; next available event or null if none
   */
  Completes<Event<T>> readNext();

  /**
   * Eventually answers the next available Event&lt;T&gt; instances by means of an EventStream&lt;T&gt;, which may be
   * empty if none are currently available. The next Event&lt;T&gt; instances are relative to the one(s)
   * previously read by the same reader instance, or the first Event&lt;T&gt; instance in the EventJournal&lt;T&gt;
   * if none have previously been read. Note that this is the most efficient read, because up to
   * maximumEvents Event&lt;T&gt; instances will be answered. The maximumEvents should be used to indicate
   * the total number of Event&lt;T&gt; instances that can be consumed in a timely fashion by the sender,
   * which is a natural back-pressure mechanism.
   * 
   * @param maximumEvents the int indicating the maximum number of Event&lt;T&gt; instances to read
   * 
   * @return the Completes&lt;EventStream&lt;T&gt;&gt; of at most maximumEvents or empty if none
   */
  Completes<EventStream<T>> readNext(final int maximumEvents);

  /**
   * Rewinds the reader so that the next available Event&lt;T&gt; is the first one in the EventJournal&lt;T&gt;.
   * Sending rewind() is the same as sending seekTo(Beginning).
   */
  void rewind();

  /**
   * Eventually answers the new position of the reader after attempting to seek to the Event&lt;T&gt; of the
   * given id, such that the next available Event&lt;T&gt; is the one of the given id. If the id does not (yet)
   * exist, the position is set to just beyond the last Event&lt;T&gt; instance in the journal (see End), or to
   * the first (see Beginning) if none currently exist. For example, if the EventJournal&lt;T&gt; id type is a
   * long, passing "-1" will cause the position to be set just beyond the last Event&lt;T&gt; instance in
   * the journal, or to the beginning if no instances exist. Passing the String "=" (see Query)
   * answers the current id position without attempting to seek in either direction. (Seeking relative
   * to the current id position is implementation specific and is not expected to be supported by any
   * given implementation.)
   * 
   * @param id the String id of the Event&lt;T&gt; instance to which the seek prepares to next read
   * 
   * @return Completes&lt;String&gt;
   */
  Completes<String> seekTo(final String id);
}
