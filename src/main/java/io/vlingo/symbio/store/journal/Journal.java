// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal;

import java.util.List;
import java.util.Optional;

import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapter;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;

/**
 * The top-level journal used within a Bounded Context (microservice) to store all of
 * its {@code Entry<T>} instances for EventSourced and CommandSourced components. Each use of
 * the journal appends some number of {@code Entry<T>} instances and perhaps a single snapshot {@code State<T>}.
 * The journal may also be queried for a {@code JournalReader<T>} and a {@code StreamReader<T>}.
 * Assuming that all successfully appended {@code Entry<T>} instances should be dispatched in some way
 * after each write transaction, you should register an {@code StreamJournalListener<T>} when first
 * creating your {@code StreamJournal<T>}.
 *
 * @param <T> the concrete type of {@code Entry<T>} stored, which maybe be String, byte[], or Object
 */
public interface Journal<T> {

  /**
   * The means by which the {@code StreamJournal<T>} informs the sender of the result of any given append.
   * @param <T> the concrete type of {@code AppendResultInterest<T>} informed of a given append result
   */
  public static interface AppendResultInterest<T> {
    /**
     * Conveys the {@code outcome} of a single appended {@code Source<S>} and a possible state {@code snapshot}.
     * @param outcome the {@code Outcome<StorageException,Result>} either failure or success
     * @param streamName the String name of the stream appended
     * @param streamVersion the int version of the stream appended
     * @param source the {@code Source<S>} that was appended
     * @param snapshot the possible {@code Optional<State<T>>} that may have persisted as the stream's most recent snapshot
     * @param object the Object supplied by the sender to be sent back in this result
     * @param <S> the Source type
     */
    <S> void appendResultedIn(final Outcome<StorageException,Result> outcome, final String streamName, final int streamVersion, final Source<S> source, final Optional<State<T>> snapshot, final Object object);

    /**
     * Conveys the {@code outcome} of attempting to append multiple {@code Source<?>} instances and a possible state {@code snapshot}.
     * @param outcome the {@code Outcome<StorageException,Result>} either failure or success
     * @param streamName the String name of the stream failing the append
     * @param streamVersion the int version of the stream failing the append
     * @param sources the {@code List<Source<?>>} that was appended
     * @param snapshot the possible {@code Optional<State<T>>} that may have persisted as the stream's most recent snapshot
     * @param object the Object supplied by the sender to be sent back in this result
     * @param <S> the Source type
     */
    <S> void appendAllResultedIn(final Outcome<StorageException,Result> outcome, final String streamName, final int streamVersion, final List<Source<S>> sources, final Optional<State<T>> snapshot, final Object object);
  }

  /**
   * Appends the single {@code Source<?>} as an {@code Entry<T>} to the end of the journal
   * creating an association to {@code streamName} with {@code streamVersion}. The {@code Source<?>}
   * is translated to a corresponding {@code Entry<T>} with an unknown id. If there is a registered
   * {@code JournalListener<T>}, it will be informed of the newly appended {@code Entry<T>} and it
   * will have an assigned valid id.
   * 
   * @param streamName the String name of the stream to append
   * @param streamVersion the int version of the stream to append
   * @param source the {@code Source<?>} to append as an {@code Entry<T>}
   * @param interest the Actor-backed {@code AppendResultInterest<T>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<T>>} response
   * @param <S> the Source type
   */
  <S> void append(final String streamName, final int streamVersion, final Source<S> source, final AppendResultInterest<T> interest, final Object object);

  /**
   * Appends the single {@code Source<?>} as an {@code Entry<T>} to the end of the journal
   * creating an association to {@code streamName} with {@code streamVersion}, and storing
   * the full state {@code snapshot}. The corresponding {@code Entry<T>} is internally
   * assigned an id. The entry and snapshot are consistently persisted together or neither
   * at all. If there is a registered {@code JournalListener<T>}, it will be informed of
   * the newly appended {@code Entry<T>} with an assigned valid id and the new {@code State<T>} snapshot.
   * 
   * @param streamName the String name of the stream to append
   * @param streamVersion the int version of the stream to append
   * @param source the {@code Source<?>} to append as an {@code Entry<T>}
   * @param snapshot the {@code State<T>} state constituting the current state of the stream as of entry
   * @param interest the Actor-backed {@code AppendResultInterest<T>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<T>>} response
   * @param <S> the Source type
   */
  <S> void appendWith(final String streamName, final int streamVersion, final Source<S> source, final State<T> snapshot, final AppendResultInterest<T> interest, final Object object);

  /**
   * Appends all {@code Source<?>} instances as {@code Entry<T>} instances to the end of the
   * journal creating an association to {@code streamName} with {@code streamVersion}. If there is
   * a registered {@code JournalListener<T>}, it will be informed of the newly appended {@code Entry<T>}
   * instances and each will have an assigned valid id.
   * 
   * @param streamName the String name of the stream to append
   * @param fromStreamVersion the int version of the stream to start appending, and increasing for each of entries
   * @param sources the {@code List<Source<?>>} to append as {@code List<Entry<T>>} instances
   * @param interest the Actor-backed {@code AppendResultInterest<T>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<T>>} response
   * @param <S> the Source type
   */
  <S> void appendAll(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final AppendResultInterest<T> interest, final Object object);

  /**
   * Appends all {@code Source<?>} instances as {@code Entry<T>} instances to the end of the
   * journal creating an association to {@code streamName} with {@code streamVersion}, and storing
   * the full state {@code snapshot}. The entries and snapshot are consistently persisted together
   * or none at all. If there is a registered {@code JournalListener<T>}, it will be informed of
   * the newly appended {@code Entry<T>} instances with assigned valid ids, and the {@code snapshot}.
   * 
   * @param streamName the String name of the stream to append
   * @param fromStreamVersion the int version of the stream to start appending, and increasing for each of entries
   * @param sources the {@code List<Source<T>>} to append as {@code List<Entry<T>>} instances
   * @param snapshot the {@code State<T>} state constituting the current state of the stream as of the last of entries
   * @param interest the Actor-backed {@code AppendResultInterest<T>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<T>>} response
   * @param <S> the Source type
   */
  <S> void appendAllWith(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final State<T> snapshot, final AppendResultInterest<T> interest, final Object object);

  /**
   * Eventually answers the {@code JournalReader<T>} named name for this journal. If
   * the reader named name does not yet exist, it is first created. Readers
   * with different names enables reading from different positions and for different
   * reasons. For example, some readers may be interested in publishing {@code Entry<T>}
   * instances messaging while others may be projecting and building pipelines
   * of new streams.
   * 
   * @param name the String name of the {@code JournalReader<T>} to answer
   * 
   * @return {@code Completes<JournalReader<T>>}
   */
  Completes<JournalReader<T>> journalReader(final String name);

  /**
   * Eventually answers the {@code StreamReader<T>} named {@code name} for this journal. If
   * the reader named name does not yet exist, it is first created. Readers
   * with different names enables reading from different streams and for different
   * reasons. For example, some streams may be very busy while others are not.
   * 
   * @param name the String name of the {@code StreamReader<T>} to answer
   * 
   * @return {@code Completes<StreamReader<T>>}
   */
  Completes<StreamReader<T>> streamReader(final String name);

  /**
   * Registers the {@code adapter} with the journal for {@code sourceType}.
   * @param sourceType the {@code Class<S>} for which to register the adapter
   * @param adapter the {@code EntryAdapter<S,E>} used for {@code sourceType}
   * @param <S> the source type
   * @param <E> the Entry type
   */
  public <S extends Source<?>,E extends Entry<?>> void registerAdapter(final Class<S> sourceType, final EntryAdapter<S,E> adapter);

  /**
   * The binary journal as type {@code Journal<byte[]>}.
   */
  public static interface BinaryJournal extends Journal<byte[]> { }

  /**
   * The object journal as type {@code Journal<Object>}.
   */
  public static interface ObjectJournal extends Journal<Object> { }

  /**
   * The text journal as type {@code Journal<String>}.
   */
  public static interface TextJournal extends Journal<String> { }
}
