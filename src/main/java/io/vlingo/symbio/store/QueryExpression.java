// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store;

/**
 * The base query expression.
 */
public class QueryExpression {
  public final QueryMode mode;
  public final String query;
  public final Class<?> type;

  /**
   * Answer a new {@code QueryExpression} with {@code type} and {@code query}.
   * @param type the {@code Class<?>} of the object(s) to query
   * @param query the String expression of the query
   * @return QueryExpression
   */
  public static QueryExpression using(final Class<?> type, final String query) {
    return new QueryExpression(type, query);
  }

  /**
   * Answer a new {@code QueryExpression} with {@code type}, {@code query}, and {@code mode}.
   * @param type the {@code Class<?>} of the object(s) to query
   * @param query the String expression of the query
   * @param mode the QueryMode
   * @return QueryExpression
   */
  public static QueryExpression using(final Class<?> type, final String query, final QueryMode mode) {
    return new QueryExpression(type, query, mode);
  }

  /**
   * Constructs my default state with {@code QueryMode.ReadOnly}.
   * @param type the {@code Class<?>} of the objects to be queried
   * @param query the String describing the query
   */
  public QueryExpression(final Class<?> type, final String query) {
    this(type, query, QueryMode.ReadOnly);
  }

  /**
   * Constructs my default state.
   * @param type the {@code Class<?>} of the objects to be queried
   * @param query the String describing the query
   * @param mode the QueryMode
   */
  public QueryExpression(final Class<?> type, final String query, final QueryMode mode) {
    this.type = type;
    this.query = query;
    this.mode = mode;
  }

  /**
   * Answer myself as a {@code ListQueryExpression}.
   * @return ListQueryExpression
   */
  public ListQueryExpression asListQueryExpression() {
    return (ListQueryExpression) this;
  }

  /**
   * Answer myself as a {@code MapQueryExpression}.
   * @return ListQueryExpression
   */
  public MapQueryExpression asMapQueryExpression() {
    return (MapQueryExpression) this;
  }

  /**
   * Answer whether or not I am a {@code ListQueryExpression}.
   * @return boolean
   */
  public boolean isListQueryExpression() {
    return false;
  }

  /**
   * Answer whether or not I am a {@code MapQueryExpression}.
   * @return boolean
   */
  public boolean isMapQueryExpression() {
    return false;
  }

  @Override
  public String toString() {
    return "QueryExpression[type=" + type.getName() + " query=" + query + " mode=" + mode + "]";
  }
}
