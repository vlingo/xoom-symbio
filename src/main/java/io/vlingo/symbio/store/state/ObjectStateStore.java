// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import io.vlingo.symbio.State.ObjectState;

public interface ObjectStateStore extends StateStore {
  void read(final String id, final Class<?> type, final ReadResultInterest<ObjectState<Object>> interest);
  void read(final String id, final Class<?> type, final ReadResultInterest<ObjectState<Object>> interest, final Object object);
  void write(final ObjectState<Object> state, final WriteResultInterest<ObjectState<Object>> interest);
  void write(final ObjectState<Object> state, final WriteResultInterest<ObjectState<Object>> interest, final Object object);

  public static interface ObjectDispatcher extends Dispatcher {
    void dispatchObject(final String dispatchId, final ObjectState<Object> state);
  }
}
