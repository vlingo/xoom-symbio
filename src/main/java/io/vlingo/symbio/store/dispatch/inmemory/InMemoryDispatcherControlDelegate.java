// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.dispatch.inmemory;

import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.DispatcherControl;

import java.util.Collection;
import java.util.List;

public class InMemoryDispatcherControlDelegate<E extends Entry<?>, RS extends State<?>> implements DispatcherControl.DispatcherControlDelegate<E, RS> {
  private final List<Dispatchable<E, RS>> dispatchables;

  public InMemoryDispatcherControlDelegate(final List<Dispatchable<E, RS>> dispatchables) {
    this.dispatchables = dispatchables;
  }

  @Override
  public Collection<Dispatchable<E, RS>> allUnconfirmedDispatchableStates() {
    return dispatchables;
  }

  @Override
  public void confirmDispatched(final String dispatchId) {
    dispatchables
            .stream()
            .filter(d -> d.id().equals(dispatchId))
            .findFirst()
            .ifPresent(dispatchables::remove);
  }

  @Override
  public void stop() {
    
  }
}
