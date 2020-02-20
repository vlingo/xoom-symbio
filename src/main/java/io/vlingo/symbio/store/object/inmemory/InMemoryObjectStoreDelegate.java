// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.object.inmemory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import io.vlingo.common.identity.IdentityGenerator;
import io.vlingo.symbio.BaseEntry;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.StateAdapterProvider;
import io.vlingo.symbio.store.QueryExpression;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.object.ObjectStoreDelegate;
import io.vlingo.symbio.store.object.ObjectStoreReader.QueryMultiResults;
import io.vlingo.symbio.store.object.ObjectStoreReader.QuerySingleResult;
import io.vlingo.symbio.store.object.StateObject;
import io.vlingo.symbio.store.object.StateObjectMapper;

public class InMemoryObjectStoreDelegate
        implements ObjectStoreDelegate<BaseEntry<?>, State<?>>, DispatcherControl.DispatcherControlDelegate<BaseEntry<?>, State<?>> {

  private long nextId;

  private final Map<Class<?>,Map<Long, State<?>>> stores;
  private final List<BaseEntry<?>> entries;
  private final List<Dispatchable<BaseEntry<?>, State<?>>> dispatchables;
  private final StateAdapterProvider stateAdapterProvider;
  private final IdentityGenerator identityGenerator;

  public InMemoryObjectStoreDelegate(final StateAdapterProvider stateAdapterProvider) {
    this.stateAdapterProvider = stateAdapterProvider;
    this.stores = new HashMap<>();
    this.entries = new ArrayList<>();
    this.dispatchables = new CopyOnWriteArrayList<>();
    this.identityGenerator = new IdentityGenerator.RandomIdentityGenerator();

    this.nextId = 1;
  }

  private static String idParameterAsString(final Object id) {
    if (id == null) {
      return null;
    } else if (id instanceof String) {
      return (String) id;
    } else if (id instanceof Long) {
      return String.valueOf((long) id);
    } else if (id instanceof Integer) {
      return String.valueOf((int) id);
    }
    return String.valueOf(id);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void registerMapper(final StateObjectMapper mapper) {
    //InMemory store does not require mappers
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    this.stores.clear();
    this.entries.clear();
    this.dispatchables.clear();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void stop() {
    this.close();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  @SuppressWarnings("rawtypes")
  public ObjectStoreDelegate copy() {
    return new InMemoryObjectStoreDelegate(this.stateAdapterProvider);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void beginTransaction() {
    //InMemory store does not require transactions
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void completeTransaction() {
    //InMemory store does not require transactions
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void failTransaction() {
    //InMemory store does not require transactions
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T extends StateObject> Collection<State<?>> persistAll(final Collection<T> stateObjects, final long updateId, final Metadata metadata) {
    final List<State<?>> states = new ArrayList<>(stateObjects.size());
    for (final T stateObject : stateObjects) {
      final State<?> raw = persist(stateObject, metadata);
      states.add(raw);
    }

    return states;
  }

  private <T extends StateObject> State<?> persist(final T stateObject, final Metadata metadata) {
    final State<?> raw = this.stateAdapterProvider.asRaw(String.valueOf(stateObject.persistenceId()), stateObject, 1, metadata);
    final Map<Long, State<?>> store = stores.computeIfAbsent(stateObject.getClass(), (type) -> new HashMap<>());
    final long persistenceId = stateObject.persistenceId() == -1L ? nextId++ : stateObject.persistenceId();
    store.put(persistenceId, raw);
    stateObject.__internal__setPersistenceId(persistenceId);
    return raw;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T extends StateObject> State<?> persist(final T stateObject, final long updateId, final Metadata metadata) {
    return persist(stateObject, metadata);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void persistEntries(final Collection<BaseEntry<?>> entries) {
    entries.forEach(baseEntry -> {
      baseEntry.__internal__setId(identityGenerator.generate().toString());
    });
    this.entries.addAll(entries);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void persistDispatchable(final Dispatchable<BaseEntry<?>, State<?>> dispatchable) {
    this.dispatchables.add(dispatchable);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QueryMultiResults queryAll(final QueryExpression expression) {
    // NOTE: No query constraints accepted; selects all stored objects

    final Set<Object> all = new HashSet<>();
    final Map<Long, State<?>> store = stores.computeIfAbsent(expression.type, (type) -> new HashMap<>());
    for (final State<?> entry : store.values()) {
      final Object stateObject = stateAdapterProvider.fromRaw(entry);
      all.add(stateObject);
    }

    return new QueryMultiResults(all);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QuerySingleResult queryObject(final QueryExpression expression) {
    final String id;
    if (expression.isListQueryExpression()) {
      id = idParameterAsString(expression.asListQueryExpression().parameters.get(0));
    } else if (expression.isMapQueryExpression()) {
      id = idParameterAsString(expression.asMapQueryExpression().parameters.get("id"));
    } else {
      throw new StorageException(Result.Error, "Unknown query type: " + expression);
    }

    final Map<Long, State<?>> store = stores.computeIfAbsent(expression.type, (type) -> new HashMap<>());
    final State<?> found = (id == null || id.equals("-1")) ? null : store.get(Long.parseLong(id));

    final Object result = Optional
            .ofNullable(found)
            .map(stateAdapterProvider::fromRaw)
            .orElse(null);
    return new QuerySingleResult(result);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<Dispatchable<BaseEntry<?>, State<?>>> allUnconfirmedDispatchableStates() {
    return new ArrayList<>(dispatchables);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void confirmDispatched(final String dispatchId) {
    dispatchables.stream()
            .filter(d -> d.id().equals(dispatchId))
            .findFirst()
            .ifPresent(dispatchables::remove);
  }

  List<BaseEntry<?>> readOnlyJournal() {
    return entries;
  }
}
