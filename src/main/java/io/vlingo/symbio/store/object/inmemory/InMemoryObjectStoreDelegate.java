// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
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
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.object.ObjectStoreDelegate;
import io.vlingo.symbio.store.object.ObjectStoreReader.QueryMultiResults;
import io.vlingo.symbio.store.object.ObjectStoreReader.QuerySingleResult;
import io.vlingo.symbio.store.object.PersistentObject;
import io.vlingo.symbio.store.object.PersistentObjectMapper;
import io.vlingo.symbio.store.object.QueryExpression;

public class InMemoryObjectStoreDelegate
        implements ObjectStoreDelegate<BaseEntry<?>, State<?>>, DispatcherControl.DispatcherControlDelegate<BaseEntry<?>, State<?>> {

  private final Map<Long, State<?>> store;
  private final List<BaseEntry<?>> entries;
  private final List<Dispatchable<BaseEntry<?>, State<?>>> dispatchables;
  private final StateAdapterProvider stateAdapterProvider;
  private final IdentityGenerator identityGenerator;

  public InMemoryObjectStoreDelegate(final StateAdapterProvider stateAdapterProvider) {
    this.stateAdapterProvider = stateAdapterProvider;
    this.store = new HashMap<>();
    this.entries = new ArrayList<>();
    this.dispatchables = new CopyOnWriteArrayList<>();
    this.identityGenerator = new IdentityGenerator.RandomIdentityGenerator();
  }

  private static String idParameterAsString(final Object id) {
    if (id instanceof String) {
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
  public void registerMapper(final PersistentObjectMapper mapper) {
    //InMemory store does not require mappers
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() {
    this.store.clear();
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
  public <T extends PersistentObject> Collection<State<?>> persistAll(final Collection<T> persistentObjects, final long updateId, final Metadata metadata) {
    final List<State<?>> states = new ArrayList<>(persistentObjects.size());
    for (final T persistentObject : persistentObjects) {
      final State<?> raw = persist(persistentObject, metadata);
      states.add(raw);
    }

    return states;
  }

  private <T extends PersistentObject> State<?> persist(final T persistentObject, final Metadata metadata) {
    final State<?> raw = this.stateAdapterProvider.asRaw(String.valueOf(persistentObject.persistenceId()), persistentObject, 1, metadata);
    store.put(persistentObject.persistenceId(), raw);
    return raw;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public <T extends PersistentObject> State<?> persist(final T persistentObject, final long updateId, final Metadata metadata) {
    return persist(persistentObject, metadata);
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
    for (final State<?> entry : store.values()) {
      final Object persistentObject = stateAdapterProvider.fromRaw(entry);
      all.add(persistentObject);
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

    final State<?> found = store.get(Long.parseLong(id));

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
