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
   * Persists the new {@code persistentObject}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param persistentObject the Object to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject> void persist(final T persistentObject, final PersistResultInterest interest) {
    persist(persistentObject, Source.none(), Metadata.nullMetadata(), -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObject} with {@code metadata}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param persistentObject the Object to persist
   * @param metadata the Metadata associated with the persistentObject and sources
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject> void persist(final T persistentObject, final Metadata metadata, final PersistResultInterest interest) {
    persist(persistentObject, Source.none(), metadata, -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObject} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persist(final T persistentObject, final List<Source<E>> sources, final PersistResultInterest interest) {
    persist(persistentObject, sources, Metadata.nullMetadata(), -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObject} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param metadata the Metadata associated with the persistentObject and sources
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persist(final T persistentObject, final List<Source<E>> sources, final Metadata metadata, final PersistResultInterest interest) {
    persist(persistentObject, sources, metadata, -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObject}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param persistentObject the Object to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject> void persist(final T persistentObject, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, Source.none(), Metadata.nullMetadata(), -1, interest, object);
  }

  /**
   * Persists the new {@code persistentObject} with {@code metadata}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param persistentObject the Object to persist
   * @param metadata the Metadata associated with the persistentObject and sources
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject> void persist(final T persistentObject, final Metadata metadata, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, Source.none(), metadata, -1, interest, object);
  }

  /**
   * Persists the new {@code persistentObject} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persist(final T persistentObject, final List<Source<E>> sources, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, sources, Metadata.nullMetadata(), -1, interest, object);
  }

  /**
   * Persists the new {@code persistentObject} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param metadata the Metadata associated with the persistentObject and sources
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persist(final T persistentObject, final List<Source<E>> sources, final Metadata metadata, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, sources, metadata, -1, interest, object);
  }

  /**
   * Persists the {@code persistentObject} as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param persistentObject the Object to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject> void persist(final T persistentObject, final long updateId, final PersistResultInterest interest) {
    persist(persistentObject, Source.none(), Metadata.nullMetadata(), updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObject} with {@code metadata}
   * as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param persistentObject the Object to persist
   * @param metadata the Metadata associated with the persistentObject
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject> void persist(final T persistentObject, final Metadata metadata, final long updateId, final PersistResultInterest interest) {
    persist(persistentObject, Source.none(), metadata, updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObject} with {@code sources} as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persist(final T persistentObject, final List<Source<E>> sources, final long updateId, final PersistResultInterest interest) {
    persist(persistentObject, sources, Metadata.nullMetadata(), updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObject} with {@code sources} and {@code metadata}
   * as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param metadata the Metadata associated with the persistentObject and sources
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persist(final T persistentObject, final List<Source<E>> sources, final Metadata metadata, final long updateId, final PersistResultInterest interest) {
    persist(persistentObject, sources, metadata, updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObject}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param persistentObject the Object to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject> void persist(final T persistentObject, final long updateId, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, Source.none(), Metadata.nullMetadata(), updateId, interest, object);
  }

  /**
   * Persists the {@code persistentObject} with {@code metadata}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param persistentObject the Object to persist
   * @param metadata the Metadata associated with the persistentObject
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject> void persist(final T persistentObject, final Metadata metadata, final long updateId, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, Source.none(), metadata, updateId, interest, object);
  }

  /**
   * Persists the {@code persistentObject} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persist(final T persistentObject, final List<Source<E>> sources, final long updateId, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, sources, Metadata.nullMetadata(), updateId, interest, object);
  }

  /**
   * Persists the {@code persistentObject} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject} to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObject the Object to persist
   * @param sources the domain events to journal related to <code>persistentObject</code>
   * @param metadata the Metadata associated with the persistentObject and sources
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  <T extends StateObject, E> void persist(final T persistentObject, final List<Source<E>> sources, final Metadata metadata, final long updateId, final PersistResultInterest interest, final Object object);


  /**
   * Persists the new {@code persistentObjects}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject> void persistAll(final Collection<T> persistentObjects, final PersistResultInterest interest) {
    persistAll(persistentObjects, Source.none(), Metadata.nullMetadata(), -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObjects} with {@code metadata}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param metadata the Metadata associated with the persistentObject
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject> void persistAll(final Collection<T> persistentObjects, final Metadata metadata, final PersistResultInterest interest) {
    persistAll(persistentObjects, Source.none(), metadata, -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObjects} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persistAll(final Collection<T> persistentObjects, final List<Source<E>> sources, final PersistResultInterest interest) {
    persistAll(persistentObjects, sources, Metadata.nullMetadata(), -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObjects} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param metadata the Metadata associated with the persistentObjects and sources
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persistAll(final Collection<T> persistentObjects, final List<Source<E>> sources, final Metadata metadata, final PersistResultInterest interest) {
    persistAll(persistentObjects, sources, metadata, -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObjects}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject> void persistAll(final Collection<T> persistentObjects, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, Source.none(), Metadata.nullMetadata(), -1, interest, object);
  }

  /**
   * Persists the new {@code persistentObjects} with {@code metadata}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param metadata the Metadata associated with the persistentObjects
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject> void persistAll(final Collection<T> persistentObjects, final Metadata metadata, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, Source.none(), metadata, -1, interest, object);
  }

  /**
   * Persists the new {@code persistentObjects} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persistAll(final Collection<T> persistentObjects, final List<Source<E>> sources, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, sources, Metadata.nullMetadata(), -1, interest, object);
  }

  /**
   * Persists the new {@code persistentObjects} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param metadata the Metadata associated with the persistentObjects and sources
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persistAll(final Collection<T> persistentObjects, final List<Source<E>> sources, final Metadata metadata, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, sources, metadata, -1, interest, object);
  }

  /**
   * Persists the {@code persistentObjects} as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject> void persistAll(final Collection<T> persistentObjects, final long updateId, final PersistResultInterest interest) {
    persistAll(persistentObjects, Source.none(), Metadata.nullMetadata(), updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObjects} with {@code metadata}
   * as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param metadata the Metadata associated with the persistentObjects
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject> void persistAll(final Collection<T> persistentObjects, final Metadata metadata, final long updateId, final PersistResultInterest interest) {
    persistAll(persistentObjects, Source.none(), metadata, updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObjects} with {@code sources}
   * as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persistAll(final Collection<T> persistentObjects, final List<Source<E>> sources, final long updateId, final PersistResultInterest interest) {
    persistAll(persistentObjects, sources, Metadata.nullMetadata(), updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObjects} with {@code sources} and {@code metadata}
   * as new or updated depending on the value of {@code updateId}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param metadata the Metadata associated with the persistentObjects and sources
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default <T extends StateObject, E> void persistAll(final Collection<T> persistentObjects, final List<Source<E>> sources, final Metadata metadata, final long updateId, final PersistResultInterest interest) {
    persistAll(persistentObjects, sources, metadata, updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObjects}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject> void persistAll(final Collection<T> persistentObjects, final long updateId, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, Source.none(), Metadata.nullMetadata(), updateId, interest, object);
  }

  /**
   * Persists the {@code persistentObjects} with {@code metadata}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param metadata the Metadata associated with the persistentObjects
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject> void persistAll(final Collection<T> persistentObjects, final Metadata metadata, final long updateId, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, Source.none(), metadata, updateId, interest, object);
  }

  /**
   * Persists the {@code persistentObjects} with {@code sources}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default <T extends StateObject, E> void persistAll(final Collection<T> persistentObjects, final List<Source<E>> sources, final long updateId, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, sources, Metadata.nullMetadata(), updateId, interest, object);
  }

  /**
   * Persists the {@code persistentObjects} with {@code sources} and {@code metadata}.
   * @param <T> the concrete type of {@link StateObject}s to persist
   * @param <E> the concrete type of the {@link Source}
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param sources the domain events to journal related to <code>persistentObjects</code>
   * @param metadata the Metadata associated with the persistentObjects and sources
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  <T extends StateObject, E> void persistAll(final Collection<T> persistentObjects, final List<Source<E>> sources, final Metadata metadata, final long updateId, final PersistResultInterest interest, final Object object);

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
