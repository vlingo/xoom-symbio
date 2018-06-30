// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore.StorageDelegate;

public class NoOpStorageDelegate implements StorageDelegate {
  @Override public void beginRead() {  }

  @Override public void beginWrite() {  }

  @Override public void close() { }

  @Override public void complete() { }

  @Override public <C> C connection() { return null; }

  @Override public void drop(final String storeName) { }

  @Override public void dropAll() { }

  @Override public void fail() { }

  @Override public <R> R readExpressionFor(final String storeName, final String id) { return null; }

  @Override public <S> S session() { return null; }

  @Override public <S,R> S stateFrom(final R result, final String id) { return null; }

  @Override public <W,S> W writeExpressionFor(final String storeName, final State<S> state) { return null; }
}
