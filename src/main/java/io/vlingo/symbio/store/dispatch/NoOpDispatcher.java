// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.dispatch;

@SuppressWarnings("rawtypes")
public class NoOpDispatcher implements Dispatcher {

  @Override
  public void controlWith(final DispatcherControl control) { }

  @Override
  public void dispatch(final Dispatchable dispatchable) { }
}
