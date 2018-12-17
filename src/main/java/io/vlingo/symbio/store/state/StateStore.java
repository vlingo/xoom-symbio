// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.Collection;

import io.vlingo.common.Outcome;
import io.vlingo.symbio.State;
import io.vlingo.symbio.State.BinaryState;
import io.vlingo.symbio.State.ObjectState;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;

public interface StateStore {
  public enum DataFormat {
    Binary {
      @Override public boolean isBinary() { return true; }
    },
    Text {
      @Override public boolean isText() { return true; }
    };

    public boolean isBinary() { return false; }
    public boolean isText() { return false; }
  };

  public static interface ConfirmDispatchedResultInterest {
    void confirmDispatchedResultedIn(final Result result, final String dispatchId);
  }

  public static interface DispatcherControl {
    void confirmDispatched(final String dispatchId, final ConfirmDispatchedResultInterest interest);
    void dispatchUnconfirmed();
  }

  public static class Dispatchable<T> {
    public final String id;
    public final State<T> state;

    public Dispatchable(final String id, final State<T> state) {
      this.id = id;
      this.state = state;
    }

    @SuppressWarnings("unchecked")
    public <S> State<S> typedState() {
      return (State<S>) state;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object other) {
      return this.id.equals(((Dispatchable<T>) other).id);
    }
  }

  public static interface Dispatcher {
    void controlWith(final DispatcherControl control);
    default void dispatch(final String dispatchId, final BinaryState state) { }
    default void dispatch(final String dispatchId, final ObjectState<?> state) { }
    default void dispatch(final String dispatchId, final TextState state) { }
  }

  public static interface ReadResultInterest<T> {
    void readResultedIn(final Outcome<StorageException,Result> outcome, final String id, final State<T> state, final Object object);
  }

  public static interface WriteResultInterest<T> {
    void writeResultedIn(final Outcome<StorageException,Result> outcome, final String id, final State<T> state, final Object object);
  }

  public static interface StorageDelegate {
    <S> Collection<Dispatchable<S>> allUnconfirmedDispatchableStates() throws Exception;
    void beginRead() throws Exception;
    void beginWrite() throws Exception;
    void close();
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
