// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import io.vlingo.symbio.State;

public interface BinaryStateStore extends StateStore {
  void read(final String id, final Class<?> type, final ResultInterest<byte[]> interest);
  void write(final State<byte[]> state, final ResultInterest<byte[]> interest);

  public static interface BinaryDispatcher extends Dispatcher {
    void dispatchBinary(final String dispatchId, final State<byte[]> state);
  }
}
