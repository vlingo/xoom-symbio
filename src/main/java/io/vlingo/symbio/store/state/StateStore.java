// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.time.LocalDateTime;
import java.util.Collection;

import io.vlingo.common.Outcome;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.StateAdapter;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;

/**
 * The basic State Store interface, defining standard dispatching and control types.
 */
public interface StateStore {
  /**
   * Read the state identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param type the {@code Class<?>} type of the state to read
   * @param interest the ReadResultInterest to which the result is dispatched
   */
  void read(final String id, final Class<?> type, final ReadResultInterest interest);

  /**
   * Read the state identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param type the {@code Class<?>} type of the state to read
   * @param interest the ReadResultInterest to which the result is dispatched
   * @param object an Object that will be sent to the ReadResultInterest when the read has succeeded or failed
   */
  void read(final String id, final Class<?> type, final ReadResultInterest interest, final Object object);

  /**
   * Write the {@code state} identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param <S> the concrete type of the state
   */
  <S> void write(final String id, final S state, final int stateVersion, final WriteResultInterest interest);

  /**
   * Write the {@code state} identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param metadata the Metadata for the state
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param <S> the concrete type of the state
   */
  <S> void write(final String id, final S state, final int stateVersion, final Metadata metadata, final WriteResultInterest interest);

  /**
   * Write the {@code state} identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param object an Object that will be sent to the WriteResultInterest when the write has succeeded or failed
   * @param <S> the concrete type of the state
   */
  <S> void write(final String id, final S state, final int stateVersion, final WriteResultInterest interest, final Object object);


  /**
   * Write the {@code state} identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param metadata the Metadata for the state
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param object an Object that will be sent to the WriteResultInterest when the write has succeeded or failed
   * @param <S> the concrete type of the state
   */
  <S> void write(final String id, final S state, final int stateVersion, final Metadata metadata, final WriteResultInterest interest, final Object object);

  /**
   * Registers the {@code adapter} with the {@code StateStore} for {@code stateType}.
   * @param stateType the {@code Class<S>} for which to register the adapter
   * @param adapter the {@code StateAdapter<S,R>} used to adapt from state type {@code S} to raw type {@code R}
   * @param <S> the type of the natural state
   * @param <R> the raw {@code State<?>} type
   */
  public <S,R extends State<?>> void registerAdapter(final Class<S> stateType, final StateAdapter<S,R> adapter);

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
     * @param object the Object passed to write() that is sent back to the receiver
     * @param <S> the native state type
     */
    <S> void writeResultedIn(final Outcome<StorageException,Result> outcome, final String id, final S state, final int stateVersion, final Object object);
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
    void dispatchUnconfirmed();
  }
  
  public static interface RedispatchControl {
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
     * @param state the {@code BinaryState} to dispatch
     * @param <S> the type of {@code State<?>}, being BinaryState, ObjectState, or TextState
     */
    <S extends State<?>> void dispatch(final String dispatchId, final S state);
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
