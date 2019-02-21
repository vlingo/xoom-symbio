// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import io.vlingo.actors.Actor;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Scheduled;
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;
import io.vlingo.symbio.store.state.StateStore.RedispatchControl;
/**
 * RedispatcherActor
 */
public class RedispatchControlActor extends Actor implements RedispatchControl, Scheduled<Object> {
  
  private final DispatcherControl dispatcherControl;
  private final Cancellable cancellable;
  
  public RedispatchControlActor(final DispatcherControl dispatcherControl, final long checkConfirmationExpirationInterval, long confirmationExpiration) {
    this.dispatcherControl = dispatcherControl;
    this.cancellable = scheduler().schedule(selfAs(Scheduled.class), null, confirmationExpiration, checkConfirmationExpirationInterval);
  }
  
  /* @see io.vlingo.actors.Actor#afterStop() */
  @Override
  protected void afterStop() {
    super.afterStop();
    if (cancellable != null) {
      cancellable.cancel();
    }
  }

  /* @see io.vlingo.common.Scheduled#intervalSignal(io.vlingo.common.Scheduled, java.lang.Object) */
  @Override
  public void intervalSignal(Scheduled<Object> scheduled, Object data) {
    //System.out.println("RedispatchControlActor::redispatching at " + System.currentTimeMillis() + " on " + Thread.currentThread().getName());
    dispatcherControl.dispatchUnconfirmed();
  }
}
