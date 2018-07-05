// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

import io.vlingo.actors.Actor;
import io.vlingo.symbio.State;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.TextStateStore;

public class JDBCTextStateStoreActor extends Actor implements TextStateStore, DispatcherControl {
  private final static TextState EmptyState = new TextState();

  private final StorageDelegate delegate;
  private final Dispatcher dispatcher;

  public JDBCTextStateStoreActor(final Dispatcher dispatcher, final StorageDelegate delegate) {
    this.dispatcher = dispatcher;
    this.delegate = delegate;

    final DispatcherControl control = selfAs(DispatcherControl.class);
    dispatcher.controlWith(control);
    control.dispatchUnconfirmed();
  }

  @Override
  public void confirmDispatched(final String dispatchId) {
    delegate.confirmDispatched(dispatchId);
  }

  @Override
  public void dispatchUnconfirmed() {
    try {
      Collection<Dispatchable<String>> all = delegate.allUnconfirmedDispatchableStates();
      for (final Dispatchable<String> dispatchable : all) {
        dispatch(dispatchable.id, dispatchable.state);
      }
    } catch (Exception e) {
      logger().log(getClass().getSimpleName() + " dispatchUnconfirmed() failed because: " + e.getMessage(), e);
    }
  }

  @Override
  public void read(final String id, Class<?> type, final ResultInterest<String> interest) {
    if (interest != null) {
      if (id == null || type == null) {
        interest.readResultedIn(Result.Failure, id, EmptyState);
        return;
      }

      final String storeName = StateTypeStateStoreMap.storeNameFrom(type);

      if (storeName == null) {
        interest.readResultedIn(Result.NoTypeStore, id, EmptyState);
        return;
      }

      try {
        delegate.beginRead();
        final PreparedStatement readStatement = delegate.readExpressionFor(storeName, id);
        try (final ResultSet result = readStatement.executeQuery()) {
          if (result.first()) {
            final TextState state = delegate.stateFrom(result, id);
            interest.readResultedIn(Result.Success, id, state);
          } else {
            interest.readResultedIn(Result.NotFound, id, EmptyState);
          }
        }
        delegate.complete();
      } catch (Exception e) {
        delegate.fail();
        interest.readResultedIn(Result.Failure, id, EmptyState);
        logger().log(
                getClass().getSimpleName() +
                " readText() failed because: " + e.getMessage() +
                " for: " + (id == null ? "unknown id" : id),
                e);
      }
    } else {
      logger().log(
              getClass().getSimpleName() +
              " readText() missing ResultInterest for: " +
              (id == null ? "unknown id" : id));
    }
  }

  @Override
  public void write(final State<String> state, final ResultInterest<String> interest) {
    if (interest != null) {
      if (state == null) {
        interest.writeResultedIn(Result.Failure, null, EmptyState);
      } else {
        try {
          final String storeName = StateTypeStateStoreMap.storeNameFrom(state.type);

          if (storeName == null) {
            interest.writeResultedIn(Result.NoTypeStore, state.id, state);
            return;
          }

          delegate.beginWrite();
          final PreparedStatement writeStatement = delegate.writeExpressionFor(storeName, state);
          writeStatement.execute();
          final String dispatchId = storeName + ":" + state.id;
          final PreparedStatement dispatchableStatement = delegate.dispatchableWriteExpressionFor(dispatchId, state);
          dispatchableStatement.execute();
          delegate.complete();
          dispatch(dispatchId, state);

          interest.writeResultedIn(Result.Success, state.id, state);
        } catch (Exception e) {
          e.printStackTrace();
          delegate.fail();
          interest.readResultedIn(Result.Failure, state.id, state);
        }
      }
    } else {
      logger().log(
              getClass().getSimpleName() +
              " writeText() missing ResultInterest for: " +
              (state == null ? "unknown id" : state.id));
    }
  }

  private void dispatch(final String dispatchId, final State<String> state) {
    dispatcher.dispatch(dispatchId, state.asTextState());
  }
}
