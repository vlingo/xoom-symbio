// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state;

import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.State.TextState;
import io.vlingo.xoom.symbio.StateAdapter;
import io.vlingo.xoom.symbio.store.journal.inmemory.SnapshotState;

public class SnapshotStateAdapter implements StateAdapter<SnapshotState,TextState> {

  @Override
  public int typeVersion() {
    return 1;
  }

  @Override
  public SnapshotState fromRawState(final TextState raw) {
    return JsonSerialization.deserialized(raw.data, raw.typed());
  }

  @Override
  public <ST> ST fromRawState(final TextState raw, final Class<ST> stateType) {
    return JsonSerialization.deserialized(raw.data, stateType);
  }

  @Override
  public TextState toRawState(final SnapshotState state, final int stateVersion, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(state);
    return new TextState(TextState.NoOp, SnapshotState.class, typeVersion(), serialization, stateVersion, metadata);
  }

  @Override
  public TextState toRawState(final String id, final SnapshotState state, final int stateVersion, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(state);
    return new TextState(id, SnapshotState.class, typeVersion(), serialization, stateVersion, metadata);
  }
}
