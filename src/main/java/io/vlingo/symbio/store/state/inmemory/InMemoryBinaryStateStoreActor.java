// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.inmemory;

import io.vlingo.symbio.State;
import io.vlingo.symbio.State.NullState;
import io.vlingo.symbio.store.state.BinaryStateStore;
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;

public class InMemoryBinaryStateStoreActor extends InMemoryStateStoreActor<byte[]>
    implements BinaryStateStore, DispatcherControl {

  private final BinaryDispatcher dispatcher;

  protected InMemoryBinaryStateStoreActor(final BinaryDispatcher dispatcher) {
    super(NullState.Binary);

    if (dispatcher == null) {
      throw new IllegalArgumentException("Dispatcher must not be null.");
    }

    this.dispatcher = dispatcher;

    dispatcher.controlWith(selfAs(DispatcherControl.class));
  }

  @Override
  public void read(final String id, Class<?> type, final ReadResultInterest<byte[]> interest) {
    readFor(id, type, interest);
  }

  @Override
  public void write(final State<byte[]> state, final WriteResultInterest<byte[]> interest) {
    writeWith(state, interest);
  }

  @Override
  protected void dispatch(final String dispatchId, final State<byte[]> state) {
    dispatcher.dispatchBinary(dispatchId, state);
  }
}
