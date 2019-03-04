// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.inmemory;

import io.vlingo.actors.Actor;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Scheduled;
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;
import io.vlingo.symbio.store.state.StateStore.RedispatchControl;
/**
 * RedispatcherActor
 */
public class InMemoryRedispatchControlActor extends Actor implements RedispatchControl, Scheduled<Object> {
  
  public final static long DEFAULT_REDISPATCH_DELAY = 2000L;

  private final DispatcherControl dispatcherControl;
  private final long confirmationExpiration;
  private final Cancellable cancellable;
  
  @SuppressWarnings("unchecked")
  public InMemoryRedispatchControlActor(final DispatcherControl dispatcherControl, final long checkConfirmationExpirationInterval, long confirmationExpiration) {
    this.dispatcherControl = dispatcherControl;
    this.confirmationExpiration = confirmationExpiration;
    this.cancellable = scheduler().schedule(selfAs(Scheduled.class), null, DEFAULT_REDISPATCH_DELAY, checkConfirmationExpirationInterval);
  }
  
  public long confirmationException() {
    return confirmationExpiration;
  }
  
  @Override
  protected void afterStop() {
    super.afterStop();
    if (cancellable != null) {
      cancellable.cancel();
    }
  }

  @Override
  public void intervalSignal(Scheduled<Object> scheduled, Object data) {
    dispatcherControl.dispatchUnconfirmed();
  }
}
