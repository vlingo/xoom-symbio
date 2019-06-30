// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.object.inmemory;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Definition;
import io.vlingo.common.Failure;
import io.vlingo.common.Success;
import io.vlingo.symbio.BaseEntry;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.StateAdapterProvider;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.dispatch.control.DispatcherControlActor;
import io.vlingo.symbio.store.dispatch.inmemory.InMemoryDispatcherControlDelegate;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.PersistentObject;
import io.vlingo.symbio.store.object.PersistentObjectMapper;
import io.vlingo.symbio.store.object.QueryExpression;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * In-memory implementation of {@code ObjectStore}. Note that {@code queryAll()} variations
 * do not support select constraints but always select all stored objects.
 */
public class InMemoryObjectStoreActor extends Actor implements ObjectStore {
  private final EntryAdapterProvider entryAdapterProvider;
  private final StateAdapterProvider stateAdapterProvider;

  private final Map<Long, State<?>> store;
  private final Map<Class<?>,PersistentObjectMapper> mappers;
  private final List<BaseEntry<?>> entries;

  private final List<Dispatchable<BaseEntry<?>, State<?>>> dispatchables;
  private final Dispatcher<Dispatchable<BaseEntry<?>,State<?>>> dispatcher;
  private final DispatcherControl dispatcherControl;

  /**
   * Construct my default state.
   * @param dispatcher The dispatcher to be used
   */
  public InMemoryObjectStoreActor(final Dispatcher<Dispatchable<BaseEntry<?>,State<?>>> dispatcher){
    this(dispatcher, 1000L, 1000L);
  }
  
  public InMemoryObjectStoreActor(final Dispatcher<Dispatchable<BaseEntry<?>,State<?>>> dispatcher,
         final long checkConfirmationExpirationInterval, final long confirmationExpiration ) {
    this.store = new HashMap<>();
    this.mappers = new HashMap<>();
    this.entries = new ArrayList<>();
    this.entryAdapterProvider = EntryAdapterProvider.instance(stage().world());
    this.stateAdapterProvider = StateAdapterProvider.instance(stage().world());

    this.dispatcher = dispatcher;
    this.dispatchables = new CopyOnWriteArrayList<>();
    final InMemoryDispatcherControlDelegate<BaseEntry<?>, State<?>> dispatcherControlDelegate = new InMemoryDispatcherControlDelegate<>(dispatchables);

    this.dispatcherControl = stage().actorFor(
            DispatcherControl.class,
            Definition.has(
                    DispatcherControlActor.class,
                    Definition.parameters(
                            dispatcher,
                            dispatcherControlDelegate,
                            checkConfirmationExpirationInterval,
                            confirmationExpiration)));
  }

  /*
   * @see io.vlingo.symbio.store.object.ObjectStore#close()
   */
  @Override
  public void close() {
    store.clear();
  }


  /* @see io.vlingo.symbio.store.object.ObjectStore#persist(java.lang.Object, java.util.List, long, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest, java.lang.Object) */
  @Override
  public <T extends PersistentObject, E> void persist(
          final T persistentObject, final List<Source<E>> sources, final Metadata metadata,
          final long updateId, final PersistResultInterest interest, final Object object) {
    final State<?> raw = persistEach(persistentObject, metadata);

    final List<BaseEntry<?>> entries = entryAdapterProvider.asEntries(sources, metadata);
    appendEntries(entries);

    dispatch(raw, entries);
    interest.persistResultedIn(Success.of(Result.Success), persistentObject, 1, 1, object);
  }


  /* @see io.vlingo.symbio.store.object.ObjectStore#persistAll(java.util.Collection, java.util.List, long, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest, java.lang.Object) */
  @Override
  public <T extends PersistentObject, E> void persistAll(final Collection<T> persistentObjects, final List<Source<E>> sources,
          final Metadata metadata, final long updateId, final PersistResultInterest interest, final Object object) {

    final List<State<?>> states = new ArrayList<>(persistentObjects.size());
    for (final T persistentObject : persistentObjects) {
      final State<?> state = persistEach(persistentObject, metadata);
      states.add(state);
    }
    final List<BaseEntry<?>> entries = entryAdapterProvider.asEntries(sources, metadata);
    appendEntries(entries);

    states.forEach(state -> {
      //dispatch each persistent object
      dispatch(state, entries);
    });
    interest.persistResultedIn(Success.of(Result.Success), persistentObjects, persistentObjects.size(), persistentObjects.size(), object);
  }

  /*
   * @see io.vlingo.symbio.store.object.ObjectStore#queryAll(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest, java.lang.Object)
   */
  @Override
  public void queryAll(final QueryExpression expression, final QueryResultInterest interest, final Object object) {
    // NOTE: No query constraints accepted; selects all stored objects

    final Set<PersistentObject> all = new TreeSet<>();
    for (final State<?> entry : store.values()) {
      final PersistentObject persistentObject = stateAdapterProvider.fromRaw(entry);
      all.add(persistentObject);
    }
    interest.queryAllResultedIn(Success.of(Result.Success), QueryMultiResults.of(all), object);
  }

  /*
   * @see io.vlingo.symbio.store.object.ObjectStore#queryObject(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest, java.lang.Object)
   */
  @Override
  public void queryObject(final QueryExpression expression, final QueryResultInterest interest, final Object object) {
    final String id;

    if (expression.isListQueryExpression()) {
      id = idParameterAsString(expression.asListQueryExpression().parameters.get(0));
    } else if (expression.isMapQueryExpression()) {
      id = idParameterAsString(expression.asMapQueryExpression().parameters.get("id"));
    } else {
      interest.queryObjectResultedIn(Failure.of(new StorageException(Result.Error, "Unknown query type: " + expression)), QuerySingleResult.of(null), object);
      return;
    }

    final State<?> found = store.get(Long.parseLong(id));

    if (found != null) {
      final PersistentObject persistentObject = stateAdapterProvider.fromRaw(found);
      interest.queryObjectResultedIn(Success.of(Result.Success), QuerySingleResult.of(persistentObject), object);
    } else {
      interest.queryObjectResultedIn(Failure.of(new StorageException(Result.NotFound, "No object identified by: " + id)), QuerySingleResult.of(null), object);
    }
  }

  /*
   * @see io.vlingo.symbio.store.object.ObjectStore#registerMapper(io.vlingo.symbio.store.object.PersistentObjectMapper)
   */
  @Override
  public void registerMapper(final PersistentObjectMapper mapper) {
    mappers.put(mapper.type(), mapper);
  }

  private String idParameterAsString(final Object id) {
    if (id instanceof String) {
      return (String) id;
    } else if (id instanceof Long) {
      return String.valueOf((long) id);
    } else if (id instanceof Integer) {
      return String.valueOf((int) id);
    }
    return String.valueOf(id);
  }

  private <E extends State<?>> E persistEach(final PersistentObject persistentObject, final Metadata metadata) {
    final E raw = this.stateAdapterProvider.asRaw(String.valueOf(persistentObject.persistenceId()), persistentObject, 1, metadata);
    store.put(persistentObject.persistenceId(), raw);
    return raw;
  }

  private <E> void appendEntries(final Collection<BaseEntry<?>> entries) {
    this.entries.addAll(entries);
  }

  private void dispatch(final State<?> state, final List<BaseEntry<?>> entries ){
    final String id = getDispatchId(state, entries);
    final Dispatchable<BaseEntry<?>, State<?>> dispatchable = new Dispatchable<>(id, LocalDateTime.now(), state, entries);
    this.dispatchables.add(dispatchable);
    this.dispatcher.dispatch(dispatchable);
  }

  private static String getDispatchId(final State<?> raw, final List<BaseEntry<?>> entries) {
    return raw.id + ":" + entries.stream().map(Entry::id).collect(Collectors.joining(":"));
  }
}
