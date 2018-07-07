// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.postgres;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;

import org.postgresql.util.PGobject;

import io.vlingo.actors.Logger;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.StateStore.DataFormat;
import io.vlingo.symbio.store.state.StateStore.StorageDelegate;
import io.vlingo.symbio.store.state.jdbc.CachedStatement;
import io.vlingo.symbio.store.state.jdbc.Configuration;
import io.vlingo.symbio.store.state.jdbc.JDBCDispatchableCachedStatements;
import io.vlingo.symbio.store.state.jdbc.JDBCStorageDelegate;

public class PostgresStorageDelegate extends JDBCStorageDelegate<Object> implements StorageDelegate, PostgresQueries {
  public PostgresStorageDelegate(final Configuration configuration, final Logger logger) {

    super(configuration.connection,
          configuration.format,
          configuration.originatorId,
          configuration.createTables,
          logger);
  }

  @Override
  protected byte[] binaryDataFrom(final ResultSet resultSet, final int columnIndex) throws Exception {
    final byte[] data = resultSet.getBytes(5);
    return data;
  }

  @Override
  protected void createTables() {
    createTables(connection, format, logger);
  }

  @Override
  protected <D> D dataTypeObject() {
    return null;
  }

  @Override
  protected JDBCDispatchableCachedStatements<Object> dispatchableCachedStatements() {
    return new PostgresDispatchableCachedStatements<Object>(originatorId, connection, format, logger);
  }

  @Override
  protected String readExpression(final String storeName, final String id) {
    return MessageFormat.format(SQL_STATE_READ, storeName.toLowerCase());
  }

  @Override
  protected <S> void setBinaryObject(final CachedStatement<Object> cached, int columnIndex, final State<S> state) throws Exception {
    cached.preparedStatement.setBytes(columnIndex, (byte[]) state.data);
  }

  @Override
  protected <S> void setTextObject(final CachedStatement<Object> cached, int columnIndex, State<S> state) throws Exception {
    final PGobject jsonObject = new PGobject();
    jsonObject.setType("json");
    jsonObject.setValue((String) state.data);
    cached.preparedStatement.setObject(columnIndex, jsonObject);
  }

  @Override
  protected String textDataFrom(final ResultSet resultSet, final int columnIndex) throws Exception {
    final String text = resultSet.getObject(columnIndex).toString();
    return text;
  }

  @Override
  protected String writeExpression(final String storeName) {
    return MessageFormat.format(SQL_STATE_WRITE, storeName.toLowerCase(),
            format.isBinary() ? SQL_FORMAT_BINARY_CAST : SQL_FORMAT_TEXT_CAST);
  }

  private String namedDispatchable(final String sql) {
    return MessageFormat.format(sql, TBL_VLINGO_SYMBIO_DISPATCHABLES);
  }

  private void createDispatchablesTable(final Connection connection, final DataFormat format) throws Exception {
    if (!tableExists(connection, TBL_VLINGO_SYMBIO_DISPATCHABLES)) {
      Statement statement = null;
      try {
        final String createDispatchablesStore = MessageFormat.format(SQL_CREATE_DISPATCHABLES_STORE,
                TBL_VLINGO_SYMBIO_DISPATCHABLES,
                format.isBinary() ? SQL_FORMAT_BINARY : SQL_FORMAT_TEXT1); // TODO: SQL_FORMAT_TEXT2

        statement = connection.createStatement();
        statement.executeUpdate(createDispatchablesStore);
        statement.executeUpdate(namedDispatchable(SQL_DISPATCH_ID_INDEX));
        statement.executeUpdate(namedDispatchable(SQL_ORIGINATOR_ID_INDEX));
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
      final String tableName = tableNameFor(storeName.toLowerCase());
      try {
        if (!tableExists(connection, tableName)) {
          createStateStoreTable(connection, tableName, format);
        }
      } catch (Exception e) {
        // assume table exists; could look at metadata
        logger.log("Could not create " + tableName + " table because: " + e.getMessage(), e);
      }
    }
  }

  private void createStateStoreTable(final Connection connection, final String tableName, final DataFormat format) throws Exception {
    final String sql = MessageFormat.format(SQL_CREATE_STATE_STORE, tableName,
            format.isBinary() ? SQL_FORMAT_BINARY : SQL_FORMAT_TEXT1); // TODO: SQL_FORMAT_TEXT2

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
    final DatabaseMetaData metadata = connection.getMetaData();
    try (final ResultSet resultSet = metadata.getTables(null, null, tableName, null)) {
      return resultSet.next();
    }
  }

  private String tableNameFor(final String storeName) {
    return "tbl_" + storeName.toLowerCase();
  }

  class PostgresDispatchableCachedStatements<T> extends JDBCDispatchableCachedStatements<T> {
    PostgresDispatchableCachedStatements(
            final String originatorId,
            final Connection connection,
            final DataFormat format,
            final Logger logger) {
  
      super(originatorId, connection, format, null, logger);
    }

    @Override
    protected String appendExpression() {
      return namedDispatchable(SQL_DISPATCHABLE_APPEND);
    }

    @Override
    protected String deleteExpression() {
      return namedDispatchable(SQL_DISPATCHABLE_DELETE);
    }

    @Override
    protected String selectExpression() {
      return namedDispatchable(SQL_DISPATCHABLE_SELECT);
    }
  }
}
