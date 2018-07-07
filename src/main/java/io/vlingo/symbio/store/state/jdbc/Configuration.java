// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import io.vlingo.symbio.store.state.StateStore.DataFormat;

public class Configuration {
  public final Connection connection;
  public final String databaseName;
  public final String driverClassname;
  public final DataFormat format;
  public final String url;
  public final String username;
  public final String password;
  public final boolean useSSL;
  public final String originatorId;
  public final boolean createTables;

  protected final ConfigurationInterest interest;

  public Configuration(
          final ConfigurationInterest interest,
          final String driverClassname,
          final DataFormat format,
          final String url,
          final String databaseName,
          final String username,
          final String password,
          final boolean useSSL,
          final String originatorId,
          final boolean createTables)
    throws Exception {

    this.interest = interest;
    this.driverClassname = driverClassname;
    this.format = format;
    this.databaseName = databaseName;
    this.url = url;
    this.username = username;
    this.password = password;
    this.useSSL = useSSL;
    this.originatorId = originatorId;
    this.createTables = createTables;
    beforeConnect();
    this.connection = connect(url, databaseName);
    afterConnect();
  }

  protected void afterConnect() throws Exception {
    interest.afterConnect(connection);
  }

  protected void beforeConnect() throws Exception {
    interest.beforeConnect(this);
  }

  protected Connection connect(final String url, final String databaseName) {
    try {
      Class.forName(driverClassname);
      final Properties properties = new Properties();
      properties.setProperty("user", username);
      properties.setProperty("password", password);
      properties.setProperty("ssl", Boolean.toString(useSSL));
      final Connection connection = DriverManager.getConnection(url + databaseName, properties);
      connection.setAutoCommit(false);
      return connection;
    }  catch (Exception e) {
      throw new IllegalStateException(getClass().getSimpleName() + ": Cannot connect because database unavilable or wrong credentials.");
    }
  }

  public interface ConfigurationInterest {
    void afterConnect(final Connection connection) throws Exception;
    void beforeConnect(final Configuration configuration) throws Exception;
    void createDatabase(final Connection connection, final String databaseName) throws Exception;
    void dropDatabase(final Connection connection, final String databaseName) throws Exception;
  }

  public static class TestConfiguration extends Configuration {
    static private final AtomicInteger uniqueNumber = new AtomicInteger(0);
    static private String testDatabaseName(final DataFormat format, final String databaseName) {
      return databaseName +
              "_" +
              uniqueNumber.incrementAndGet() +
              (format.isBinary() ? "b":"t");
    }

    private String testDatabaseName;

    public TestConfiguration(
            final ConfigurationInterest interest,
            final String driverClassname,
            final DataFormat format,
            final String url,
            final String databaseName,
            final String username,
            final String password,
            final boolean useSSL,
            final String originatorId,
            final boolean createTables)
    throws Exception {
      super(interest, driverClassname, format, url, databaseName, username, password, useSSL, originatorId, createTables);
    }

    public void cleanUp() {
      try (final Connection ownerConnection = swapConnections()) {
        try (final Statement statement = ownerConnection.createStatement()) {
          ownerConnection.setAutoCommit(true);
          interest.dropDatabase(ownerConnection, testDatabaseName);
        }  catch (Exception e) {
          e.printStackTrace();
          // ignore
        }
      }  catch (Exception e) {
        e.printStackTrace();
        // ignore
      }
    }

    @Override
    protected Connection connect(final String url, final String databaseName) {
      final Connection connection = super.connect(url, databaseName);

      try (final Statement statement = connection.createStatement()) {
        this.testDatabaseName = testDatabaseName(format, databaseName);
        interest.createDatabase(connection, testDatabaseName);
        connection.close();
        return super.connect(url, testDatabaseName);
      }  catch (Exception e) {
        throw new IllegalStateException(getClass().getSimpleName() + ": Cannot connect because the server or database unavilable, or wrong credentials.", e);
      }
    }

    private Connection swapConnections() {
      try {
        connection.close();
        return super.connect(url, databaseName);
      } catch (Exception e) {
        throw new IllegalStateException(getClass().getSimpleName() + ": Cannot swap database to owner's because: " + e.getMessage(), e);
      }
    }
  }
}
