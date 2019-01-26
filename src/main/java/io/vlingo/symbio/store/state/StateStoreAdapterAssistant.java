// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.HashMap;
import java.util.Map;

import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.StateAdapter;

public class StateStoreAdapterAssistant {
  private final Map<Class<?>,StateAdapter<?,?>> stateAdapters;

  public StateStoreAdapterAssistant() {
    this.stateAdapters = new HashMap<>();
  }

  @SuppressWarnings("unchecked")
  public <ST,RS extends State<?>> RS adaptToRawState(final ST state, final int stateVersion) {
    final StateAdapter<ST,RS> adapter = (StateAdapter<ST,RS>) stateAdapters.get(state.getClass());
    final RS raw = adapter.toRawState(state, stateVersion);
    return raw;
  }

  @SuppressWarnings("unchecked")
  public <ST,RS extends State<?>> RS adaptToRawState(final ST state, final int stateVersion, final Metadata metadata) {
    final StateAdapter<ST,RS> adapter = (StateAdapter<ST,RS>) stateAdapters.get(state.getClass());
    final RS raw = adapter.toRawState(state, stateVersion, metadata);
    return raw;
  }

  @SuppressWarnings("unchecked")
  public <ST,RS extends State<?>> ST adaptFromRawState(final RS raw) {
    final StateAdapter<ST,RS> adapter = (StateAdapter<ST,RS>) stateAdapters.get(raw.typed());
    final ST state = adapter.fromRawState(raw);
    return state;
  }

  public <S, R extends State<?>> void registerAdapter(Class<S> stateType, StateAdapter<S, R> adapter) {
    stateAdapters.put(stateType, adapter);
  }
}