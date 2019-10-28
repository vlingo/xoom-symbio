// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.object;

import io.vlingo.common.Outcome;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;

import java.util.Collection;
import java.util.List;
/**
 * ObjectStoreWriter defines protocol for writing to an {@link ObjectStore}.
 */
public interface ObjectStoreWriter {

  /**
   * Persists the new {@code stateObject} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param stateSources the Object to persist with the domain events that were its source of truth
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persist(final StateSources<T,E> stateSources, final PersistResultInterest interest) {
    persist(stateSources, Metadata.nullMetadata(), -1, interest, null);
  }

  /**
   * Persists the new {@code stateObject} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param stateSources the Object to persist with the domain events that were its source of truth
   * @param metadata the Metadata associated with the stateObject and sources
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persist(final StateSources<T,E> stateSources, final Metadata metadata, final PersistResultInterest interest) {
    persist(stateSources, metadata, -1, interest, null);
  }

  /**
   * Persists the new {@code stateObject} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param stateSources the Object to persist with the domain events that were its source of truth
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persist(final StateSources<T,E> stateSources, final PersistResultInterest interest, final Object object) {
    persist(stateSources, Metadata.nullMetadata(), -1, interest, object);
  }

  /**
   * Persists the new {@code stateObject} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param stateSources the Object to persist with the domain events that were its source of truth
   * @param metadata the Metadata associated with the stateObject and sources
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persist(final StateSources<T,E> stateSources, final Metadata metadata, final PersistResultInterest interest, final Object object) {
    persist(stateSources, metadata, -1, interest, object);
  }

  /**
   * Persists the {@code stateObject} with {@code sources} as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param stateSources the Object to persist with the domain events that were its source of truth
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persist(final StateSources<T,E> stateSources, final long updateId, final PersistResultInterest interest) {
    persist(stateSources, Metadata.nullMetadata(), updateId, interest, null);
  }

  /**
   * Persists the {@code stateObject} with {@code sources} and {@code metadata}
   * as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param stateSources the Object to persist with the domain events that were its source of truth
   * @param metadata the Metadata associated with the stateObject and sources
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persist(final StateSources<T,E> stateSources, final Metadata metadata, final long updateId, final PersistResultInterest interest) {
    persist(stateSources, metadata, updateId, interest, null);
  }

  /**
   * Persists the {@code stateObject} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param stateSources the Object to persist with the domain events that were its source of truth
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persist(final StateSources<T,E> stateSources, final long updateId, final PersistResultInterest interest, final Object object) {
    persist(stateSources, Metadata.nullMetadata(), updateId, interest, object);
  }

  /**
   * Persists the {@code stateObject} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param stateSources the Object to persist with the domain events that were its source of truth
   * @param metadata the Metadata associated with the stateObject and sources
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  <T extends StateObject, E> void persist(final StateSources<T,E> stateSources, final Metadata metadata, final long updateId, final PersistResultInterest interest, final Object object);


  /**
   * Persists the new {@code stateObjects} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param allStateSources the Objects to persist, each with the domain events that were its source of truth
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persistAll(final Collection<StateSources<T,E>> allStateSources, final PersistResultInterest interest) {
    persistAll(allStateSources, Metadata.nullMetadata(), -1, interest, null);
  }

  /**
   * Persists the new {@code stateObjects} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param allStateSources the Objects to persist, each with the domain events that were its source of truth
   * @param metadata the Metadata associated with the stateObjects and sources
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persistAll(final Collection<StateSources<T,E>> allStateSources, final Metadata metadata, final PersistResultInterest interest) {
    persistAll(allStateSources, metadata, -1, interest, null);
  }

  /**
   * Persists the new {@code stateObjects}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param allStateSources the Objects to persist, each with the domain events that were its source of truth
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persistAll(final Collection<StateSources<T,E>> allStateSources, final PersistResultInterest interest, final Object object) {
    persistAll(allStateSources, Metadata.nullMetadata(), -1, interest, object);
  }

  /**
   * Persists the new {@code stateObjects} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param allStateSources the Objects to persist, each with the domain events that were its source of truth
   * @param sources the domain events to journal related to <code>stateObjects</code>
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persistAll(final Collection<StateSources<T,E>> allStateSources, final List<Source<E>> sources, final PersistResultInterest interest, final Object object) {
    persistAll(allStateSources, Metadata.nullMetadata(), -1, interest, object);
  }

  /**
   * Persists the new {@code stateObjects} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param allStateSources the Objects to persist, each with the domain events that were its source of truth
   * @param metadata the Metadata associated with the stateObjects and sources
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persistAll(final Collection<StateSources<T,E>> allStateSources, final Metadata metadata, final PersistResultInterest interest, final Object object) {
    persistAll(allStateSources, metadata, -1, interest, object);
  }

  /**
   * Persists the {@code stateObjects} with {@code sources}
   * as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param allStateSources the Objects to persist, each with the domain events that were its source of truth
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persistAll(final Collection<StateSources<T,E>> allStateSources, final long updateId, final PersistResultInterest interest) {
    persistAll(allStateSources, Metadata.nullMetadata(), updateId, interest, null);
  }

  /**
   * Persists the {@code stateObjects} with {@code sources} and {@code metadata}
   * as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param allStateSources the Objects to persist, each with the domain events that were its source of truth
   * @param metadata the Metadata associated with the stateObjects and sources
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persistAll(final Collection<StateSources<T,E>> allStateSources, final Metadata metadata, final long updateId, final PersistResultInterest interest) {
    persistAll(allStateSources, metadata, updateId, interest, null);
  }

  /**
   * Persists the {@code stateObjects} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param allStateSources the Objects to persist, each with the domain events that were its source of truth
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persistAll(final Collection<StateSources<T,E>> allStateSources, final long updateId, final PersistResultInterest interest, final Object object) {
    persistAll(allStateSources, Metadata.nullMetadata(), updateId, interest, object);
  }

  /**
   * Persists the {@code stateObjects} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param allStateSources the Objects to persist, each with the domain events that were its source of truth
   * @param metadata the Metadata associated with the stateObjects and sources
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  <T extends StateObject, E> void persistAll(final Collection<StateSources<T,E>> allStateSources, final Metadata metadata, final long updateId, final PersistResultInterest interest, final Object object);

  /**
   * Defines the result of persisting to the store with a persistent object.
   */
  public static interface PersistResultInterest {
    /**
     * Implemented by the interest of a given Object Store for persist operation results.
     * @param outcome the {@code Outcome<StorageException,Result>} of the persist operation
     * @param stateObject the Object to persist; for persistAll() this will be a {@code Collection<Object>}
     * @param possible the int number of possible objects to persist
     * @param actual the int number of actual objects persisted
     * @param object the Object passed to persist() that is sent back to the receiver, or null if not passed
     */
    void persistResultedIn(final Outcome<StorageException,Result> outcome, final Object stateObject, final int possible, final int actual, final Object object);
  }
}
