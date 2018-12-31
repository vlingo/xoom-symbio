// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.inmemory;

import io.vlingo.symbio.State.ObjectState;
import io.vlingo.symbio.store.state.ObjectStateStore;
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;

public class InMemoryObjectStateStoreActor extends InMemoryStateStoreActor<ObjectState<Object>>
    implements ObjectStateStore, DispatcherControl {

  private final ObjectDispatcher dispatcher;
 
  public InMemoryObjectStateStoreActor(final ObjectDispatcher dispatcher) {
    super(ObjectState.Null);

    if (dispatcher == null) {
      throw new IllegalArgumentException("Dispatcher must not be null.");
    }

    this.dispatcher = dispatcher;

    dispatcher.controlWith(selfAs(DispatcherControl.class));
  }

  @Override
  public void read(final String id, Class<?> type, final ReadResultInterest<ObjectState<Object>> interest) {
    readFor(id, type, interest, null);
  }

  @Override
  public void read(final String id, final Class<?> type, final ReadResultInterest<ObjectState<Object>> interest, final Object object) {
    readFor(id, type, interest, object);
  }

  @Override
  public void write(final ObjectState<Object> state, final WriteResultInterest<ObjectState<Object>> interest) {
    writeWith(state, interest, null);
  }

  @Override
  public void write(final ObjectState<Object> state, final WriteResultInterest<ObjectState<Object>> interest, final Object object) {
    writeWith(state, interest, object);
  }

  @Override
  protected void dispatch(final String dispatchId, final ObjectState<Object> state) {
    dispatcher.dispatchObject(dispatchId, state);
  }
}
