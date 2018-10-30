// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vlingo.actors.Actor;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.state.StateStore.ConfirmDispatchedResultInterest;
import io.vlingo.symbio.store.state.StateStore.Dispatchable;
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;
import io.vlingo.symbio.store.state.StateStore.ReadResultInterest;
import io.vlingo.symbio.store.state.StateStore.WriteResultInterest;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;

public abstract class InMemoryStateStoreActor<T> extends Actor implements DispatcherControl {
  private final List<Dispatchable<T>> dispatchables;
  private final State<T> emptyState;
  private final Map<String, Map<String, State<T>>> store;

  protected InMemoryStateStoreActor(final State<T> emptyState) {
    this.emptyState = emptyState;
    this.store = new HashMap<>();
    this.dispatchables = new ArrayList<>();

    selfAs(DispatcherControl.class).dispatchUnconfirmed();
  }

  @Override
  public void confirmDispatched(final String dispatchId, final ConfirmDispatchedResultInterest interest) {
    dispatchables.remove(new Dispatchable<T>(dispatchId, null));
    interest.confirmDispatchedResultedIn(Result.Success, dispatchId);
  }

  @Override
  public void dispatchUnconfirmed() {
    for (int idx = 0; idx < dispatchables.size(); ++idx) {
      final Dispatchable<T> dispatchable = dispatchables.get(idx);
      dispatch(dispatchable.id, dispatchable.state);
    }
  }

  protected abstract void dispatch(final String dispatchId, final State<T> state);
  
  protected void readFor(final String id, final Class<?> type, final ReadResultInterest<T> interest, final Object object) {
    if (interest != null) {
      if (id == null || type == null) {
        interest.readResultedIn(Result.Error, new IllegalArgumentException(id == null ? "The id is null." : "The type is null."), id, emptyState, object);
        return;
      }

      final String storeName = StateTypeStateStoreMap.storeNameFrom(type);

      if (storeName == null) {
        interest.readResultedIn(Result.NoTypeStore, new IllegalStateException("No type store."), id, emptyState, object);
        return;
      }

      final Map<String, State<T>> typeStore = store.get(storeName);

      if (typeStore == null) {
        interest.readResultedIn(Result.NotFound, new IllegalStateException("Store not found: " + storeName), id, emptyState, object);
        return;
      }

      final State<T> state = typeStore.get(id);

      if (state != null) {
        interest.readResultedIn(Result.Success, id, state, object);
      } else {
        interest.readResultedIn(Result.NotFound, new IllegalStateException("Not found."), id, emptyState, object);
      }
    } else {
      logger().log(
              getClass().getSimpleName() +
              " readText() missing ReadResultInterest for: " +
              (id == null ? "unknown id" : id));
    }
  }

  protected void writeWith(final State<T> state, final WriteResultInterest<T> interest, final Object object) {
    if (interest != null) {
      if (state == null) {
        interest.writeResultedIn(Result.Error, new IllegalArgumentException("The state is null."), null, emptyState, object);
      } else {
        try {
          final String storeName = StateTypeStateStoreMap.storeNameFrom(state.type);

          if (storeName == null) {
            interest.writeResultedIn(Result.NoTypeStore, new IllegalStateException("Store not found: " + storeName), state.id, state, object);
            return;
          }

          Map<String, State<T>> typeStore = store.get(storeName);

          if (typeStore == null) {
            typeStore = new HashMap<>();
            final Map<String, State<T>> existingTypeStore = store.putIfAbsent(storeName, typeStore);
            if (existingTypeStore != null) {
              typeStore = existingTypeStore;
            }
          }

          final State<T> persistedState = typeStore.putIfAbsent(state.id, state);
          if (persistedState != null) {
            if (persistedState.dataVersion >= state.dataVersion) {
              interest.writeResultedIn(Result.ConcurrentyViolation, new IllegalArgumentException("Version conflict."), state.id, state, object);
              return;
            }
            typeStore.put(state.id, state);
          }
          final String dispatchId = storeName + ":" + state.id;
          dispatchables.add(new Dispatchable<T>(dispatchId, state));
          dispatch(dispatchId, state);

          interest.writeResultedIn(Result.Success, state.id, state, object);
        } catch (Exception e) {
          logger().log(getClass().getSimpleName() + " writeText() error because: " + e.getMessage(), e);
          interest.writeResultedIn(Result.Error, e, state.id, state, object);
        }
      }
    } else {
      logger().log(
              getClass().getSimpleName() +
              " writeText() missing WriteResultInterest for: " +
              (state == null ? "unknown id" : state.id));
    }
  }
}
