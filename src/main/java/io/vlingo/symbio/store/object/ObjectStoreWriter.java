// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.vlingo.common.Outcome;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
/**
 * ObjectStoreWriter defines protocol for writing to an {@link ObjectStore}.
 */
public interface ObjectStoreWriter {
  
  /**
   * An empty collection of {@link Source}.
   */
  public static final ArrayList<Source<Object>> EmptySources = new ArrayList<>();
  
  /**
   * Persists the new {@code persistentObject}.
   * @param persistentObject the Object to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default void persist(final Object persistentObject, final PersistResultInterest interest) {
    persist(persistentObject, EmptySources, -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObject}.
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <E> void persist(final Object persistentObject, final List<Source<E>> sources, final PersistResultInterest interest) {
    persist(persistentObject, sources, -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObject}.
   * @param persistentObject the Object to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default void persist(final Object persistentObject, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, EmptySources, -1, interest, object);
  }

  /**
   * Persists the new {@code persistentObject}.
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <E> void persist(final Object persistentObject, final List<Source<E>> sources, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, sources, -1, interest, object);
  }

  /**
   * Persists the {@code persistentObject} as new or updated depending on the value of {@code updateId}.
   * @param persistentObject the Object to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default void persist(final Object persistentObject, final long updateId, final PersistResultInterest interest) {
    persist(persistentObject, EmptySources, updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObject} as new or updated depending on the value of {@code updateId}.
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <E> void persist(final Object persistentObject, final List<Source<E>> sources, final long updateId, final PersistResultInterest interest) {
    persist(persistentObject, sources, updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObject}.
   * @param persistentObject the Object to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default void persist(final Object persistentObject, final long updateId, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, EmptySources, updateId, interest, object);
  }

  /**
   * Persists the {@code persistentObject}.
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  <E> void persist(final Object persistentObject, final List<Source<E>> sources, final long updateId, final PersistResultInterest interest, final Object object);


  /**
   * Persists the new {@code persistentObjects}.
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default void persistAll(final Collection<Object> persistentObjects, final PersistResultInterest interest) {
    persistAll(persistentObjects, EmptySources, -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObjects}.
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <E> void persistAll(final Collection<Object> persistentObjects, final List<Source<E>> sources, final PersistResultInterest interest) {
    persistAll(persistentObjects, sources, -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObjects}.
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default void persistAll(final Collection<Object> persistentObjects, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, EmptySources, -1, interest, object);
  }

  /**
   * Persists the new {@code persistentObjects}.
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <E> void persistAll(final Collection<Object> persistentObjects, final List<Source<E>> sources, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, sources, -1, interest, object);
  }

  /**
   * Persists the {@code persistentObjects} as new or updated depending on the value of {@code updateId}.
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default void persistAll(final Collection<Object> persistentObjects, final long updateId, final PersistResultInterest interest) {
    persistAll(persistentObjects, EmptySources, updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObjects} as new or updated depending on the value of {@code updateId}.
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <E> void persistAll(final Collection<Object> persistentObjects, final List<Source<E>> sources, final long updateId, final PersistResultInterest interest) {
    persistAll(persistentObjects, sources, updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObjects}.
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default void persistAll(final Collection<Object> persistentObjects, final long updateId, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, EmptySources, updateId, interest, object);
  }

  /**
   * Persists the {@code persistentObjects}.
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  <E> void persistAll(final Collection<Object> persistentObjects, final List<Source<E>> sources, final long updateId, final PersistResultInterest interest, final Object object);

  /**
   * Defines the result of persisting to the store with a persistent object.
   */
  public static interface PersistResultInterest {
    /**
     * Implemented by the interest of a given Object Store for persist operation results.
     * @param outcome the {@code Outcome<StorageException,Result>} of the persist operation
     * @param persistentObject the Object to persist; for persistAll() this will be a {@code Collection<Object>}
     * @param possible the int number of possible objects to persist
     * @param actual the int number of actual objects persisted
     * @param object the Object passed to persist() that is sent back to the receiver, or null if not passed
     */
    void persistResultedIn(final Outcome<StorageException,Result> outcome, final Object persistentObject, final int possible, final int actual, final Object object);
  }
}
