// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.EntryReader;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.dispatch.Dispatchable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * The basic State Store interface, defining standard dispatching and control types.
 */
public interface StateStore extends StateStoreReader, StateStoreWriter {
  /**
   * Answer the {@code StateStoreEntriesReader<ET>} identified by the {@code name}.
   * @param name the String name of the reader
   * @param <ET> the specific type of {@code Entry<?>} that will be read
   * @return {@code Completes<StateStoreEntriesReader<ET>>}
   */
  public <ET extends Entry<?>> Completes<StateStoreEntryReader<ET>> entryReader(final String name);

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
   * Defines the data holder for identity and state that has been
   * successfully stored and is then dispatched to registered
   * interests.
   *
   * @param <R> the concrete {@code State<?>} type of the storage
   */
   class StateDispatchable<R extends State<?>> extends Dispatchable {

    /**
     * My R concrete {@code State<?>} type.
     */
    private final R state;
    
    /**
     * My {@code List<Entry<?>>} to dispatch
     */
    private final List<Entry<?>> entries;

    /**
     * Constructs my state.
     * @param id the String unique identity
     * @param createdAt the persistence creation timestamp
     * @param state the R concrete {@code State<?>} type
     * @param entries the {@code List<Entry<?>>} to dispatch
     */
    public StateDispatchable(final String id, final LocalDateTime createdAt, final R state, List<Entry<?>> entries) {
      super(id, createdAt);
      this.state = state;
      this.entries = entries;
    }

    public R getState() {
      return state;
    }

    public List<Entry<?>> getEntries() {
      return entries;
    }

    /**
     * Answer the state as an instance of specific type {@code State<S>}.
     * @return {@code State<S>}
     * @param <S> the type of the state, String or byte[]
     */
    @SuppressWarnings("unchecked")
    public <S> State<S> typedState() {
      return (State<S>) state;
    }
  }

  /**
   * Defines the interface through which basic abstract storage implementations
   * delegate to the technical implementations. See any of the existing concrete
   * implementations for details, such as the Postgres or HSQL found in component
   * {@code vlingo-symbio-jdbc}.
   */
  public static interface StorageDelegate {
    <S extends State<?>> Collection<StateDispatchable<S>> allUnconfirmedDispatchableStates() throws Exception;
    <A,E> A appendExpressionFor(final Entry<E> entry) throws Exception;
    <A> A appendIdentityExpression();
    void beginRead() throws Exception;
    void beginWrite() throws Exception;
    void close();
    boolean isClosed();
    void complete() throws Exception;
    void confirmDispatched(final String dispatchId);
    <C> C connection();
    <W,S> W dispatchableWriteExpressionFor(final String dispatchId, final State<S> state) throws Exception;
    EntryReader.Advice entryReaderAdvice();
    void fail();
    String originatorId();
    <R> R readExpressionFor(final String storeName, final String id) throws Exception;
    <S> S session() throws Exception;
    <S,R> S stateFrom(final R result, final String id) throws Exception;
    <W,S> W writeExpressionFor(final String storeName, final State<S> state) throws Exception;
  }
}
