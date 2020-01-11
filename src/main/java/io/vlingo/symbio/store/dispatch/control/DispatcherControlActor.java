// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.dispatch.control;

import io.vlingo.actors.Actor;
import io.vlingo.common.Cancellable;
import io.vlingo.common.Scheduled;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.dispatch.ConfirmDispatchedResultInterest;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;

public class DispatcherControlActor extends Actor implements DispatcherControl, Scheduled<Object> {
  private final static long DEFAULT_REDISPATCH_DELAY = 2000L;
  
  private final Dispatcher<Dispatchable<? extends Entry<?>, ? extends State<?>>> dispatcher;
  private final DispatcherControlDelegate<? extends Entry<?>, ? extends State<?>> delegate;
  private final Cancellable cancellable;
  private final long confirmationExpiration;

  public DispatcherControlActor(final Dispatcher<Dispatchable<? extends Entry<?>, ? extends State<?>>> dispatcher,
          final DispatcherControlDelegate<? extends Entry<?>, ? extends State<?>> delegate,
          final long checkConfirmationExpirationInterval, final long confirmationExpiration) {
    this.dispatcher = dispatcher;
    this.delegate = delegate;
    this.confirmationExpiration = confirmationExpiration;
    this.cancellable = scheduler().schedule(this, null, DEFAULT_REDISPATCH_DELAY, checkConfirmationExpirationInterval);
    this.dispatcher.controlWith(this);
  }

  @Override
  public void intervalSignal(final Scheduled<Object> scheduled, final Object data) {
    dispatchUnconfirmed();
  }

  @Override
  public void confirmDispatched(final String dispatchId, final ConfirmDispatchedResultInterest interest) {
    try {
      delegate.confirmDispatched(dispatchId);
      interest.confirmDispatchedResultedIn(Result.Success, dispatchId);
    } catch (final Exception e) {
      logger().error(getClass().getSimpleName() + " confirmDispatched() failed because: " + e.getMessage(), e);
      interest.confirmDispatchedResultedIn(Result.Failure, dispatchId);
    }
  }

  @Override
  public void dispatchUnconfirmed() {
    try {
      final LocalDateTime now = LocalDateTime.now();
      final Collection<? extends Dispatchable<? extends Entry<?>, ? extends State<?>>> dispatchables = delegate.allUnconfirmedDispatchableStates();
      for (final Dispatchable<? extends Entry<?>, ? extends State<?>> dispatchable : dispatchables) {
        final LocalDateTime then = dispatchable.createdOn();
        final Duration duration = Duration.between(then, now);
        if (Math.abs(duration.toMillis()) > confirmationExpiration) {
          dispatcher.dispatch(dispatchable);
        }
      }
    } catch (final Exception e) {
      logger().error(getClass().getSimpleName() + " dispatchUnconfirmed() failed because: " + e.getMessage(), e);
    }
  }

  /* @see io.vlingo.symbio.store.state.StateStore.DispatcherControl#stop() */
  @Override
  public void stop() {
    if (cancellable != null) {
      cancellable.cancel();
    }
    this.delegate.stop();
    super.stop();
  }
}
