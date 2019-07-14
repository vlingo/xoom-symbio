// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.object;

import io.vlingo.symbio.Entry;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.object.ObjectStoreReader.QueryMultiResults;
import io.vlingo.symbio.store.object.ObjectStoreReader.QuerySingleResult;

import java.util.Collection;

public interface ObjectStoreDelegate<E extends Entry<?>, ST extends State<?>> extends DispatcherControl.DispatcherControlDelegate<E, ST> {

  /**
   * Register the {@code mapper} for a given persistent type.
   *
   * @param mapper the PersistentObjectMapper
   */
  void registerMapper(final PersistentObjectMapper mapper);

  /**
   * Close me.
   */
  void close();

  /**
   * Copy this delegate.
   *
   * @return a copy of this delegate
   */
  ObjectStoreDelegate copy();

  /**
   * Begin store transaction.
   */
  void beginTransaction();

  /**
   * Complete store transaction.
   */
  void completeTransaction();

  /**
   * Fail store transaction.
   */
  void failTransaction();

  /**
   * Persists the {@code persistentObjects} with {@code metadata}.
   *
   * @param <T>               the concrete type of {@link PersistentObject}s to persist
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param metadata          the Metadata associated with the persistentObjects and sources
   * @param updateId          the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @return the {@code Collection<ST>} with persisted {@code State<?>}, created from {@code persistentObjects}
   * @throws StorageException in case persistence failed
   */
  <T extends PersistentObject> Collection<ST> persistAll(final Collection<T> persistentObjects, final long updateId, final Metadata metadata)
          throws StorageException;

  /**
   * Persists the {@code persistentObject} with {@code metadata}.
   *
   * @param <T>              the concrete type of {@link PersistentObject} to persist
   * @param persistentObject the Object to persist
   * @param metadata         the Metadata associated with the persistentObject and sources
   * @param updateId         the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @return the persisted {@code State<?>}, created from {@code persistentObject}
   * @throws StorageException in case persistence failed
   */
  <T extends PersistentObject> ST persist(final T persistentObject, final long updateId, final Metadata metadata) throws StorageException;

  /**
   * Persist the {@code Collection<E>} of entries, that originated from {@code sources}.
   *
   * @param entries {@code Collection<E>}
   * @throws StorageException in case persistence failed
   */
  void persistEntries(final Collection<E> entries) throws StorageException;

  /**
   * Persist the {@code Dispatchable<E, ST>} that originated from write.
   *
   * @param dispatchable {@code Dispatchable<E, ST>}
   * @throws StorageException in case persistence failed
   */
  void persistDispatchable(final Dispatchable<E, ST> dispatchable) throws StorageException;

  /**
   * Executes the query defined by {@code expression} that may result in zero to many objects.
   *
   * @param expression the QueryExpression
   * @return a {@code Collection<QueryMultiResults>} with objects that matches the expression.
   * @throws StorageException in case query failed
   */
  QueryMultiResults queryAll(final QueryExpression expression) throws StorageException;

  /**
   * Executes the query defined by {@code expression} that may result in one object.
   *
   * @param expression the QueryExpression
   * @return a {@code QuerySingleResult} with the result
   * @throws StorageException in case query failed
   */
  QuerySingleResult queryObject(final QueryExpression expression) throws StorageException;
}
