// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.object;

import java.util.Collection;

import io.vlingo.common.Outcome;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
/**
 * ObjectStoreWriter defines protocol for writing to an {@link ObjectStore}.
 */
public interface ObjectStoreWriter {
  
  /**
   * Persists the new {@code persistentObject}.
   * @param persistentObject the Object to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default void persist(final Object persistentObject, final PersistResultInterest interest) {
    persist(persistentObject, -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObject}.
   * @param persistentObject the Object to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default void persist(final Object persistentObject, final PersistResultInterest interest, final Object object) {
    persist(persistentObject, -1, interest, object);
  }

  /**
   * Persists the {@code persistentObject} as new or updated depending on the value of {@code updateId}.
   * @param persistentObject the Object to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default void persist(final Object persistentObject, final long updateId, final PersistResultInterest interest) {
    persist(persistentObject, updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObject}.
   * @param persistentObject the Object to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  void persist(final Object persistentObject, final long updateId, final PersistResultInterest interest, final Object object);


  /**
   * Persists the new {@code persistentObjects}.
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default void persistAll(final Collection<Object> persistentObjects, final PersistResultInterest interest) {
    persistAll(persistentObjects, -1, interest, null);
  }

  /**
   * Persists the new {@code persistentObjects}.
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  default void persistAll(final Collection<Object> persistentObjects, final PersistResultInterest interest, final Object object) {
    persistAll(persistentObjects, -1, interest, object);
  }

  /**
   * Persists the {@code persistentObjects} as new or updated depending on the value of {@code updateId}.
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   */
  default void persistAll(final Collection<Object> persistentObjects, final long updateId, final PersistResultInterest interest) {
    persistAll(persistentObjects, updateId, interest, null);
  }

  /**
   * Persists the {@code persistentObjects}.
   * @param persistentObjects the {@code Collection<Object>} to persist
   * @param updateId the long identity to facilitate update; &lt; 0 for create &gt; 0 for update
   * @param interest the PersistResultInterest to which the result is dispatched
   * @param object an Object sent to the PersistResultInterest when the persist has succeeded or failed
   */
  void persistAll(final Collection<Object> persistentObjects, final long updateId, final PersistResultInterest interest, final Object object);

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
