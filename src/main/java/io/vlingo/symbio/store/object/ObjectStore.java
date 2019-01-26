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
 * An object storage in which persistent objects are self defined, including
 * their identity. This is often thought of as object-relational mapping,
 * which certainly can be and is supported, but is not limited to such.
 */
public interface ObjectStore {
  /**
   * The value given to non-identity ids.
   */
  public static final long NoId = -1L;

  /**
   * Answer whether or not {@code id} is the {@code NoId}.
   * @param id the identity to check
   * @return boolean
   */
  static boolean isNoId(final long id) {
    return NoId == id;
  }

  /**
   * Answer whether or not {@code id} is an identity.
   * @param id the identity to check
   * @return boolean
   */
  static boolean isId(final long id) {
    return id > NoId;
  }

  /**
   * Close me.
   */
  void close();

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
   * Executes the query defined by {@code expression} that may result in zero to many objects,
   * and sends the result to {@code interest}.
   * @param expression the QueryExpression
   * @param interest the QueryResultInterest
   */
  default void queryAll(final QueryExpression expression, final QueryResultInterest interest) {
    queryAll(expression, interest, null);
  }

  /**
   * Executes the query defined by {@code expression} that may result in zero to many objects,
   * and sends the result to {@code interest}.
   * @param expression the QueryExpression
   * @param interest the QueryResultInterest
   * @param object an Object sent to the QueryResultInterest when the query has succeeded or failed
   */
  void queryAll(final QueryExpression expression, final QueryResultInterest interest, final Object object);

  /**
   * Executes the query defined by {@code expression} that may result in one object,
   * and sends the result to {@code interest}.
   * @param expression the QueryExpression
   * @param interest the QueryResultInterest
   */
  default void queryObject(final QueryExpression expression, final QueryResultInterest interest) {
    queryObject(expression, interest, null);
  }

  /**
   * Executes the query defined by {@code expression} that may result in one object,
   * and sends the result to {@code interest}.
   * @param expression the QueryExpression
   * @param interest the QueryResultInterest
   * @param object an Object sent to the QueryResultInterest when the query has succeeded or failed
   */
  void queryObject(final QueryExpression expression, final QueryResultInterest interest, final Object object);

  /**
   * Register the {@code mapper} for a given persistent type.
   * @param mapper the PersistentObjectMapper
   */
  void registerMapper(final PersistentObjectMapper mapper);

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

  /**
   * Defines the result of querying one or more persistent objects.
   */
  public static interface QueryResultInterest {
    /**
     * Implemented by the interest of a given Object Store for a query operation with zero or more results.
     * @param outcome the {@code Outcome<StorageException,Result>} of the query
     * @param results the {@code MultiQueryResults} of the query, with zero or more objects and a possible updateId
     * @param object the Object passed to query() that is sent back to the receiver
     */
    void queryAllResultedIn(final Outcome<StorageException,Result> outcome, final QueryMultiResults results, final Object object);

    /**
     * Implemented by the interest of a given Object Store for an object query operation with a single result.
     * @param outcome the {@code Outcome<StorageException,Result>} of the query
     * @param result the {@code SingleQueryResult} of the query, with zero or one object and a possible updateId
     * @param object the Object passed to query() that is sent back to the receiver
     */
    void queryObjectResultedIn(final Outcome<StorageException,Result> outcome, final QuerySingleResult result, final Object object);
  }

  /**
   * The purpose of the query.
   */
  public static enum QueryMode {
    ReadOnly {
      @Override public boolean isReadOnly() { return true; }
    },
    ReadUpdate {
      @Override public boolean isReadUpdate() { return true; }
    };

    public boolean isReadOnly() {
      return false;
    }

    public boolean isReadUpdate() {
      return false;
    }
  }

  /**
   * Abstract base of query result types.
   */
  public static abstract class QueryResult {
    public final long updateId;

    protected QueryResult() {
      this(NoId);
    }

    protected QueryResult(final long updateId) {
      this.updateId = updateId;
    }

    public boolean isUpdatable() {
      return isId(updateId);
    }
  }

  /**
   * The single {@code persistentObject} result of the completed query and a possible {@code updateId}.
   */
  public static class QuerySingleResult extends QueryResult {
    public final Object persistentObject;

    public static QuerySingleResult of(final Object persistentObject) {
      return new QuerySingleResult(persistentObject);
    }

    public static QuerySingleResult of(final Object persistentObject, final long updateId) {
      return new QuerySingleResult(persistentObject, updateId);
    }

    public QuerySingleResult(final Object persistentObject) {
      super();
      this.persistentObject = persistentObject;
    }

    public QuerySingleResult(final Object persistentObject, final long updateId) {
      super(updateId);
      this.persistentObject = persistentObject;
    }

    @SuppressWarnings("unchecked")
    public <T> T persistentObject() {
      return (T) persistentObject;
    }
  }

  /**
   * The collection of {@code persistentObjects} results of the completed query and a possible {@code updateId}.
   */
  public static class QueryMultiResults extends QueryResult {
    public final Collection<?> persistentObjects;

    public static QueryMultiResults of(final Collection<?> persistentObjects) {
      return new QueryMultiResults(persistentObjects);
    }

    public static QueryMultiResults of(final Collection<?> persistentObjects, final long updateId) {
      return new QueryMultiResults(persistentObjects, updateId);
    }

    public QueryMultiResults(final Collection<?> persistentObjects) {
      super();
      this.persistentObjects = persistentObjects;
    }

    public QueryMultiResults(final Collection<?> persistentObjects, final long updateId) {
      super(updateId);
      this.persistentObjects = persistentObjects;
    }

    @SuppressWarnings("unchecked")
    public <T> Collection<T> persistentObjects() {
      return (Collection<T>) persistentObjects;
    }
  }
}
