// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.hsqldb;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;

import io.vlingo.actors.Logger;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.StateStore.DataFormat;
import io.vlingo.symbio.store.state.StateStore.StorageDelegate;
import io.vlingo.symbio.store.state.jdbc.CachedStatement;
import io.vlingo.symbio.store.state.jdbc.Configuration;
import io.vlingo.symbio.store.state.jdbc.JDBCDispatchableCachedStatements;
import io.vlingo.symbio.store.state.jdbc.JDBCStorageDelegate;

public class HSQLDBStorageDelegate extends JDBCStorageDelegate<Blob> implements StorageDelegate, HSQLDBQueries {

  public HSQLDBStorageDelegate(final Configuration configuration, final Logger logger) {

    super(configuration.connection,
          configuration.format,
          configuration.originatorId,
          configuration.createTables,
          logger);
  }

  @Override
  protected byte[] binaryDataFrom(final ResultSet resultSet, final int columnIndex) throws Exception {
    final Blob blob = resultSet.getBlob(columnIndex);
    final byte[] data = blob.getBytes(1, (int) blob.length());
    return data;
  }

  @Override
  protected void createTables() {
    createTables(connection, format, logger);
  }

  @Override
  @SuppressWarnings("unchecked")
  protected <D> D dataTypeObject() throws Exception {
    return (D) connection.createBlob();
  }

  @Override
  protected JDBCDispatchableCachedStatements<Blob> dispatchableCachedStatements() {
    return new HSQLDBDispatchableCachedStatements(originatorId, connection, format, logger);
  }

  @Override
  protected String readExpression(final String storeName, final String id) {
    return MessageFormat.format(SQL_STATE_READ, storeName.toUpperCase());
  }

  @Override
  protected <S> void setBinaryObject(final CachedStatement<Blob> cached, int columnIndex, State<S> state) throws Exception {
    final byte[] data = (byte[]) state.data;
    cached.data.setBytes(1, data);
    cached.preparedStatement.setBlob(columnIndex, cached.data);
  }

  @Override
  protected <S> void setTextObject(final CachedStatement<Blob> cached, int columnIndex, State<S> state) throws Exception {
    cached.preparedStatement.setString(columnIndex, (String) state.data);
  }

  @Override
  protected String textDataFrom(final ResultSet resultSet, final int columnIndex) throws Exception {
    final String data = resultSet.getString(columnIndex);
    return data;
  }

  @Override
  protected String writeExpression(String storeName) {
    return MessageFormat.format(SQL_STATE_WRITE, storeName.toUpperCase(),
            format.isBinary() ? SQL_FORMAT_BINARY_CAST : SQL_FORMAT_TEXT_CAST);
  }

  private void createDispatchablesTable(final Connection connection, final DataFormat format) throws Exception {
    if (!tableExists(connection, TBL_VLINGO_SYMBIO_DISPATCHABLES)) {
      Statement statement = null;
      try {
        final String createDispatchablesStore = MessageFormat.format(SQL_CREATE_DISPATCHABLES_STORE,
                format.isBinary() ? SQL_FORMAT_BINARY : SQL_FORMAT_TEXT);
        statement = connection.createStatement();
        statement.executeUpdate(createDispatchablesStore);
        statement.executeUpdate(SQL_DISPATCH_ID_INDEX);
        statement.executeUpdate(SQL_ORIGINATOR_ID_INDEX);
        connection.commit();
      } finally {
        if (statement != null) {
          statement.close();
        }
      }
    }
  }

  private void createTables(final Connection connection, final DataFormat format, final Logger logger) {
    try {
      createDispatchablesTable(connection, format);
    } catch (Exception e) {
      // assume table exists; could look at metadata
      logger.log("Could not create dispatchables table because: " + e.getMessage(), e);
    }

    for (final String storeName : StateTypeStateStoreMap.allStoreNames()) {
      final String postfixTableName = storeName.toUpperCase();
      try {
        if (!tableExists(connection, tableNameFor(postfixTableName))) {
          createStateStoreTable(connection, postfixTableName, format);
        }
      } catch (Exception e) {
        // assume table exists; could look at metadata
        logger.log("Could not create " + postfixTableName + " table because: " + e.getMessage(), e);
      }
    }
  }

  private void createStateStoreTable(final Connection connection, final String tableName, final DataFormat format) throws Exception {
    final String sql = MessageFormat.format(SQL_CREATE_STATE_STORE, tableName,
            format.isBinary() ? SQL_FORMAT_BINARY : SQL_FORMAT_TEXT);

    Statement statement = null;
    try {
      statement = connection.createStatement();
      statement.executeUpdate(sql);
      connection.commit();
    } finally {
      if (statement != null) {
        statement.close();
      }
    }
  }

  private boolean tableExists(final Connection connection, String tableName) throws Exception {
    DatabaseMetaData metadata = connection.getMetaData();
    try (final ResultSet resultSet = metadata.getTables(null, null, tableName, null)) {
      return resultSet.next();
    }
  }

  private String tableNameFor(final String storeName) {
    return "TBL_" + storeName;
  }

  private static Blob blobIfBinary(final Connection connection, DataFormat format, final Logger logger) {
    try {
      return format.isBinary() ? connection.createBlob() : null;
    } catch (SQLException e) {
      final String message =
              HSQLDBDispatchableCachedStatements.class.getSimpleName() + ": Failed to create blob because: " + e.getMessage();
      logger.log(message, e);
      throw new IllegalStateException(message);
    }
  }

  class HSQLDBDispatchableCachedStatements extends JDBCDispatchableCachedStatements<Blob> {
    HSQLDBDispatchableCachedStatements(
            final String originatorId,
            final Connection connection,
            final DataFormat format,
            final Logger logger) {

      super(originatorId, connection, format, blobIfBinary(connection, format, logger), logger);
    }

    @Override
    protected String appendExpression() {
      return SQL_DISPATCHABLE_APPEND;
    }

    @Override
    protected String deleteExpression() {
      return SQL_DISPATCHABLE_DELETE;
    }

    @Override
    protected String selectExpression() {
      return SQL_DISPATCHABLE_SELECT;
    }
  }
}
