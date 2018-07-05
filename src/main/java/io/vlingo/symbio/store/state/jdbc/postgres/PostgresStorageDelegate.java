// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import io.vlingo.actors.Logger;
import io.vlingo.common.fn.Tuple2;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.State.BinaryState;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.state.StateStore.DataFormat;
import io.vlingo.symbio.store.state.StateStore.Dispatchable;
import io.vlingo.symbio.store.state.StateStore.StorageDelegate;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.jdbc.CachedStatement;
import io.vlingo.symbio.store.state.jdbc.Mode;

public class PostgresStorageDelegate extends PostgresStore implements StorageDelegate {
  private final Configuration configuration;
  private final Connection connection;
  private final DispatchableCachedStatements dispatchable;
  private final Logger logger;
  private Mode mode;
  private final Map<String, CachedStatement> readStatements;
  private final Map<String, CachedStatement> writeStatements;

  public PostgresStorageDelegate(
          final Configuration configuration,
          final Logger logger) {
    
    this.configuration = configuration;
    this.connection = connect(configuration, logger);
    this.logger = logger;
    this.mode = Mode.None;
    this.readStatements = new HashMap<>();
    this.writeStatements = new HashMap<>();

    if (configuration.createTables) {
      createTables(connection, configuration.format, logger);
    }

    this.dispatchable = new DispatchableCachedStatements(configuration.originatorId, connection, configuration.format, logger);
  }

  public PostgresStorageDelegate(final DataFormat format, final Logger logger) {
    this(new Configuration(format, "jdbc:postgresql://localhost/vlingo-test-state-store", "vlingo-user", "vlingo123", false, "TEST", true), logger);
  }

  @Override
  public <S> Collection<Dispatchable<S>> allUnconfirmedDispatchableStates() throws Exception {
    final List<Dispatchable<S>> dispatchables = new ArrayList<>();

    try (final ResultSet result = dispatchable.queryAll.preparedStatement.executeQuery()) {
      while (result.next()) {
        final Dispatchable<S> dispatchable = stateFrom(result);
        dispatchables.add(dispatchable);
      }
    }

    return dispatchables;
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
      logger.log(getClass().getSimpleName() + ": Could not close because: " + e.getMessage(), e);
    }
  }

  @Override
  public void complete() throws Exception {
    mode = Mode.None;
    connection.commit();
  }

  @Override
  public void confirmDispatched(final String dispatchId) {
    try {
      beginWrite();
      dispatchable.delete.preparedStatement.clearParameters();
      dispatchable.delete.preparedStatement.setString(1, dispatchId);
      dispatchable.delete.preparedStatement.executeUpdate();
      complete();
    } catch (Exception e) {
      fail();
      logger.log(getClass().getSimpleName() +
              ": Confirm dispatched for: " + dispatchId +
              " failed because: " + e.getMessage(), e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C> C connection() throws Exception {
    return (C) connection;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <W, S> W dispatchableWriteExpressionFor(final String dispatchId, final State<S> state) throws Exception {
    dispatchable.append.preparedStatement.clearParameters();
    dispatchable.append.preparedStatement.setString(1, configuration.originatorId);
    dispatchable.append.preparedStatement.setString(2, dispatchId);
    dispatchable.append.preparedStatement.setString(3, state.id);
    dispatchable.append.preparedStatement.setString(4, state.type);
    dispatchable.append.preparedStatement.setInt(5, state.typeVersion);
    if (configuration.format.isBinary()) {
      dispatchable.append.preparedStatement.setBytes(6, (byte[]) state.data);
    } else if (state.isText()) {
      dispatchable.append.preparedStatement.setString(6, (String) state.data);
    }
    dispatchable.append.preparedStatement.setInt(7, state.dataVersion);
    dispatchable.append.preparedStatement.setString(8, state.metadata.value);
    dispatchable.append.preparedStatement.setString(9, state.metadata.operation);
    final Tuple2<String, String> metadataObject = serialized(state.metadata.object);
    dispatchable.append.preparedStatement.setString(10, metadataObject._1);
    dispatchable.append.preparedStatement.setString(11, metadataObject._2);
    return (W) dispatchable.append.preparedStatement;
  }

  @Override
  public void drop(final String storeName) throws Exception {
    dropTable(connection, tableNameFor(storeName));
    connection.commit();
  }

  @Override
  public void dropAll() throws Exception {
    for (final String storeName : StateTypeStateStoreMap.allStoreNames()) {
      try {
        drop(storeName.toUpperCase());
      } catch (Exception e) {
        // assume table already dropped; could look at metadata
      }
    }

    try {
      dropAllInternalResouces(connection);
    } catch (Exception e) {
      // assume table already dropped; could look at metadata
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
  public String originatorId() {
    return configuration.originatorId;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <R> R readExpressionFor(final String storeName, final String id) throws Exception {
    final CachedStatement maybeCached = readStatements.get(storeName);

    if (maybeCached == null) {
      final String select = MessageFormat.format(SQL_STATE_READ, storeName.toUpperCase());
      final PreparedStatement preparedStatement = connection.prepareStatement(select);
      final CachedStatement cached = new CachedStatement(preparedStatement);
      readStatements.put(storeName, cached);
      prepareForRead(cached, id);
      return (R) preparedStatement;
    }

    prepareForRead(maybeCached, id);

    return (R) maybeCached.preparedStatement;
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
      return (S) (configuration.format.isBinary() ? new BinaryState() : new TextState());
    }
    final Class<?> type = Class.forName(resultSet.getString(1));
    final int typeVersion = resultSet.getInt(2);
    // 3 below
    final int dataVersion = resultSet.getInt(4);
    final String metadataValue = resultSet.getString(5);
    final String metadataOperation = resultSet.getString(6);

    final Metadata metadata = Metadata.with(metadataValue, metadataOperation);

    // note possible truncation with long cast to in, but
    // hopefully no objects are larger than int max value

    if (configuration.format.isBinary()) {
      final byte[] data = resultSet.getBytes(3);
      return (S) new BinaryState(id, type, typeVersion, data, dataVersion, metadata);
    } else {
      final String data = resultSet.getObject(3).toString();
      return (S) new TextState(id, type, typeVersion, data, dataVersion, metadata);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public <W, S> W writeExpressionFor(final String storeName, final State<S> state) throws Exception {
    final CachedStatement maybeCached = writeStatements.get(storeName);

    if (maybeCached == null) {
      final String upsert = MessageFormat.format(SQL_STATE_WRITE, storeName.toUpperCase(),
              configuration.format.isBinary() ? SQL_FORMAT_BINARY_CAST : SQL_FORMAT_TEXT_CAST);
      final PreparedStatement preparedStatement = connection.prepareStatement(upsert);
      final CachedStatement cached = new CachedStatement(preparedStatement);
      writeStatements.put(storeName, cached);
      prepareForWrite(cached, state);
      return (W) cached.preparedStatement;
    }

    prepareForWrite(maybeCached, state);

    return (W) maybeCached.preparedStatement;
  }

  private Connection connect(
          final Configuration configuration,
          final Logger logger) {

    try {
      Class.forName("org.postgresql.Driver");
      final Properties properties = new Properties();
      properties.setProperty("user", configuration.username);
      properties.setProperty("password", configuration.password);
      properties.setProperty("ssl", Boolean.toString(configuration.useSSL));
      final Connection connection = DriverManager.getConnection(configuration.url, properties);
      connection.setAutoCommit(false);
      return connection;
    }  catch (Exception e) {
      logger.log(getClass().getSimpleName() + ": Cannot connect because: " + e.getMessage(), e);
      throw new IllegalStateException(getClass().getSimpleName() + ": Cannot connect because database unavilable or wrong credentials.");
    }
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
    if (configuration.format.isBinary()) {
      cached.preparedStatement.setBytes(4, (byte[]) state.data);
    } else if (state.isText()) {
      cached.preparedStatement.setString(4, (String) state.data);
    }
    cached.preparedStatement.setInt(5, state.dataVersion);
    cached.preparedStatement.setString(6, state.metadata.value);
    cached.preparedStatement.setString(7, state.metadata.operation);
  }

  private Tuple2<String, String> serialized(final Object object) {
    if (object != null) {
      return Tuple2.from(JsonSerialization.serialized(object), object.getClass().getName());
    }
    return Tuple2.from(null, null);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private <S> Dispatchable<S> stateFrom(final ResultSet resultSet) throws Exception {
    final String dispatchId = resultSet.getString(1);
    final String id = resultSet.getString(2);
    final Class<?> type = Class.forName(resultSet.getString(3));
    final int typeVersion = resultSet.getInt(4);
    // 5 below
    final int dataVersion = resultSet.getInt(6);
    final String metadataValue = resultSet.getString(7);
    final String metadataOperation = resultSet.getString(8);
    final String metadataObject = resultSet.getString(9);
    final String metadataObjectType = resultSet.getString(10);

    final Object object = metadataObject != null ?
            JsonSerialization.deserialized(metadataObject, Class.forName(metadataObjectType)) : null;

    final Metadata metadata = Metadata.with(object, metadataValue, metadataOperation);

    // note possible truncation with long cast to in, but
    // hopefully no objects are larger than int max value

    if (configuration.format.isBinary()) {
      final byte[] data = resultSet.getBytes(5);
      return new Dispatchable(dispatchId, new BinaryState(id, type, typeVersion, data, dataVersion, metadata));
    } else {
      final String data = resultSet.getObject(5).toString();
      return new Dispatchable(dispatchId, new TextState(id, type, typeVersion, data, dataVersion, metadata));
    }
  }
}
