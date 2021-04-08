// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.symbio.State.TextState;

public class StateAdapterProvider {
  static final String INTERNAL_NAME = UUID.randomUUID().toString();

  private final Map<Class<?>,StateAdapter<?,?>> adapters;
  private final Map<String,StateAdapter<?,?>> namedAdapters;
  private final StateAdapter<Object,TextState> defaultTextStateAdapter;

  public static StateAdapterProvider instance(final World world) {
    StateAdapterProvider instance = world.resolveDynamic(INTERNAL_NAME, StateAdapterProvider.class);
    if (instance == null) {
      instance = new StateAdapterProvider(world);
    }
    return instance;
  }

  public StateAdapterProvider(final World world) {
    this();
    world.registerDynamic(INTERNAL_NAME, this);
  }

  public StateAdapterProvider() {
    this.adapters = new HashMap<>();
    this.namedAdapters = new HashMap<>();
    this.defaultTextStateAdapter = new DefaultTextStateAdapter();
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

  public <S,ST extends State<?>> ST asRaw(final String id, final S state, final int stateVersion) {
    return asRaw(id, state, stateVersion, Metadata.nullMetadata());
  }

  @SuppressWarnings("unchecked")
  public <S,ST extends State<?>> ST asRaw(final String id, final S state, final int stateVersion, final Metadata metadata) {
    final StateAdapter<S,ST>  adapter = (StateAdapter<S,ST>) adapter((Class<S>) state.getClass());
    if (adapter != null) {
      return adapter.toRawState(id, state, stateVersion, metadata);
    }
    return (ST) defaultTextStateAdapter.toRawState(id, state, stateVersion, metadata);
  }

  @SuppressWarnings("unchecked")
  public <S,ST extends State<?>> S fromRaw(final ST state) {
    StateAdapter<S,ST> adapter = namedAdapter(state);
    if (adapter != null) {
      return adapter.fromRawState(state);
    }
    return (S) defaultTextStateAdapter.fromRawState((TextState) state);
  }

  @SuppressWarnings("unchecked")
  private <S,ST extends State<?>> StateAdapter<S,ST> adapter(final Class<?> stateType) {
    final StateAdapter<S,ST> adapter = (StateAdapter<S,ST>) adapters.get(stateType);
    return adapter;
  }

  @SuppressWarnings("unchecked")
  private <S,ST extends State<?>> StateAdapter<S,ST> namedAdapter(final ST state) {
    final StateAdapter<S,ST> adapter = (StateAdapter<S,ST>) namedAdapters.get(state.type);
    return adapter;
  }
}
