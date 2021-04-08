// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.dispatch;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import io.vlingo.xoom.actors.ActorInstantiator;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.dispatch.control.DispatcherControlActor;

/**
 * Defines the means to confirm previously dispatched results, and to
 * re-dispatch those that have not been successfully confirmed.
 */
public interface DispatcherControl {
  /**
   * Confirm that the {@code dispatchId} has been dispatched.
   *
   * @param dispatchId the String unique identity of the dispatched state
   * @param interest   the ConfirmDispatchedResultInterest
   */
  void confirmDispatched(final String dispatchId, final ConfirmDispatchedResultInterest interest);

  /**
   * Attempt to dispatch any unconfirmed dispatchables.
   */
  void dispatchUnconfirmed();

  /**
   * Stop attempting to dispatch unconfirmed dispatchables.
   */
  void stop();

  /**
   * Defines the interface through which basic abstract storage implementations
   * delegate to the technical implementations. See any of the existing concrete
   * implementations for details, such as the Postgres or HSQL found in component
   * {@code xoom-symbio-jdbc}.
   */
  interface DispatcherControlDelegate<E extends Entry<?>, RS extends State<?>> {
    Collection<Dispatchable<E, RS>> allUnconfirmedDispatchableStates() throws Exception;

    void confirmDispatched(final String dispatchId);

    void stop();
  }

  public static class DispatcherControlInstantiator<ET extends Entry<?>, ST extends State<?>> implements ActorInstantiator<DispatcherControlActor> {
    private static final long serialVersionUID = 1739556269104244158L;
    private static final long DEFAULT_REDISPATCH_DELAY = 2000L;

    private final List<Dispatcher<Dispatchable<? extends Entry<?>, ? extends State<?>>>> dispatchers;
    private final DispatcherControlDelegate<? extends Entry<?>, ? extends State<?>> delegate;
    private final long redispatchDelay;
    private final long checkConfirmationExpirationInterval;
    private final long confirmationExpiration;

    public DispatcherControlInstantiator(
            final List<Dispatcher<Dispatchable<? extends Entry<?>, ? extends State<?>>>> dispatchers,
            final DispatcherControlDelegate<? extends Entry<?>, ? extends State<?>> delegate,
            final long redispatchDelay,
            final long checkConfirmationExpirationInterval,
            final long confirmationExpiration) {
      this.dispatchers = dispatchers;
      this.delegate = delegate;
      this.redispatchDelay = redispatchDelay;
      this.checkConfirmationExpirationInterval = checkConfirmationExpirationInterval;
      this.confirmationExpiration = confirmationExpiration;
    }

    public DispatcherControlInstantiator(
            final List<Dispatcher<Dispatchable<? extends Entry<?>, ? extends State<?>>>> dispatchers,
            final DispatcherControlDelegate<? extends Entry<?>, ? extends State<?>> delegate,
            final long checkConfirmationExpirationInterval,
            final long confirmationExpiration) {
      this(dispatchers, delegate, DEFAULT_REDISPATCH_DELAY, checkConfirmationExpirationInterval, confirmationExpiration);
    }

    public DispatcherControlInstantiator(
            final Dispatcher<Dispatchable<? extends Entry<?>, ? extends State<?>>> dispatcher,
            final DispatcherControlDelegate<? extends Entry<?>, ? extends State<?>> delegate,
            final long redispatchDelay,
            final long checkConfirmationExpirationInterval,
            final long confirmationExpiration) {
      this(Arrays.asList(dispatcher), delegate, redispatchDelay, checkConfirmationExpirationInterval, confirmationExpiration);
    }

    public DispatcherControlInstantiator(
            final Dispatcher<Dispatchable<? extends Entry<?>, ? extends State<?>>> dispatcher,
            final DispatcherControlDelegate<? extends Entry<?>, ? extends State<?>> delegate,
            final long checkConfirmationExpirationInterval,
            final long confirmationExpiration) {
      this(Arrays.asList(dispatcher), delegate, checkConfirmationExpirationInterval, confirmationExpiration);
    }

    @Override
    public DispatcherControlActor instantiate() {
      return new DispatcherControlActor(
              dispatchers,
              delegate,
              redispatchDelay,
              checkConfirmationExpirationInterval,
              confirmationExpiration);
    }

    @Override
    public Class<DispatcherControlActor> type() {
      return DispatcherControlActor.class;
    }
  }
}
