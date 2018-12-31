// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import io.vlingo.symbio.State.TextState;

public interface TextStateStore extends StateStore {
  void read(final String id, final Class<?> type, final ReadResultInterest<TextState> interest);
  void read(final String id, final Class<?> type, final ReadResultInterest<TextState> interest, final Object object);
  void write(final TextState state, final WriteResultInterest<TextState> interest);
  void write(final TextState state, final WriteResultInterest<TextState> interest, final Object object);

  public static interface TextDispatcher extends Dispatcher {
    void dispatchText(final String dispatchId, final TextState state);
  }
}
