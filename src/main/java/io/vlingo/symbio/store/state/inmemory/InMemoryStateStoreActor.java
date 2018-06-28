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
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;
import io.vlingo.symbio.store.state.StateStore.Result;
import io.vlingo.symbio.store.state.StateStore.ResultInterest;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;

public abstract class InMemoryStateStoreActor<T> extends Actor implements DispatcherControl {
  private final List<Dispatchable> dispatchables;
  private final State<T> emptyState;
  private final Map<String, Map<String, State<T>>> store;

  protected InMemoryStateStoreActor(final State<T> emptyState) {
    this.emptyState = emptyState;
    this.store = new HashMap<>();
    this.dispatchables = new ArrayList<>();
    
    final DispatcherControl control = selfAs(DispatcherControl.class);
    control.dispatchUnconfirmed();
  }

  @Override
  public void confirmDispatched(final String dispatchId) {
    dispatchables.remove(new Dispatchable(dispatchId, null));
  }

  @Override
  public void dispatchUnconfirmed() {
    for (int idx = 0; idx < dispatchables.size(); ++idx) {
      final Dispatchable dispatchable = dispatchables.get(idx);
      dispatch(dispatchable.id, dispatchable.state);
    }
  }

  protected abstract void dispatch(final String dispatchId, final State<T> state);
  
  protected void readFor(final String id, final Class<?> type, final ResultInterest<T> interest) {
    if (interest != null) {
      if (id == null || type == null) {
        interest.readResultedIn(Result.Failure, id, emptyState);
        return;
      }

      final String storeName = StateTypeStateStoreMap.storeNameFrom(type);

      if (storeName == null) {
        interest.readResultedIn(Result.NoTypeStore, id, emptyState);
        return;
      }

      final Map<String, State<T>> typeStore = store.get(storeName);

      if (typeStore == null) {
        interest.readResultedIn(Result.NotFound, id, emptyState);
        return;
      }

      final State<T> state = typeStore.get(id);

      if (state != null) {
        interest.readResultedIn(Result.Success, id, state);
      } else {
        interest.readResultedIn(Result.NotFound, id, emptyState);
      }
    } else {
      logger().log(
              getClass().getSimpleName() +
              " readText() missing ResultInterest for: " +
              (id == null ? "unknown id" : id));
    }
  }

  protected void writeWith(final State<T> state, final ResultInterest<T> interest) {
    if (interest != null) {
      if (state == null) {
        interest.writeResultedIn(Result.Failure, null, emptyState);
      } else {
        try {
          final String storeName = StateTypeStateStoreMap.storeNameFrom(state.type);

          if (storeName == null) {
            interest.writeResultedIn(Result.NoTypeStore, state.id, state);
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

          typeStore.put(state.id, state);
          final String dispatchId = storeName + ":" + state.id;
          dispatchables.add(new Dispatchable(dispatchId, state));
          dispatch(dispatchId, state);

          interest.writeResultedIn(Result.Success, state.id, state);
        } catch (Exception e) {
          e.printStackTrace();
          interest.writeResultedIn(Result.Failure, state.id, state);
        }
      }
    } else {
      logger().log("InMemoryTextStateStore writeText() missing ResultInterest for: " + (state == null ? "unknown id" : state.id));
    }
  }

  private class Dispatchable {
    private final String id;
    private final State<T> state;

    Dispatchable(final String id, final State<T> state) {
      this.id = id;
      this.state = state;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(final Object other) {
      return this.id.equals(((Dispatchable) other).id);
    }
  }
}
