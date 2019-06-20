// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.dispatch;

/**
 * Defines the means to confirm previously dispatched results, and to
 * re-dispatch those that have not been successfully confirmed.
 */
public interface DispatcherControl {
  /**
   * Confirm that the {@code dispatchId} has been dispatched.
   * @param dispatchId the String unique identity of the dispatched state
   * @param interest the ConfirmDispatchedResultInterest
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
}
