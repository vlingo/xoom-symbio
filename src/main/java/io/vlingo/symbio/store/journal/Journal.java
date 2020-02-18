// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal;

import java.util.List;
import java.util.Optional;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;

/**
 * The top-level journal used within a Bounded Context (microservice) to store all of
 * its {@code Entry<T>} instances for {@code EventSourced} and {@code CommandSourced} components. Each use of
 * the journal appends some number of {@code Entry<T>} instances and perhaps a single snapshot {@code State<ST>}.
 * The journal may also be queried for a {@code JournalReader<T>} and a {@code StreamReader<T>}.
 * Assuming that all successfully appended {@code Entry<T>} instances should be dispatched in some way
 * after each write transaction, you should register an {@code StreamJournalListener<T>} when first
 * creating your {@code StreamJournal<T>}.
 *
 * @param <T> the concrete type of {@code Entry<T>} and {@code State<?>} stored, which maybe be String, byte[], or Object
 */
public interface Journal<T> {
  static final long DefaultCheckConfirmationExpirationInterval = 1000;
  static final long DefaultConfirmationExpiration = 1000;

  /**
   * Answer a new {@code Journal<T>}
   * @param stage the Stage within which the {@code Journal<T>} is created
   * @param implementor the {@code Class<A>} of the implementor
   * @param dispatchers the {@code List<Dispatcher<T>>}
   * @param additional the Object[] of additional parameters
   * @param <A> the concrete type of the Actor implementing the {@code Journal<T>} protocol
   * @param <T> the concrete type of {@code Entry<T>} stored and read, which maybe be String, byte[], or Object
   * @param <RS> the raw snapshot state type
   * @return {@code Journal<T>}
   */
  @SuppressWarnings("unchecked")
  static <A extends Actor, T, RS extends State<?>> Journal<T> using(final Stage stage, final Class<A> implementor,
          final List<Dispatcher<Dispatchable<Entry<T>,RS>>> dispatchers, final Object...additional) {
    return additional.length == 0 ?
             stage.actorFor(Journal.class, implementor, dispatchers) :
             stage.actorFor(Journal.class, implementor, dispatchers, additional);
  }

  /**
   * Answer a new {@code Journal<T>}
   * @param stage the Stage within which the {@code Journal<T>} is created
   * @param implementor the {@code Class<A>} of the implementor
   * @param dispatcher the {@code Dispatcher<T>}
   * @param additional the Object[] of additional parameters
   * @param <A> the concrete type of the Actor implementing the {@code Journal<T>} protocol
   * @param <T> the concrete type of {@code Entry<T>} stored and read, which maybe be String, byte[], or Object
   * @param <RS> the raw snapshot state type
   * @return {@code Journal<T>}
   */
  @SuppressWarnings("unchecked")
  static <A extends Actor, T, RS extends State<?>> Journal<T> using(final Stage stage, final Class<A> implementor,
          final Dispatcher<Dispatchable<Entry<T>,RS>> dispatcher, final Object...additional) {
    return additional.length == 0 ?
             stage.actorFor(Journal.class, implementor, dispatcher) :
             stage.actorFor(Journal.class, implementor, dispatcher, additional);
  }

  /**
   * The means by which the {@code Journal<T>} informs the sender of the result of any given append.
   */
  public static interface AppendResultInterest {
    /**
     * Conveys the {@code outcome} of a single appended {@code Source<S>} and a possible state {@code snapshot}.
     * @param outcome the {@code Outcome<StorageException,Result>} either failure or success
     * @param streamName the String name of the stream appended
     * @param streamVersion the int version of the stream appended
     * @param source the {@code Source<S>} that was appended
     * @param snapshot the possible {@code Optional<ST>} that may have persisted as the stream's most recent snapshot
     * @param object the Object supplied by the sender to be sent back in this result
     * @param <S> the Source type
     * @param <ST> the snapshot state type
     */
    <S,ST> void appendResultedIn(final Outcome<StorageException,Result> outcome, final String streamName, final int streamVersion, final Source<S> source, final Optional<ST> snapshot, final Object object);

    /**
     * Conveys the {@code outcome} of a single appended {@code Source<S>} and a possible state {@code snapshot}.
     * @param outcome the {@code Outcome<StorageException,Result>} either failure or success
     * @param streamName the String name of the stream appended
     * @param streamVersion the int version of the stream appended
     * @param source the {@code Source<S>} that was appended
     * @param metadata the Metadata associated with the Source
     * @param snapshot the possible {@code Optional<ST>} that may have persisted as the stream's most recent snapshot
     * @param object the Object supplied by the sender to be sent back in this result
     * @param <S> the Source type
     * @param <ST> the snapshot state type
     */
    <S,ST> void appendResultedIn(final Outcome<StorageException,Result> outcome, final String streamName, final int streamVersion, final Source<S> source, final Metadata metadata, final Optional<ST> snapshot, final Object object);

    /**
     * Conveys the {@code outcome} of attempting to append multiple {@code Source<S>} instances and a possible state {@code snapshot}.
     * @param outcome the {@code Outcome<StorageException,Result>} either failure or success
     * @param streamName the String name of the stream failing the append
     * @param streamVersion the int version of the stream failing the append
     * @param sources the {@code List<Source<S>>} that was appended
     * @param snapshot the possible {@code Optional<ST>} that may have persisted as the stream's most recent snapshot
     * @param object the Object supplied by the sender to be sent back in this result
     * @param <S> the Source type
     * @param <ST> the snapshot state type
     */
    <S,ST> void appendAllResultedIn(final Outcome<StorageException,Result> outcome, final String streamName, final int streamVersion, final List<Source<S>> sources, final Optional<ST> snapshot, final Object object);

    /**
     * Conveys the {@code outcome} of attempting to append multiple {@code Source<S>} instances and a possible state {@code snapshot}.
     * @param outcome the {@code Outcome<StorageException,Result>} either failure or success
     * @param streamName the String name of the stream failing the append
     * @param streamVersion the int version of the stream failing the append
     * @param sources the {@code List<Source<S>>} that was appended
     * @param metadata the Metadata associated with the Source
     * @param snapshot the possible {@code Optional<ST>} that may have persisted as the stream's most recent snapshot
     * @param object the Object supplied by the sender to be sent back in this result
     * @param <S> the Source type
     * @param <ST> the snapshot state type
     */
    <S,ST> void appendAllResultedIn(final Outcome<StorageException,Result> outcome, final String streamName, final int streamVersion, final List<Source<S>> sources, final Metadata metadata, final Optional<ST> snapshot, final Object object);
  }

  /**
   * Appends the single {@code Source<S>} as an {@code Entry<T>} to the end of the journal
   * creating an association to {@code streamName} with {@code streamVersion}. The {@code Source<S>}
   * is translated to a corresponding {@code Entry<T>} with an unknown id. If there is a registered
   * {@code JournalListener<T>}, it will be informed of the newly appended {@code Entry<T>} and it
   * will have an assigned valid id.
   *
   * @param streamName the String name of the stream to append
   * @param streamVersion the int version of the stream to append
   * @param source the {@code Source<S>} to append as an {@code Entry<T>}
   * @param interest the Actor-backed {@code AppendResultInterest<ST>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<ST>>} response
   * @param <S> the Source type
   * @param <ST> the snapshot state type
   */
  default <S,ST> void append(final String streamName, final int streamVersion, final Source<S> source, final AppendResultInterest interest, final Object object) {
    append(streamName, streamVersion, source, Metadata.nullMetadata(), interest, object);
  }

  /**
   * Appends the single {@code Source<S>} as an {@code Entry<T>} to the end of the journal
   * creating an association to {@code streamName} with {@code streamVersion}. The {@code Source<S>}
   * is translated to a corresponding {@code Entry<T>} with an unknown id. If there is a registered
   * {@code JournalListener<T>}, it will be informed of the newly appended {@code Entry<T>} and it
   * will have an assigned valid id.
   *
   * @param streamName the String name of the stream to append
   * @param streamVersion the int version of the stream to append
   * @param source the {@code Source<S>} to append as an {@code Entry<T>}
   * @param metadata the Metadata associated with the Source
   * @param interest the Actor-backed {@code AppendResultInterest<ST>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<ST>>} response
   * @param <S> the Source type
   * @param <ST> the snapshot state type
   */
  <S,ST> void append(final String streamName, final int streamVersion, final Source<S> source, final Metadata metadata, final AppendResultInterest interest, final Object object);

  /**
   * Appends the single {@code Source<S>} as an {@code Entry<T>} to the end of the journal
   * creating an association to {@code streamName} with {@code streamVersion}, and storing
   * the full state {@code snapshot}. The corresponding {@code Entry<T>} is internally
   * assigned an id. The entry and snapshot are consistently persisted together or neither
   * at all. If there is a registered {@code JournalListener<T>}, it will be informed of
   * the newly appended {@code Entry<T>} with an assigned valid id and the new {@code ST} snapshot.
   *
   * @param streamName the String name of the stream to append
   * @param streamVersion the int version of the stream to append
   * @param source the {@code Source<S>} to append as an {@code Entry<T>}
   * @param snapshot the current {@code ST} state of the stream before the source is applied
   * @param interest the Actor-backed {@code AppendResultInterest<ST>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<ST>>} response
   * @param <S> the Source type
   * @param <ST> the snapshot state type
   */
  default <S,ST> void appendWith(final String streamName, final int streamVersion, final Source<S> source, final ST snapshot, final AppendResultInterest interest, final Object object) {
    appendWith(streamName, streamVersion, source, Metadata.nullMetadata(), snapshot, interest, object);
  }

  /**
   * Appends the single {@code Source<S>} as an {@code Entry<T>} to the end of the journal
   * creating an association to {@code streamName} with {@code streamVersion}, and storing
   * the full state {@code snapshot}. The corresponding {@code Entry<T>} is internally
   * assigned an id. The entry and snapshot are consistently persisted together or neither
   * at all. If there is a registered {@code JournalListener<T>}, it will be informed of
   * the newly appended {@code Entry<T>} with an assigned valid id and the new {@code ST} snapshot.
   *
   * @param streamName the String name of the stream to append
   * @param streamVersion the int version of the stream to append
   * @param source the {@code Source<S>} to append as an {@code Entry<T>}
   * @param metadata the Metadata associated with the Source
   * @param snapshot the current {@code ST} state of the stream before the source is applied
   * @param interest the Actor-backed {@code AppendResultInterest<ST>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<ST>>} response
   * @param <S> the Source type
   * @param <ST> the snapshot state type
   */
  <S,ST> void appendWith(final String streamName, final int streamVersion, final Source<S> source, final Metadata metadata, final ST snapshot, final AppendResultInterest interest, final Object object);

  /**
   * Appends all {@code Source<S>} instances as {@code Entry<T>} instances to the end of the
   * journal creating an association to {@code streamName} with {@code streamVersion}. If there is
   * a registered {@code JournalListener<T>}, it will be informed of the newly appended {@code Entry<T>}
   * instances and each will have an assigned valid id.
   *
   * @param streamName the String name of the stream to append
   * @param fromStreamVersion the int version of the stream to start appending, and increasing for each of entries
   * @param sources the {@code List<Source<S>>} to append as {@code List<Entry<T>>} instances
   * @param interest the Actor-backed {@code AppendResultInterest<ST>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<ST>>} response
   * @param <S> the Source type
   * @param <ST> the snapshot state type
   */
  default <S,ST> void appendAll(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final AppendResultInterest interest, final Object object) {
    appendAll(streamName, fromStreamVersion, sources, Metadata.nullMetadata(), interest, object);
  }

  /**
   * Appends all {@code Source<S>} instances as {@code Entry<T>} instances to the end of the
   * journal creating an association to {@code streamName} with {@code streamVersion}. If there is
   * a registered {@code JournalListener<T>}, it will be informed of the newly appended {@code Entry<T>}
   * instances and each will have an assigned valid id.
   *
   * @param streamName the String name of the stream to append
   * @param fromStreamVersion the int version of the stream to start appending, and increasing for each of entries
   * @param sources the {@code List<Source<S>>} to append as {@code List<Entry<T>>} instances
   * @param metadata the Metadata associated with the Source
   * @param interest the Actor-backed {@code AppendResultInterest<ST>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<ST>>} response
   * @param <S> the Source type
   * @param <ST> the snapshot state type
   */
  <S,ST> void appendAll(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final Metadata metadata, final AppendResultInterest interest, final Object object);

  /**
   * Appends all {@code Source<S>} instances as {@code Entry<T>} instances to the end of the
   * journal creating an association to {@code streamName} with {@code streamVersion}, and storing
   * the full state {@code snapshot}. The entries and snapshot are consistently persisted together
   * or none at all. If there is a registered {@code JournalListener<T>}, it will be informed of
   * the newly appended {@code Entry<T>} instances with assigned valid ids, and the {@code snapshot}.
   *
   * @param streamName the String name of the stream to append
   * @param fromStreamVersion the int version of the stream to start appending, and increasing for each of entries
   * @param sources the {@code List<Source<S>>} to append as {@code List<Entry<T>>} instances
   * @param snapshot the current {@code ST} state of the stream before the sources are applied
   * @param interest the Actor-backed {@code AppendResultInterest<T>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<T>>} response
   * @param <S> the Source type
   * @param <ST> the concrete state type
   */
  default <S,ST> void appendAllWith(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final ST snapshot, final AppendResultInterest interest, final Object object) {
    appendAllWith(streamName, fromStreamVersion, sources, Metadata.nullMetadata(), snapshot, interest, object);
  }

  /**
   * Appends all {@code Source<S>} instances as {@code Entry<T>} instances to the end of the
   * journal creating an association to {@code streamName} with {@code streamVersion}, and storing
   * the full state {@code snapshot}. The entries and snapshot are consistently persisted together
   * or none at all. If there is a registered {@code JournalListener<T>}, it will be informed of
   * the newly appended {@code Entry<T>} instances with assigned valid ids, and the {@code snapshot}.
   *
   * @param streamName the String name of the stream to append
   * @param fromStreamVersion the int version of the stream to start appending, and increasing for each of entries
   * @param sources the {@code List<Source<S>>} to append as {@code List<Entry<T>>} instances
   * @param metadata the Metadata associated with the Source
   * @param snapshot the current {@code ST} state of the stream before the sources are applied
   * @param interest the Actor-backed {@code AppendResultInterest<T>>} used to convey the result of the append
   * @param object the Object from the sender that is to be included in the {@code AppendResultInterest<T>>} response
   * @param <S> the Source type
   * @param <ST> the concrete state type
   */
  <S,ST> void appendAllWith(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final Metadata metadata, final ST snapshot, final AppendResultInterest interest, final Object object);

  /**
   * Eventually answers the {@code JournalReader<ET>} named {@code name} for this journal. If
   * the reader named {@code name} does not yet exist, it is first created. Readers
   * with different names enables reading from different positions and for different
   * reasons. For example, some readers may be interested in publishing {@code Entry<T>}
   * instances messaging while others may be projecting and building pipelines
   * of new streams.
   *
   * @param name the String name of the {@code JournalReader<T>} to answer
   * @param <ET> the concrete type of {@code Entry<?} of the {@code JournalReader<ET>}
   * @return {@code Completes<JournalReader<T>>}
   */
  <ET extends Entry<?>> Completes<JournalReader<ET>> journalReader(final String name);

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
   * The binary journal as type {@code Journal<byte[],BinaryState>}.
   */
  public static interface BinaryJournal extends Journal<byte[]> { }

  /**
   * The object journal as type {@code Journal<Object,ObjectState<?>>}.
   */
  public static interface ObjectJournal extends Journal<Object> { }

  /**
   * The text journal as type {@code Journal<String,TextState>}.
   */
  public static interface TextJournal extends Journal<String> { }
}
