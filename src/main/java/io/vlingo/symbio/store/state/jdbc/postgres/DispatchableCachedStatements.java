// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.postgres;

import static io.vlingo.symbio.store.state.jdbc.postgres.PostgresStore.namedDispatchable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import io.vlingo.actors.Logger;
import io.vlingo.symbio.store.state.StateStore.DataFormat;
import io.vlingo.symbio.store.state.jdbc.CachedStatement;

class DispatchableCachedStatements {
  final static String SQL_DISPATCHABLE_APPEND =
          "INSERT INTO {0} \n" +
               "(d_id, d_originaltor_id, d_dispatch_id, \n" +
               " d_state_id, d_state_type, d_state_type_version, \n" +
               " d_state_data, d_state_data_version, \n" +
               " d_state_metadata_value, d_state_metadata_op, d_state_metadata_object, d_state_metadata_object_type) \n" +
               "VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

  final static String SQL_DISPATCHABLE_DELETE =
          "DELETE FROM {0} WHERE d_dispatch_id = ?";

  final static String SQL_DISPATCHABLE_SELECT =
          "SELECT d_dispatch_id, d_state_id, d_state_type, d_state_type_version, d_state_data, d_state_data_version, \n" +
          "       d_state_metadata_value, d_state_metadata_op, d_state_metadata_object, d_state_metadata_object_type \n" +
          "FROM {0} \n" +
          "WHERE d_originaltor_id = ? ORDER BY D_ID";

  final CachedStatement append;
  final CachedStatement delete;
  final CachedStatement queryAll;

  DispatchableCachedStatements(final String originatorId, final Connection connection, final DataFormat format, final Logger logger) {
    this.append = createStatement(namedDispatchable(SQL_DISPATCHABLE_APPEND), connection, logger);
    this.delete = createStatement(namedDispatchable(SQL_DISPATCHABLE_DELETE), connection, logger);
    this.queryAll = createStatement(namedDispatchable(SQL_DISPATCHABLE_SELECT), connection, logger);
    prepareQuery(originatorId, logger);
  }

  private CachedStatement createStatement(final String sql, final Connection connection, final Logger logger) {
    try {
      final PreparedStatement preparedStatement = connection.prepareStatement(sql);
      return new CachedStatement(preparedStatement);
    } catch (Exception e) {
      final String message =
              getClass().getSimpleName() + ": Failed to create dispatchable statement: \n" +
              sql +
              "\nbecause: " + e.getMessage();
      logger.log(message, e);
      throw new IllegalStateException(message);
    }
  }

  private void prepareQuery(final String originatorId, final Logger logger) {
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
