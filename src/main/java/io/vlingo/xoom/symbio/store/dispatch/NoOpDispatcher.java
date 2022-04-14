// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.dispatch;

import io.vlingo.xoom.symbio.store.Result;

@SuppressWarnings("rawtypes")
public class NoOpDispatcher implements Dispatcher, ConfirmDispatchedResultInterest {
  private DispatcherControl control;

  //=====================================
  // Dispatcher
  //=====================================

  @Override
  public void controlWith(final DispatcherControl control) {
    this.control = control;
  }

  @Override
  public void dispatch(final Dispatchable dispatchable) {
    control.confirmDispatched(dispatchable.id(), this);
  }

  //=====================================
  // ConfirmDispatchedResultInterest
  //=====================================

  @Override
  public void confirmDispatchedResultedIn(final Result result, final String dispatchId) { }
}
