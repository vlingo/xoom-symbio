// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.Collection;

import io.vlingo.symbio.State;
import io.vlingo.symbio.State.BinaryState;
import io.vlingo.symbio.State.TextState;

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

  public enum Result {
    ConcurrentyViolation {
      @Override public boolean isConcurrentyViolation() { return true; }
    },
    Failure {
      @Override public boolean isFailure() { return true; }
    },
    NotFound {
      @Override public boolean isNotFound() { return true; }
    },
    NoTypeStore {
      @Override public boolean isNoTypeStore() { return true; }
    },
    Success {
      @Override public boolean isSuccess() { return true; }
    };

    public boolean isConcurrentyViolation() { return false; }
    public boolean isFailure() { return false; }
    public boolean isNotFound() { return false; }
    public boolean isNoTypeStore() { return false; }
    public boolean isSuccess() { return false; }
  }

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
    default <T> void dispatch(final String dispatchId, final BinaryState state) { }
    default <T> void dispatch(final String dispatchId, final TextState state) { }
  }

  public static interface ReadResultInterest<T> {
    void readResultedIn(final Result result, final String id, final State<T> state);
  }

  public static interface WriteResultInterest<T> {
    void writeResultedIn(final Result result, final String id, final State<T> state);
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
