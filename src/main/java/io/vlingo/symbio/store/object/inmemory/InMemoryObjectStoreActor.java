// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.object.inmemory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import io.vlingo.actors.Actor;
import io.vlingo.common.Failure;
import io.vlingo.common.Success;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.PersistentObject;
import io.vlingo.symbio.store.object.PersistentObjectMapper;
import io.vlingo.symbio.store.object.QueryExpression;

/**
 * In-memory implementation of {@code ObjectStore}. Note that {@code queryAll()} variations
 * do not support select constraints but always select all stored objects.
 */
public class InMemoryObjectStoreActor extends Actor implements ObjectStore {
  private final Map<Long,SerializedPersistentObject> store;
  private final Map<Class<?>,PersistentObjectMapper> mappers;

  /**
   * Construct my default state.
   */
  public InMemoryObjectStoreActor() {
    this.store = new HashMap<>();
    this.mappers = new HashMap<>();
  }

  /*
   * @see io.vlingo.symbio.store.object.ObjectStore#close()
   */
  @Override
  public void close() {
    store.clear();
  }

  /*
   * @see io.vlingo.symbio.store.object.ObjectStore#persist(java.lang.Object, long, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest, java.lang.Object)
   */
  @Override
  public void persist(final Object persistentObject, final long updateId, final PersistResultInterest interest, final Object object) {
    persistEach(persistentObject);
    interest.persistResultedIn(Success.of(Result.Success), persistentObject, 1, 1, object);
  }

  /*
   * @see io.vlingo.symbio.store.object.ObjectStore#persistAll(java.util.Collection, long, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest, java.lang.Object)
   */
  @Override
  public void persistAll(final Collection<Object> persistentObjects, final long updateId, final PersistResultInterest interest, final Object object) {
    for (final Object persistentObject : persistentObjects) {
      persistEach(persistentObject);
    }
    interest.persistResultedIn(Success.of(Result.Success), persistentObjects, persistentObjects.size(), persistentObjects.size(), object);
  }

  /*
   * @see io.vlingo.symbio.store.object.ObjectStore#queryAll(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest, java.lang.Object)
   */
  @Override
  public void queryAll(final QueryExpression expression, final QueryResultInterest interest, final Object object) {
    // NOTE: No query constraints accepted; selects all stored objects

    final Set<PersistentObject> all = new TreeSet<>();
    for (final SerializedPersistentObject entry : store.values()) {
      final PersistentObject persistentObject = entry.deserialize();
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

    final SerializedPersistentObject found = store.get(Long.parseLong(id));

    if (found != null) {
      final PersistentObject persistentObject = found.deserialize();
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

  private void persistEach(final Object persistentObject) {
    final SerializedPersistentObject persistable = new SerializedPersistentObject((PersistentObject) persistentObject);
    store.put(persistable.id, persistable);
  }

  private static class SerializedPersistentObject {
    final long id;
    final String serialization;
    final Class<?> type;

    SerializedPersistentObject(final PersistentObject persistentObject) {
      this.id = persistentObject.persistenceId();
      this.type = persistentObject.getClass();
      this.serialization = JsonSerialization.serialized(persistentObject);
    }

    PersistentObject deserialize() {
      return (PersistentObject) JsonSerialization.deserialized(serialization, type);
    }
  }
}
