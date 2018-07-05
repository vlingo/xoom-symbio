package io.vlingo.symbio.store.state.jdbc.postgres;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;

import io.vlingo.actors.Logger;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.StateStore.DataFormat;

class PostgresStore {
  final static String TBL_VLINGO_SYMBIO_DISPATCHABLES = "tbl_vlingo_symbio_dispatchables";

  private final static String SQL_CREATE_DISPATCHABLES_STORE =
          "CREATE TABLE {0} (\n" +
          "   d_id BIGSERIAL PRIMARY KEY," +
          "   d_originator_id VARCHAR(32) NOT NULL," +
          "   d_dispatch_id VARCHAR(128) NOT NULL,\n" +
          "   d_state_id VARCHAR(128) NOT NULL, \n" +
          "   d_state_type VARCHAR(256) NOT NULL,\n" +
          "   d_state_type_version INT NOT NULL,\n" +
          "   d_state_data {1} NOT NULL,\n" +
          "   d_state_data_version INT NOT NULL,\n" +
          "   d_state_metadata_value TEXT NOT NULL,\n" +
          "   d_state_metadata_op VARCHAR(128) NOT NULL,\n" +
          "   d_state_metadata_object TEXT,\n" +
          "   d_state_metadata_object_type VARCHAR(256)\n" +
          ");";

  private final static String SQL_DISPATCH_ID_INDEX =
          "CREATE INDEX idx_dispatchables_dispatch_id \n" + 
          "ON {0} (d_dispatch_id);";

  private final static String SQL_ORIGINATOR_ID_INDEX =
          "CREATE INDEX idx_dispatchables_originator_id \n" + 
          "ON {0} (d_originator_id);";

  final static String SQL_STATE_READ =
          "SELECT TBL_{0}.S_TYPE, TBL_{0}.S_TYPE_VERSION, TBL_{0}.S_DATA, TBL_{0}.S_DATA_VERSION, TBL_{0}.S_METADATA_VALUE, TBL_{0}.S_METADATA_OP " +
          "FROM TBL_{0} " +
          "WHERE TBL_{0}.S_ID = ?";

  final static String SQL_STATE_WRITE =
          "INSERT INTO tbl_{0} \n" +
          "(s_id, s_type, s_type_version, s_data, s_data_version, s_metadata_value, s_metadata_op) \n" +
          "VALUES (?, ?, ?, {1}, ?, ?, ?) \n" +
          "ON CONFLICT (s_id) DO UPDATE SET\n " + 
              "s_type = EXCLUDED.s_type, \n" +
              "s_type_version = EXCLUDED.s_type_version, \n" +
              "s_data = EXCLUDED.s_data, \n" +
              "s_data_version = EXCLUDED.s_data_version, \n" +
              "s_metadata_value = EXCLUDED.s_metadata_value, \n" +
              "s_metadata_op = EXCLUDED.s_metadata_op \n";

  final static String SQL_FORMAT_BINARY_CAST = "?";
  final static String SQL_FORMAT_TEXT_CAST = "?::JSON";

  private final static String SQL_CREATE_STATE_STORE =
          "CREATE TABLE {0} (\n" +
          "   s_id VARCHAR(128) NOT NULL,\n" +
          "   s_type VARCHAR(256) NOT NULL,\n" +
          "   s_type_version INT NOT NULL,\n" +
          "   s_data {1} NOT NULL,\n" +
          "   s_data_version INT NOT NULL,\n" +
          "   s_metadata_value TEXT NOT NULL,\n" +
          "   s_metadata_op VARCHAR(128) NOT NULL,\n" +
          "   PRIMARY KEY (s_id) \n" +
          ");";

  private final static String SQL_FORMAT_BINARY = "bytea";
  private final static String SQL_FORMAT_TEXT1 = "json";
  // private final static String SQL_FORMAT_TEXT2 = "jsonb";

  static String namedDispatchable(final String sql) {
    return MessageFormat.format(sql, PostgresStore.TBL_VLINGO_SYMBIO_DISPATCHABLES);
  }

  protected void createDispatchablesTable(final Connection connection, final DataFormat format) throws Exception {
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

  protected void createTables(final Connection connection, final DataFormat format, final Logger logger) {
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

  protected void createStateStoreTable(final Connection connection, final String tableName, final DataFormat format) throws Exception {
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

  protected void dropAllInternalResouces(final Connection connection) throws Exception {
    dropTable(connection, TBL_VLINGO_SYMBIO_DISPATCHABLES);
  }

  protected void dropTable(final Connection connection, String tableName) throws Exception {
    try (Statement statement = connection.createStatement()) {
      statement.executeUpdate("DROP TABLE " + tableName);
    }
  }

  protected boolean tableExists(final Connection connection, String tableName) throws Exception {
    final DatabaseMetaData metadata = connection.getMetaData();
    try (final ResultSet resultSet = metadata.getTables(null, null, tableName, null)) {
      return resultSet.next();
    }
  }

  protected String tableNameFor(final String storeName) {
    return "tbl_" + storeName.toLowerCase();
  }
}
