// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.inmemory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vlingo.actors.Actor;
import io.vlingo.common.Failure;
import io.vlingo.common.Success;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.StateAdapter;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateStoreAdapterAssistant;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;

public class InMemoryStateStoreActor<RS extends State<?>> extends Actor
    implements StateStore {

  private final StateStoreAdapterAssistant adapterAssistant;
  private final List<Dispatchable<RS>> dispatchables;
  private final Dispatcher dispatcher;
  private final DispatcherControl dispatcherControl;
  private final Map<String, Map<String, RS>> store;

  public InMemoryStateStoreActor(final Dispatcher dispatcher) {
    this(dispatcher, 1000L, 1000L);
  }

  public InMemoryStateStoreActor(final Dispatcher dispatcher, long checkConfirmationExpirationInterval, final long confirmationExpiration) {
    if (dispatcher == null) {
      throw new IllegalArgumentException("Dispatcher must not be null.");
    }
    this.dispatcher = dispatcher;
    this.adapterAssistant = new StateStoreAdapterAssistant();
    this.store = new HashMap<>();
    this.dispatchables = new ArrayList<>();
    
    this.dispatcherControl = new InMemoryDispatcherControl<RS>(
      dispatcher,
      dispatchables,
      checkConfirmationExpirationInterval,
      confirmationExpiration);
    
    dispatcher.controlWith(dispatcherControl);
    dispatcherControl.dispatchUnconfirmed();
  }
  
  @Override
  public void stop() {
    if (dispatcherControl != null) {
      dispatcherControl.stop();
    }
    super.stop();
  }

  @Override
  public void read(final String id, Class<?> type, final ReadResultInterest interest) {
    readFor(id, type, interest, null);
  }

  @Override
  public void read(final String id, Class<?> type, final ReadResultInterest interest, final Object object) {
    readFor(id, type, interest, object);
  }

  @Override
  public <S> void write(final String id, final S state, final int stateVersion, final WriteResultInterest interest) {
    writeWith(id, state, stateVersion, null, interest, null);
  }

  @Override
  public <S> void write(final String id, final S state, final int stateVersion, final Metadata metadata, final WriteResultInterest interest) {
    writeWith(id, state, stateVersion, metadata, interest, null);
  }

  @Override
  public <S> void write(final String id, final S state, final int stateVersion, final WriteResultInterest interest, final Object object) {
    writeWith(id, state, stateVersion, null, interest, object);
  }

  @Override
  public <S> void write(final String id, final S state, final int stateVersion, final Metadata metadata, final WriteResultInterest interest, final Object object) {
    writeWith(id, state, stateVersion, metadata, interest, object);
  }

  @Override
  public <S, R extends State<?>> void registerAdapter(Class<S> stateType, StateAdapter<S, R> adapter) {
    adapterAssistant.registerAdapter(stateType, adapter);
  }

  protected void readFor(final String id, final Class<?> type, final ReadResultInterest interest, final Object object) {
    if (interest != null) {
      if (id == null || type == null) {
        interest.readResultedIn(Failure.of(new StorageException(Result.Error, id == null ? "The id is null." : "The type is null.")), id, null, -1, null, object);
        return;
      }

      final String storeName = StateTypeStateStoreMap.storeNameFrom(type);

      if (storeName == null) {
        interest.readResultedIn(Failure.of(new StorageException(Result.NoTypeStore, "No type store for: " + type.getSimpleName())), id, null, -1, null, object);
        return;
      }

      final Map<String, RS> typeStore = store.get(storeName);

      if (typeStore == null) {
        interest.readResultedIn(Failure.of(new StorageException(Result.NotFound, "Store not found: " + storeName)), id, null, -1, null, object);
        return;
      }

      final RS raw = typeStore.get(id);

      if (raw != null) {
        final Object state = adapterAssistant.adaptFromRawState(raw);
        interest.readResultedIn(Success.of(Result.Success), id, state, raw.dataVersion, raw.metadata, object);
      } else {
        for (String storeId : typeStore.keySet()) {
          System.out.println("UNFOUND STATES\n=====================");
          System.out.println("STORE ID: '" + storeId + "' STATE: " + typeStore.get(storeId));
        }
        interest.readResultedIn(Failure.of(new StorageException(Result.NotFound, "Not found.")), id, null, -1, null, object);
      }
    } else {
      logger().log(
              getClass().getSimpleName() +
              " readText() missing ReadResultInterest for: " +
              (id == null ? "unknown id" : id));
    }
  }

  protected <S> void writeWith(final String id, final S state, final int stateVersion, final Metadata metadata, final WriteResultInterest interest, final Object object) {
    if (interest != null) {
      if (state == null) {
        interest.writeResultedIn(Failure.of(new StorageException(Result.Error, "The state is null.")), id, state, stateVersion, object);
      } else {
        try {
          final String storeName = StateTypeStateStoreMap.storeNameFrom(state.getClass());

          if (storeName == null) {
            interest.writeResultedIn(Failure.of(new StorageException(Result.NoTypeStore, "No type store for: " + state.getClass())), id, state, stateVersion, object);
            return;
          }

          Map<String, RS> typeStore = store.get(storeName);

          if (typeStore == null) {
            typeStore = new HashMap<>();
            final Map<String, RS> existingTypeStore = store.putIfAbsent(storeName, typeStore);
            if (existingTypeStore != null) {
              typeStore = existingTypeStore;
            }
          }

          final RS raw = metadata == null ?
                  adapterAssistant.adaptToRawState(state, stateVersion) :
                  adapterAssistant.adaptToRawState(state, stateVersion, metadata);

          final RS persistedState = typeStore.putIfAbsent(raw.id, raw);
          if (persistedState != null) {
            if (persistedState.dataVersion >= raw.dataVersion) {
              interest.writeResultedIn(Failure.of(new StorageException(Result.ConcurrentyViolation, "Version conflict.")), id, state, stateVersion, object);
              return;
            }
            typeStore.put(id, raw);
          }
          final String dispatchId = storeName + ":" + id;
          dispatchables.add(new Dispatchable<RS>(dispatchId, LocalDateTime.now(), raw));
          dispatch(dispatchId, raw);

          interest.writeResultedIn(Success.of(Result.Success), id, state, stateVersion, object);
        } catch (Exception e) {
          logger().log(getClass().getSimpleName() + " writeText() error because: " + e.getMessage(), e);
          interest.writeResultedIn(Failure.of(new StorageException(Result.Error, e.getMessage(), e)), id, state, stateVersion, object);
        }
      }
    } else {
      logger().log(
              getClass().getSimpleName() +
              " writeText() missing WriteResultInterest for: " +
              (state == null ? "unknown id" : id));
    }
  }

  private <ST extends State<?>> void dispatch(final String dispatchId, final ST state) {
    dispatcher.dispatch(dispatchId, state);
  }
}
