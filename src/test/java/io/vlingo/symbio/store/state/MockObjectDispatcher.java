// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.symbio.State;
import io.vlingo.symbio.State.ObjectState;
import io.vlingo.symbio.store.state.StateStore.ConfirmDispatchedResultInterest;
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;
import io.vlingo.symbio.store.state.ObjectStateStore.ObjectDispatcher;

public class MockObjectDispatcher implements ObjectDispatcher {
  public final ConfirmDispatchedResultInterest confirmDispatchedResultInterest;
  public DispatcherControl control;
  public final Map<String,State<Object>> dispatched = new HashMap<>();
  public final AtomicBoolean processDispatch = new AtomicBoolean(true);
  public TestUntil until = TestUntil.happenings(0);

  public MockObjectDispatcher(final int testUntilHappenings, final ConfirmDispatchedResultInterest confirmDispatchedResultInterest) {
    this.until = TestUntil.happenings(testUntilHappenings);
    this.confirmDispatchedResultInterest = confirmDispatchedResultInterest;
  }

  @Override
  public void controlWith(final DispatcherControl control) {
    this.control = control;
  }

  @Override
  @SuppressWarnings({ "rawtypes", "unchecked" }) 
  public void dispatch(final String dispatchId, final ObjectState<?> state) {
    if (processDispatch.get()) {
      dispatched.put(dispatchId, (ObjectState) state);
      control.confirmDispatched(dispatchId, confirmDispatchedResultInterest);
      until.happened();
    }
  }

  @Override
  public void dispatchObject(final String dispatchId, final ObjectState<Object> state) {
    if (processDispatch.get()) {
      dispatched.put(dispatchId, state);
      control.confirmDispatched(dispatchId, confirmDispatchedResultInterest);
      until.happened();
    }
  }
}
