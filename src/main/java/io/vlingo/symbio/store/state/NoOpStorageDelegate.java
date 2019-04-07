// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.Collection;

import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.EntryReader.Advice;
import io.vlingo.symbio.store.state.StateStore.Dispatchable;
import io.vlingo.symbio.store.state.StateStore.StorageDelegate;

public class NoOpStorageDelegate implements StorageDelegate {
  @Override public <S extends State<?>> Collection<Dispatchable<S>> allUnconfirmedDispatchableStates() { return null; }

  @Override public void beginRead() {  }

  @Override public void beginWrite() {  }

  @Override public void close() { }

  @Override public boolean isClosed() { return true; }

  @Override public void complete() { }

  @Override public void confirmDispatched(final String dispatchId) { }

  @Override public <C> C connection() { return null; }

  @Override public <W, S> W dispatchableWriteExpressionFor(final String dispatchId, final State<S> state) { return null; }

  @Override public <A, E> A appendExpressionFor(final Entry<E> entry) throws Exception { return null; }

  @Override public <A> A appendIdentityExpression() { return null; }

  @Override public Advice entryReaderAdvice() { return null; }

  @Override public void fail() { }

  @Override public String originatorId() { return null; }

  @Override public <R> R readExpressionFor(final String storeName, final String id) { return null; }

  @Override public <S> S session() { return null; }

  @Override public <S,R> S stateFrom(final R result, final String id) { return null; }

  @Override public <W,S> W writeExpressionFor(final String storeName, final State<S> state) { return null; }
}
