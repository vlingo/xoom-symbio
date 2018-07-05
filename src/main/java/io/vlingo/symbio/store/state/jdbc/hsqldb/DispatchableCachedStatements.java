// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.hsqldb;

import static io.vlingo.symbio.store.state.jdbc.hsqldb.HSQLDBStore.SQL_DISPATCHABLE_APPEND;
import static io.vlingo.symbio.store.state.jdbc.hsqldb.HSQLDBStore.SQL_DISPATCHABLE_DELETE;
import static io.vlingo.symbio.store.state.jdbc.hsqldb.HSQLDBStore.SQL_DISPATCHABLE_SELECT;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.vlingo.actors.Logger;
import io.vlingo.symbio.store.state.StateStore.DataFormat;

class DispatchableCachedStatements {
  final CachedBlobCapableStatement append;
  final CachedBlobCapableStatement delete;
  final CachedBlobCapableStatement queryAll;

  DispatchableCachedStatements(final String originatorId, final Connection connection, final DataFormat format, final Logger logger) {
    this.append = createStatement(SQL_DISPATCHABLE_APPEND, blobIfBinary(connection, format, logger), connection, logger);
    this.delete = createStatement(SQL_DISPATCHABLE_DELETE, null, connection, logger);
    this.queryAll = createStatement(SQL_DISPATCHABLE_SELECT, null, connection, logger);
    prepareQuery(originatorId, logger);
  }

  private Blob blobIfBinary(final Connection connection, DataFormat format, final Logger logger) {
    try {
      return format.isBinary() ? connection.createBlob() : null;
    } catch (SQLException e) {
      final String message =
              getClass().getSimpleName() + ": Failed to create blob because: " + e.getMessage();
      logger.log(message, e);
      throw new IllegalStateException(message);
    }
  }

  private CachedBlobCapableStatement createStatement(final String sql, final Blob blob, final Connection connection, final Logger logger) {
    try {
      final PreparedStatement preparedStatement = connection.prepareStatement(sql);
      return new CachedBlobCapableStatement(preparedStatement, blob);
    } catch (Exception e) {
      final String message =
              getClass().getSimpleName() + ": Failed to create dispatchable statement: \n" +
              sql +
              "\nbecause: " + e.getMessage();
      logger.log(message, e);
      throw new IllegalStateException(message);
    }
  }

  private void prepareQuery(String originatorId, final Logger logger) {
    try {
      this.queryAll.preparedStatement.setString(1, originatorId);
    } catch (SQLException e) {
      final String message =
              getClass().getSimpleName() + ": Failed to prepare query=all because: " + e.getMessage();
      logger.log(message, e);
      throw new IllegalStateException(message);
    }     
  }
}
