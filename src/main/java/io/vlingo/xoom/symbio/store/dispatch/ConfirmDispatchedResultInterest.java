// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.dispatch;

import io.vlingo.xoom.symbio.store.Result;

/**
 * Defines the means to communicate the confirmation of a previously
 * dispatched storage {@code State<?>} results to the receiver.
 */
public interface ConfirmDispatchedResultInterest {
  /**
   * Sends the confirmation of a dispatched {@code State<?>}.
   * @param result the {@code Result} of the dispatch confirmation
   * @param dispatchId the String unique identity of the dispatched {@code State<?>}
   */
  void confirmDispatchedResultedIn(final Result result, final String dispatchId);
}
