// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state;

import java.util.Collection;
import java.util.List;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.ActorInstantiator;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.EntryReader;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;

/**
 * The basic State Store interface, defining standard dispatching and control types.
 */
public interface StateStore extends StateStoreReader, StateStoreWriter {
  long DefaultCheckConfirmationExpirationInterval = 1000;
  long DefaultConfirmationExpiration = 1000;

  long DefaultTimeBetweenFlushWrites = 20;
  long DefaultBatchWriteCount = 200;
  /**
   * Answer the {@code StateStoreEntriesReader<ET>} identified by the {@code name}.
   * @param name the String name of the reader
   * @param <ET> the specific type of {@code Entry<?>} that will be read
   * @return {@code Completes<StateStoreEntriesReader<ET>>}
   */
  public <ET extends Entry<?>> Completes<StateStoreEntryReader<ET>> entryReader(final String name);

  /**
   * Used for writing and reading multiple states in one batch.
   */
  public static class TypedStateBundle {
    public final String id;
    public final Class<?> type;
    public final Object state;
    public final int stateVersion;
    public final Metadata metadata;

    public TypedStateBundle(final String id, final Class<?> type, final Object state, final int stateVersion, final Metadata metadata) {
      this.id = id;
      this.type = type;
      this.state = state;
      this.stateVersion = stateVersion;
      this.metadata = metadata;
    }

    public TypedStateBundle(final String id, final Object state, final int stateVersion, final Metadata metadata) {
      this(id, null, state, stateVersion, metadata);
    }

    public TypedStateBundle(final String id, final Class<?> type) {
      this(id, type, null, 0, null);
    }

    @SuppressWarnings("unchecked")
    public <T> T typedState() {
      return (T) state;
    }
  }

  /**
   * Defines the result of reading the state with the specific id to the store.
   */
  public static interface ReadResultInterest {
    /**
     * Implemented by the interest of a given State Store for read operation results.
     * @param outcome the {@code Outcome<StorageException,Result>} of the read
     * @param id the String unique identity of the state to read
     * @param state the S native state that was read, or the empty null if not found
     * @param stateVersion the int version of the state that was read, or -1 if not found
     * @param metadata the Metadata of the state that was read, or null if not found
     * @param object the Object passed to read() that is sent back to the receiver
     * @param <S> the native state type
     */
    <S> void readResultedIn(final Outcome<StorageException,Result> outcome, final String id, final S state, final int stateVersion, final Metadata metadata, final Object object);

    /**
     * Implemented by the interest of a given State Store for multi-read operation results.
     * <p>NOTE: In the cause of a partial multi-read failure, the results will contain the
     * {@code TypedStateBundle} instances that were read. In such cases the {@code outcome}
     * will have the {@code StorageException#Result.NotAllFound} set.
     * @param outcome the {@code Outcome<StorageException,Result>} of the read
     * @param bundles the {@code Collection<TypedStateBundle>} holding multi-read results
     * @param object the Object passed to read() that is sent back to the receiver
     * @param <S> the native state type
     */
    default <S> void readResultedIn(final Outcome<StorageException,Result> outcome, final Collection<TypedStateBundle> bundles, final Object object) {
      // defaulted to prevent required override by all consumers
    }
  }

  /**
   * Defines the result of writing to the store with the specific id and state.
   */
  public static interface WriteResultInterest {
    /**
     * Implemented by the interest of a given State Store for write operation results.
     * @param outcome the {@code Outcome<StorageException,Result>} of the write
     * @param id the String unique identity of the state to attempted write
     * @param state the S native state that was possibly written
     * @param stateVersion the int version of the state that was possibly written
     * @param sources the {@code List<Source<C>>} if any
     * @param object the Object passed to write() that is sent back to the receiver
     * @param <S> the native state type
     * @param <C> the native source type
     */
    <S,C> void writeResultedIn(final Outcome<StorageException,Result> outcome, final String id, final S state, final int stateVersion, final List<Source<C>> sources, final Object object);
  }

  /**
   * Defines the interface through which basic abstract storage implementations
   * delegate to the technical implementations. See any of the existing concrete
   * implementations for details, such as the Postgres or HSQL found in component
   * {@code xoom-symbio-jdbc}.
   */
  public static interface StorageDelegate {
    StorageDelegate copy();
    void close();
    boolean isClosed();
    EntryReader.Advice entryReaderAdvice();
    void initialize();
    default <A extends Actor> ActorInstantiator<A> instantiator() { return null; }
    String originatorId();
    <S,R> S stateFrom(final R result, final String id) throws Exception;
    <S,R> S stateFrom(final R result, final String id, final int columnOffset) throws Exception;
  }

  /**
   * Use the pre-initialized {@code StateStore} to prime
   * the initialization before it's attempted.
   */
  public static interface InitializationPrimer {
    /**
     * Received by the implementor just prior to initialization.
     * @param stateStore the pre-initialized StateStore
     */
    void prime(final StateStore stateStore);
  }
}
