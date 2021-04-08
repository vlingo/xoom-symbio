// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A query expression whose parameters are provided in a {@code List}.
 */
public class ListQueryExpression extends QueryExpression {
  public final List<?> parameters;

  /**
   * Answer a new {@code ListQueryExpression} with {@code type}, {@code query}, and {@code parameters}.
   * @param type the {@code Class<?>} of the objects to be queried
   * @param query the String describing the query
   * @param parameters the {@code List<?>} containing query parameters
   * @return ListQueryExpression
   */
  public static ListQueryExpression using(final Class<?> type, final String query, final List<?> parameters) {
    return new ListQueryExpression(type, query, parameters);
  }

  /**
   * Answer a new {@code ListQueryExpression} with {@code type}, {@code query}, and {@code parameters}.
   * @param type the {@code Class<?>} of the objects to be queried
   * @param query the String describing the query
   * @param mode the QueryMode
   * @param parameters the {@code List<?>} containing query parameters
   * @return ListQueryExpression
   */
  public static ListQueryExpression using(final Class<?> type, final String query, final QueryMode mode, final List<?> parameters) {
    return new ListQueryExpression(type, query, mode, parameters);
  }

  /**
   * Constructs my default state.
   * @param type the {@code Class<?>} of the objects to be queried
   * @param query the String describing the query
   * @param parameters the {@code List<?>} containing query parameters
   */
  public ListQueryExpression(final Class<?> type, final String query, final List<?> parameters) {
    super(type, query);
    this.parameters = parameters;
  }

  /**
   * Constructs my default state.
   * @param type the {@code Class<?>} of the objects to be queried
   * @param query the String describing the query
   * @param mode the QueryMode
   * @param parameters the {@code List<?>} containing query parameters
   */
  public ListQueryExpression(final Class<?> type, final String query, final QueryMode mode, final List<?> parameters) {
    super(type, query, mode);
    this.parameters = parameters;
  }

  /**
   * Constructs my default state with {@code QueryMode.ReadOnly}.
   * @param type the {@code Class<?>} of the objects to be queried
   * @param query the String describing the query
   * @param parameters the varargs of {@code Object} containing query parameters
   */
  public ListQueryExpression(final Class<?> type, final String query, final Object... parameters) {
    super(type, query);
    this.parameters = Collections.unmodifiableList(Arrays.asList(parameters));
  }

  /**
   * Constructs my default state.
   * @param type the {@code Class<?>} of the objects to be queried
   * @param query the String describing the query
   * @param mode the QueryMode
   * @param parameters the varargs of {@code Object} containing query parameters
   */
  public ListQueryExpression(final Class<?> type, final String query, final QueryMode mode, final Object... parameters) {
    super(type, query, mode);
    this.parameters = Collections.unmodifiableList(Arrays.asList(parameters));
  }

  /*
   * @see io.vlingo.xoom.symbio.store.object.ObjectStore.QueryExpression#isListQueryExpression()
   */
  @Override
  public boolean isListQueryExpression() {
    return true;
  }

  @Override
  public String toString() {
    return "ListQueryExpression[type=" + type.getName() + " query=" + query + " mode=" + mode + " parameters=" + parameters + "]";
  }
}
