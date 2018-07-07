// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;

import io.vlingo.actors.Logger;
import io.vlingo.symbio.store.state.StateStore.DataFormat;

public abstract class JDBCDispatchableCachedStatements<T> {
  private final CachedStatement<T> append;
  private final CachedStatement<T> delete;
  private final CachedStatement<T> queryAll;

  protected JDBCDispatchableCachedStatements(
          final String originatorId,
          final Connection connection,
          final DataFormat format,
          final T appendDataObject,
          final Logger logger) {
    this.append = createStatement(appendExpression(), appendDataObject, connection, logger);
    this.delete = createStatement(deleteExpression(), null, connection, logger);
    this.queryAll = prepareQuery(createStatement(selectExpression(), null, connection, logger), originatorId, logger);
  }

  public final CachedStatement<T> appendStatement() {
    return append;
  }

  public final CachedStatement<T> deleteStatement() {
    return delete;
  }

  public final CachedStatement<T> queryAllStatement() {
    return queryAll;
  }

  protected abstract String appendExpression();
  protected abstract String deleteExpression();
  protected abstract String selectExpression();

  private CachedStatement<T> createStatement(
          final String sql,
          final T data,
          final Connection connection,
          final Logger logger) {

    try {
      final PreparedStatement preparedStatement = connection.prepareStatement(sql);
      return new CachedStatement<T>(preparedStatement, data);
    } catch (Exception e) {
      final String message =
              getClass().getSimpleName() + ": Failed to create dispatchable statement: \n" +
              sql +
              "\nbecause: " + e.getMessage();
      logger.log(message, e);
      throw new IllegalStateException(message);
    }
  }

  private CachedStatement<T> prepareQuery(final CachedStatement<T> cached, String originatorId, final Logger logger) {
    try {
      cached.preparedStatement.setString(1, originatorId);
      return cached;
    } catch (Exception e) {
      final String message =
              getClass().getSimpleName() + ": Failed to prepare query=all because: " + e.getMessage();
      logger.log(message, e);
      throw new IllegalStateException(message);
    }     
  }
}
