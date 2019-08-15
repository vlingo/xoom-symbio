// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.object;

import java.util.Collection;

import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.EntryReader;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
/**
 * ObjectStoreReader defines protocol for reading from an {@link ObjectStore}.
 */
public interface ObjectStoreReader {

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

  public Completes<EntryReader<? extends Entry<?>>> entryReader(final String name);

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

