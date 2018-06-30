// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc;

import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.hsqldb.server.Server;

import io.vlingo.actors.Logger;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.State.BinaryState;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.state.StateStore.DataFormat;
import io.vlingo.symbio.store.state.StateStore.StorageDelegate;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;

public class HSQLDBStorageDelegate implements StorageDelegate {
  private enum Mode { None, Reading, Writing };

  private final static String SQL_READ =
          "SELECT {0}.S_TYPE, {0}.S_TYPE_VERSION, {0}.S_DATA, {0}.S_DATA_VERSION, {0}.S_METADATA_OP, {0}.S_METADATA_VALUE " +
          "FROM {0} " +
          "WHERE {0}.S_ID = ?";

  private final static String SQL_WRITE =
          "MERGE INTO {0} \n" +
          "USING (VALUES ?, ?, ?, {1}, ?, ?, ?) \n" +
          "S (S_ID, S_TYPE, S_TYPE_VERSION, S_DATA, S_DATA_VERSION, S_METADATA_OP, S_METADATA_VALUE) \n" +
          "ON ({0}.S_ID = S.S_ID) \n" +
          "WHEN MATCHED THEN UPDATE \n" +
                  "SET {0}.S_TYPE = S.S_TYPE, \n" +
                  "    {0}.S_TYPE_VERSION = S.S_TYPE_VERSION, \n" +
                  "    {0}.S_DATA = S.S_DATA, \n" +
                  "    {0}.S_DATA_VERSION = S.S_DATA_VERSION, \n" +
                  "    {0}.S_METADATA_OP = S.S_METADATA_OP, \n" +
                  "    {0}.S_METADATA_VALUE = S.S_METADATA_VALUE \n" +
          "WHEN NOT MATCHED THEN INSERT \n" +
                  "(S_ID, S_TYPE, S_TYPE_VERSION, S_DATA, S_DATA_VERSION, S_METADATA_OP, S_METADATA_VALUE) \n" +
                  "VALUES (S.S_ID, S.S_TYPE, S.S_TYPE_VERSION, S.S_DATA, S.S_DATA_VERSION, S.S_METADATA_OP, S.S_METADATA_VALUE)";

  private final static String SQL_BINARY = "VARBINARY(63000)";
  private final static String SQL_BINARY_CAST = "CAST(? AS VARBINARY(63000))";
  private final static String SQL_TEXT = "TEXT(63000)";
  private final static String SQL_TEXT_CAST = "CAST(? AS TEXT(63000))";
  private final static String SQL_CREATE =
          "CREATE TABLE {0} (\n" +
          "   S_ID VARCHAR(64) NOT NULL,\n" +
          "   S_TYPE VARCHAR(256) NOT NULL,\n" +
          "   S_TYPE_VERSION INT NOT NULL,\n" +
          "   S_DATA {1} NOT NULL,\n" +
          "   S_DATA_VERSION INT NOT NULL,\n" +
          "   S_METADATA_VALUE VARCHAR(2048) NOT NULL,\n" +
          "   S_METADATA_OP VARCHAR(128) NOT NULL,\n" +
          "   PRIMARY KEY (S_ID) \n" +
          ");";

  private final Connection connection;
  private Server databaseSever;   // unused other than to prevent GC
  private final DataFormat format;
  private final Logger logger;
  private Mode mode;
  private final Map<String, CachedStatement> readStatements;
  private final Map<String, CachedStatement> writeStatements;

  private static void createTables(final Connection connection, final DataFormat format) {
    for (final String storeName : StateTypeStateStoreMap.allStoreNames()) {
      try {
        createTable(connection, storeName, format);
      } catch (Exception e) {
        // assume table exists; could look at metadata
      }
    }
  }

  private static void createTable(Connection connection, String tableName, DataFormat format) throws Exception {
    final String sql = MessageFormat.format(SQL_CREATE, tableName, format.isBinary() ? SQL_BINARY : SQL_TEXT);
    Statement statement = null;
    try {
      statement = connection.createStatement();
      statement.executeUpdate(sql);
      connection.commit();
    } catch (Exception e) {
      if (statement != null) {
        statement.close();
      }
    }
  }

  public HSQLDBStorageDelegate(
          final DataFormat format,
          final String url,
          final String username,
          final String password,
          final boolean createTables,
          final Logger logger) {

    this.format = format;
    this.connection = connect(url, username, password, logger);
    this.logger = logger;
    this.mode = Mode.None;
    this.readStatements = new HashMap<>();
    this.writeStatements = new HashMap<>();

    if (createTables) {
      createTables(connection, format);
    }
  }

  public HSQLDBStorageDelegate(final DataFormat format, final Logger logger) {
    this(format, "jdbc:hsqldb:mem:testdb;sql.syntax_mys=true", "SA", "", true, logger);
  }

  @Override
  public void beginRead() throws Exception {
    if (mode != Mode.None) {
      logger.log(getClass().getSimpleName() + ": Cannot begin read because currently: " + mode.name());
    } else {
      mode = Mode.Reading;
    }
  }

  @Override
  public void beginWrite() throws Exception {
    if (mode != Mode.None) {
      logger.log(getClass().getSimpleName() + ": Cannot begin write because currently: " + mode.name());
    } else {
      mode = Mode.Writing;
    }
  }

  @Override
  public void close() {
    try {
      mode = Mode.None;

      if (connection != null) {
        connection.close();
      }
    } catch (Exception e) {
      logger.log(getClass().getSimpleName() + ": Could not close because: " + mode.name(), e);
    }
  }

  @Override
  public void complete() throws Exception {
    mode = Mode.None;
    connection.commit();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C> C connection() throws Exception {
    return (C) connection;
  }

  @Override
  public void drop(final String storeName) throws Exception {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate("DROP TABLE " + storeName);
      connection.commit();
    }
  }

  @Override
  public void dropAll() {
    for (final String storeName : StateTypeStateStoreMap.allStoreNames()) {
      try {
        drop(storeName.toUpperCase());
      } catch (Exception e) {
        // assume table already dropped; could look at metadata
      }
    }
  }

  @Override
  public void fail() {
    try {
      mode = Mode.None;
      connection.rollback();
    } catch (Exception e) {
      logger.log(getClass().getSimpleName() + ": Rollback failed because: " + e.getMessage(), e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <E> E readExpressionFor(final String storeName, final String id) throws Exception {
    final CachedStatement maybeCached = readStatements.get(storeName);

    if (maybeCached == null) {
      final String select = MessageFormat.format(SQL_READ, storeName.toUpperCase());
      final PreparedStatement preparedStatement = connection.prepareStatement(select);
      final Blob blob = format.isBinary() ? connection.createBlob() : null;
      final CachedStatement cached = new CachedStatement(preparedStatement, blob);
      readStatements.put(storeName, cached);
      prepareForRead(cached, id);
      return (E) preparedStatement;
    }

    prepareForRead(maybeCached, id);

    return (E) maybeCached.preparedStatement;
  }

  @Override
  public <S> S session() throws Exception {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S, R> S stateFrom(final R result, final String id) throws Exception {
    final ResultSet resultSet = ((ResultSet) result);
    if (!resultSet.next()) {
      return (S) (format.isBinary() ? new BinaryState() : new TextState());
    }
    final Class<?> type = Class.forName(resultSet.getString(1));
    final int typeVersion = resultSet.getInt(2);
    final int dataVersion = resultSet.getInt(4);
    final String metadataOperation = resultSet.getString(5);
    final String metadataValue = resultSet.getString(6);

    final Metadata metadata = Metadata.with(metadataValue, metadataOperation);

    // note possible truncation with long cast to in, but
    // hopefully no objects are larger than int max value

    if (format.isBinary()) {
      final Blob blob = resultSet.getBlob(3);
      final byte[] data = new byte[(int) blob.length()];
      return (S) new BinaryState(id, type, typeVersion, data, dataVersion, metadata);
    } else {
      final Clob clob = resultSet.getClob(3);
      final String data = clob.getSubString(1, (int) clob.length());
      return (S) new TextState(id, type, typeVersion, data, dataVersion, metadata);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <W, S> W writeExpressionFor(final String storeName, final State<S> state) throws Exception {
    final CachedStatement maybeCached = writeStatements.get(storeName);

    if (maybeCached == null) {
      final String upsert = MessageFormat.format(SQL_WRITE, storeName.toUpperCase(),
              format.isBinary() ? SQL_BINARY_CAST : SQL_TEXT_CAST);
      final PreparedStatement preparedStatement = connection.prepareStatement(upsert);
      final Blob blob = format.isBinary() ? connection.createBlob() : null;
      final CachedStatement cached = new CachedStatement(preparedStatement, blob);
      writeStatements.put(storeName, cached);
      prepareForWrite(cached, state);
      return (W) cached.preparedStatement;
    }

    prepareForWrite(maybeCached, state);

    return (W) maybeCached.preparedStatement;
  }

  private Connection attemptConnect(
          final String url,
          final String username,
          final String password,
          final Logger logger) {

    try {
      Class.forName("org.hsqldb.jdbc.JDBCDriver");
      final Connection connection = DriverManager.getConnection(url, username, password);
      connection.setAutoCommit(false);
      return connection;
    }  catch (Exception e) {
      logger.log(getClass().getSimpleName() + ": Cannot connect because: " + e.getMessage(), e);
      return null;
    }
  }

  private Connection connect(
          final String url,
          final String username,
          final String password,
          final Logger logger) {

    if (!url.contains("sql.syntax_mys=true")) {
      throw new IllegalArgumentException(getClass().getSimpleName() + ": Must emulate MySQL: " + url);
    }

    final Connection connection1 = attemptConnect(url, username, password, logger);
    if (connection1 != null) {
      return connection1;
    }

    databaseSever = new Server();
    databaseSever.start();
    databaseSever.checkRunning(true);

    final Connection connection2 = attemptConnect(url, username, password, logger);
    if (connection2 != null) {
      return connection2;
    }

    throw new IllegalStateException(getClass().getSimpleName() + ": Cannot start server and/or connect to: " + url);
  }

  private void prepareForRead(final CachedStatement cached, final String id) throws Exception {
    cached.preparedStatement.clearParameters();
    cached.preparedStatement.setString(1, id);
  }

  private <S> void prepareForWrite(final CachedStatement cached, final State<S> state) throws Exception {
    cached.preparedStatement.clearParameters();

    cached.preparedStatement.setString(1, state.id);
    cached.preparedStatement.setString(2, state.type);
    cached.preparedStatement.setInt(3, state.typeVersion);
    if (format.isBinary()) {
      final byte[] data = (byte[]) state.data;
      cached.blob.setBytes(1, data);
      cached.preparedStatement.setBlob(4, cached.blob);
    } else if (state.isText()) {
      cached.preparedStatement.setString(4, (String) state.data);
    }
    cached.preparedStatement.setInt(5, state.dataVersion);
    cached.preparedStatement.setString(6, state.metadata.operation);
    cached.preparedStatement.setString(7, state.metadata.value);
  }

  private static class CachedStatement {
    private final Blob blob;
    private final PreparedStatement preparedStatement;

    CachedStatement(final PreparedStatement preparedStatement, final Blob blob) {
      this.preparedStatement = preparedStatement;
      this.blob = blob;
    }
  }
}
