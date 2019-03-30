// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import io.vlingo.common.Outcome;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;

/**
 * The basic State Store interface, defining standard dispatching and control types.
 */
public interface StateStore extends StateStoreReader, StateStoreWriter {
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
   * Defines the means to communicate the confirmation of a previously
   * dispatched storage {@code State<?>} results to the receiver.
   */
  public static interface ConfirmDispatchedResultInterest {
    /**
     * Sends the confirmation of a dispatched {@code State<?>}.
     * @param result the {@code Result} of the dispatch confirmation
     * @param dispatchId the String unique identity of the dispatched {@code State<?>}
     */
    void confirmDispatchedResultedIn(final Result result, final String dispatchId);
  }

  /**
   * Defines the means to confirm previously dispatched results, and to
   * re-dispatch those that have not been successfully confirmed.
   */
  public static interface DispatcherControl {
    /**
     * Confirm that the {@code dispatchId} has been dispatched.
     * @param dispatchId the String unique identity of the dispatched state
     * @param interest the ConfirmDispatchedResultInterest
     */
    void confirmDispatched(final String dispatchId, final ConfirmDispatchedResultInterest interest);

    /**
     * Attempt to dispatch any unconfirmed dispatchables.
     */
    void dispatchUnconfirmed();

    /**
     * Stop attempting to dispatch unconfirmed dispatchables.
     */
    void stop();
  }

  /**
   * Defines the data holder for identity and state that has been
   * successfully stored and is then dispatched to registered
   * interests.
   *
   * @param <R> the concrete {@code State<?>} type of the storage
   */
  public static class Dispatchable<R extends State<?>> {
    /**
     * My String unique identity.
     */
    public final String id;

    /**
     * The moment when I was persistently created.
     */
    public final LocalDateTime createdAt;

    /**
     * My R concrete {@code State<?>} type.
     */
    public final R state;

    /**
     * Constructs my state.
     * @param id the String unique identity
     * @param createdAt the persistence creation timestamp
     * @param state the R concrete {@code State<?>} type
     */
    public Dispatchable(final String id, final LocalDateTime createdAt, final R state) {
      this.id = id;
      this.createdAt = createdAt;
      this.state = state;
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

    /**
     * See equals(Object).
     * @return boolean
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object other) {
      return this.id.equals(((Dispatchable<R>) other).id);
    }
  }

  /**
   * Defines the support for dispatching binary, object, and text state.
   */
  public static interface Dispatcher {
    /**
     * Register the {@code control} with the receiver.
     * @param control the DispatcherControl to register
     */
    void controlWith(final DispatcherControl control);

    /**
     * Dispatch the {@code state} with the uniquely assigned {@code dispatchId}.
     * @param dispatchId the String id assigned to this dispatch
     * @param state the {@code S} state to dispatch
     * @param <S> the type of {@code State<?>}, being BinaryState, ObjectState, or TextState
     */
    default <S extends State<?>> void dispatch(final String dispatchId, final S state) {
      dispatch(dispatchId, state, Source.none());
    }

    /**
     * Dispatch the {@code source} with the uniquely assigned {@code dispatchId}.
     * @param dispatchId the String id assigned to this dispatch
     * @param state the {@code BinaryState} to dispatch
     * @param sources the {@code List<Source<?>>} to dispatch
     * @param <S> the type of {@code State<?>}, being BinaryState, ObjectState, or TextState
     * @param <C> the type of {@code Source<?>}
     */
    <S extends State<?>, C extends Source<?>> void dispatch(final String dispatchId, final S state, final Collection<C> sources);
  }

  /**
   * Defines the interface through which basic abstract storage implementations
   * delegate to the technical implementations. See any of the existing concrete
   * implementations for details, such as the Postgres or HSQL found in component
   * {@code vlingo-symbio-jdbc}.
   */
  public static interface StorageDelegate {
    <S extends State<?>> Collection<Dispatchable<S>> allUnconfirmedDispatchableStates() throws Exception;
    void beginRead() throws Exception;
    void beginWrite() throws Exception;
    void close();
    boolean isClosed();
    void complete() throws Exception;
    void confirmDispatched(final String dispatchId);
    <C> C connection();
    <W,S> W dispatchableWriteExpressionFor(final String dispatchId, final State<S> state) throws Exception;
    void fail();
    String originatorId();
    <R> R readExpressionFor(final String storeName, final String id) throws Exception;
    <S> S session() throws Exception;
    <S,R> S stateFrom(final R result, final String id) throws Exception;
    <W,S> W writeExpressionFor(final String storeName, final State<S> state) throws Exception;
  }
}
