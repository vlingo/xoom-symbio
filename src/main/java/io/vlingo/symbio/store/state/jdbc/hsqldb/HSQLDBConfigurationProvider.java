// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.hsqldb;

import java.sql.Connection;

import org.hsqldb.server.Server;

import io.vlingo.symbio.store.state.StateStore.DataFormat;
import io.vlingo.symbio.store.state.jdbc.Configuration;
import io.vlingo.symbio.store.state.jdbc.Configuration.ConfigurationInterest;
import io.vlingo.symbio.store.state.jdbc.Configuration.TestConfiguration;

public class HSQLDBConfigurationProvider {
  private static final ConfigurationInterest interest = new ConfigurationInterest() {
    private Server databaseSever;

    @Override
    public void afterConnect(final Connection connection) { }
    @Override public void createDatabase(final Connection connection, final String databaseName) { }
    @Override public void dropDatabase(final Connection connection, final String databaseName) { }

    @Override
    public void beforeConnect(final Configuration configuration) {
      if (databaseSever != null) return;
      databaseSever = new Server();
      databaseSever.start();
    }
  };

  public static Configuration configuration(final DataFormat format) throws Exception {
    return new Configuration(
            interest,
            "org.hsqldb.jdbc.JDBCDriver",
            format,
            "jdbc:hsqldb:mem:",
            "testdb",       // database name
            "SA",           // username
            "",             // password
            false,          // useSSL
            "TEST",         // originatorId
            true);          // create tables
  }

  public static TestConfiguration testConfiguration(final DataFormat format) throws Exception {
    return new TestConfiguration(
            interest,
            "org.hsqldb.jdbc.JDBCDriver",
            format,
            "jdbc:hsqldb:mem:",
            "testdb",       // database name
            "SA",           // username
            "",             // password
            false,          // useSSL
            "TEST",         // originatorId
            true);          // create tables
  }
}
