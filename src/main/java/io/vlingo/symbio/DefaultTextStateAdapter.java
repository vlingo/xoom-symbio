// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.State.TextState;

public class DefaultTextStateAdapter implements StateAdapter<Object, TextState> {
  @Override
  public int typeVersion() {
    return 1;
  }

  @Override
  public Object fromRawState(final TextState raw) {
    try {
      final Class<?> stateType = Class.forName(raw.type);
      return JsonSerialization.deserialized(raw.data, stateType);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot convert to type: " + raw.type);
    }
  }

  @Override
  public <ST> ST fromRawState(final TextState raw, final Class<ST> stateType) {
    return JsonSerialization.deserialized(raw.data, stateType);
  }

  @Override
  public TextState toRawState(final Object state, final int stateVersion, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(state);
    return new TextState(State.NoOp, state.getClass(), typeVersion(), serialization, stateVersion);
  }
}
