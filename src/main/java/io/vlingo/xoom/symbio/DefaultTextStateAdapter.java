// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio;

import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.symbio.State.TextState;
import io.vlingo.xoom.symbio.store.StoredTypes;

public class DefaultTextStateAdapter implements StateAdapter<Object, TextState> {
  @Override
  public int typeVersion() {
    return 1;
  }

  @Override
  public Object fromRawState(final TextState raw) {
    try {
      final Class<?> stateType = StoredTypes.forName(raw.type);
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
  public TextState toRawState(final String id, final Object state, final int stateVersion, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(state);
    return new TextState(id, state.getClass(), typeVersion(), serialization, stateVersion, metadata);
  }
}
