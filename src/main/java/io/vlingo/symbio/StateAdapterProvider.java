// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class StateAdapterProvider {
  private final Map<Class<?>,StateAdapter<?,?>> adapters;
  private final Map<String,StateAdapter<?,?>> namedAdapters;

  public StateAdapterProvider() {
    this.adapters = new HashMap<>();
    this.namedAdapters = new HashMap<>();
  }

  public <S,ST extends State<?>> void registerAdapter(final Class<S> stateType, final StateAdapter<S,ST> adapter) {
    adapters.put(stateType, adapter);
    namedAdapters.put(stateType.getName(), adapter);
  }

  public <S,ST extends State<?>> void registerAdapter(final Class<S> stateType, final StateAdapter<S,ST> adapter, final BiConsumer<Class<S>,StateAdapter<S,ST>> consumer) {
    adapters.put(stateType, adapter);
    namedAdapters.put(stateType.getName(), adapter);
    consumer.accept(stateType, adapter);
  }

  @SuppressWarnings("unchecked")
  public <S,ST extends State<?>> ST asRaw(final S state, final int stateVersion) {
    final StateAdapter<S,ST>  adapter = (StateAdapter<S,ST>) adapter((Class<S>) state.getClass());
    return adapter.toRawState(state, stateVersion);
  }

  public <S,ST extends State<?>> S fromRaw(final ST state) {
    StateAdapter<S,ST> adapter = namedAdapter(state);
    return (S) adapter.fromRawState(state);
  }

  @SuppressWarnings("unchecked")
  private <S,ST extends State<?>> StateAdapter<S,ST> adapter(final Class<?> stateType) {
    final StateAdapter<S,ST> adapter = (StateAdapter<S,ST>) adapters.get(stateType);
    if (adapter != null) {
      return adapter;
    }
    throw new IllegalStateException("Adapter not registrered for: " + stateType.getName());
  }

  @SuppressWarnings("unchecked")
  private <S,ST extends State<?>> StateAdapter<S,ST> namedAdapter(final ST state) {
    final StateAdapter<S,ST> adapter = (StateAdapter<S,ST>) namedAdapters.get(state.type);
    if (adapter != null) {
      return adapter;
    }
    throw new IllegalStateException("Adapter not registrered for: " + state.type);
  }
}
