// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.postgres;

public interface PostgresQueries {
  final static String TBL_VLINGO_SYMBIO_DISPATCHABLES = "tbl_vlingo_symbio_dispatchables";

  final static String SQL_CREATE_DISPATCHABLES_STORE =
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

  final static String SQL_DISPATCH_ID_INDEX =
          "CREATE INDEX idx_dispatchables_dispatch_id \n" + 
          "ON {0} (d_dispatch_id);";

  final static String SQL_ORIGINATOR_ID_INDEX =
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
          "ON CONFLICT (s_id) DO UPDATE SET \n" + 
              "s_type = EXCLUDED.s_type, \n" +
              "s_type_version = EXCLUDED.s_type_version, \n" +
              "s_data = EXCLUDED.s_data, \n" +
              "s_data_version = EXCLUDED.s_data_version, \n" +
              "s_metadata_value = EXCLUDED.s_metadata_value, \n" +
              "s_metadata_op = EXCLUDED.s_metadata_op \n";

  final static String SQL_FORMAT_BINARY_CAST = "?";
  final static String SQL_FORMAT_TEXT_CAST = "?::JSON";

  final static String SQL_CREATE_STATE_STORE =
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

  final static String SQL_FORMAT_BINARY = "bytea";
  final static String SQL_FORMAT_TEXT1 = "json";
  // private final static String SQL_FORMAT_TEXT2 = "jsonb";

  final static String SQL_DISPATCHABLE_APPEND =
          "INSERT INTO {0} \n" +
               "(d_id, d_originator_id, d_dispatch_id, \n" +
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
          "WHERE d_originator_id = ? ORDER BY D_ID";
}
