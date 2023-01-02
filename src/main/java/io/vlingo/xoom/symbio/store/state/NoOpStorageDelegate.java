// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state;

import io.vlingo.xoom.symbio.store.EntryReader.Advice;
import io.vlingo.xoom.symbio.store.state.StateStore.StorageDelegate;

public class NoOpStorageDelegate implements StorageDelegate {
  @Override
  public void close() {
  }

  @Override
  public StorageDelegate copy() { return new NoOpStorageDelegate(); }

  @Override
  public boolean isClosed() {
    return false;
  }

  @Override
  public Advice entryReaderAdvice() {
    return null;
  }

  @Override
  public void initialize() {

  }

  @Override
  public String originatorId() {
    return null;
  }

  @Override
  public <S, R> S stateFrom(final R result, final String id) {
    return null;
  }

  @Override
  public <S, R> S stateFrom(final R result, final String id, final int columnOffset) throws Exception {
    return null;
  }
}
