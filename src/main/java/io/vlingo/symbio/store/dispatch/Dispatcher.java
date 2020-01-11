// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.dispatch;

/**
 * Defines the support for dispatching.
 */
@SuppressWarnings("rawtypes")
public interface Dispatcher<D extends Dispatchable> {
  /**
   * Register the {@code control} with the receiver.
   *
   * @param control the DispatcherControl to register
   */
  void controlWith(final DispatcherControl control);

  /**
   * Dispatch the Dispatchable instance.
   *
   * @param dispatchable the Dispatchable instance to this dispatch
   */
  void dispatch(final D dispatchable);
}
