// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.dispatch.inmemory;

import io.vlingo.actors.Actor;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Scheduled;
import io.vlingo.common.Scheduler;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.dispatch.ConfirmDispatchedResultInterest;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
/**
 * InMemoryDispatcherControl
 */
public class InMemoryDispatcherControl<D extends Dispatchable> extends Actor implements DispatcherControl, Scheduled<Object> {
  
  public final static long DEFAULT_REDISPATCH_DELAY = 2000L;

  private final Dispatcher<D> dispatcher;
  private final List<D> dispatchables;
  private final long confirmationExpiration;
  private final Cancellable cancellable;
  
  public InMemoryDispatcherControl(
    final Dispatcher<D> dispatcher,
    final List<D> dispatchables,
    final long checkConfirmationExpirationInterval,
    long confirmationExpiration)
  {
    this.dispatcher = dispatcher;
    this.dispatchables = dispatchables;
    this.confirmationExpiration = confirmationExpiration;
    Scheduler scheduler = new Scheduler();
    this.cancellable = scheduler.schedule(this, null, DEFAULT_REDISPATCH_DELAY, checkConfirmationExpirationInterval);
  }
  
  @Override
  public void intervalSignal(Scheduled<Object> scheduled, Object data) {
    dispatchUnconfirmed();
  }

  @Override
  public void dispatchUnconfirmed() {
    final LocalDateTime now = LocalDateTime.now();
    for (D dispatchable : dispatchables) {
      final LocalDateTime then = dispatchable.getCreatedAt();
      Duration duration = Duration.between(then, now);
      if (Math.abs(duration.toMillis()) > confirmationExpiration) {
        dispatcher.dispatch(dispatchable);
      }
    }
  }
  
  @Override
  public void confirmDispatched(String dispatchId, ConfirmDispatchedResultInterest interest) {
    dispatchables
            .stream()
            .filter(d -> d.getId().equals(dispatchId))
            .findFirst()
            .ifPresent(dispatchables::remove);

    interest.confirmDispatchedResultedIn(Result.Success, dispatchId);
  }

  /* @see io.vlingo.symbio.store.state.StateStore.DispatcherControl#stop() */
  @Override
  public void stop() {
    if (cancellable != null) {
      cancellable.cancel();
    }
  }
}
