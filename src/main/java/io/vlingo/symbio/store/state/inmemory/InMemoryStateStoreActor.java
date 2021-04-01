// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.inmemory;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import io.vlingo.actors.Actor;
import io.vlingo.actors.ActorInstantiator;
import io.vlingo.actors.Definition;
import io.vlingo.common.Completes;
import io.vlingo.common.Failure;
import io.vlingo.common.Outcome;
import io.vlingo.common.Success;
import io.vlingo.reactivestreams.Stream;
import io.vlingo.symbio.BaseEntry;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.StateAdapterProvider;
import io.vlingo.symbio.store.QueryExpression;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.dispatch.DispatcherControl.DispatcherControlInstantiator;
import io.vlingo.symbio.store.dispatch.control.DispatcherControlActor;
import io.vlingo.symbio.store.dispatch.inmemory.InMemoryDispatcherControlDelegate;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateStoreEntryReader;
import io.vlingo.symbio.store.state.StateStream;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;

public class InMemoryStateStoreActor<RS extends State<?>> extends Actor
    implements StateStore {

  private final List<Dispatchable<Entry<?>,RS>> dispatchables;
  private final List<Dispatcher<Dispatchable<Entry<?>,RS>>> dispatchers;
  private final DispatcherControl dispatcherControl;
  private final List<Entry<?>> entries;
  private final Map<String,StateStoreEntryReader<?>> entryReaders;
  private final EntryAdapterProvider entryAdapterProvider;
  private final StateAdapterProvider stateAdapterProvider;
  private final ReadAllResultCollector readAllResultCollector;
  private final Map<String, Map<String, RS>> store;

  public InMemoryStateStoreActor(final List<Dispatcher<Dispatchable<Entry<?>, RS>>> dispatchers) {
    this(dispatchers, 1000L, 1000L);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public InMemoryStateStoreActor(
          final List<Dispatcher<Dispatchable<Entry<?>, RS>>> dispatchers,
          final long checkConfirmationExpirationInterval,
          final long confirmationExpiration) {

    if (dispatchers == null) {
      throw new IllegalArgumentException("Dispatcher must not be null.");
    }
    this.dispatchers = dispatchers;
    this.entryAdapterProvider = EntryAdapterProvider.instance(stage().world());
    this.stateAdapterProvider = StateAdapterProvider.instance(stage().world());
    this.entries = new CopyOnWriteArrayList<>();
    this.entryReaders = new HashMap<>();
    this.store = new HashMap<>();
    this.dispatchables = new CopyOnWriteArrayList<>();
    this.readAllResultCollector = new ReadAllResultCollector();

    final InMemoryDispatcherControlDelegate<Entry<?>, RS> dispatcherControlDelegate = new InMemoryDispatcherControlDelegate<>(dispatchables);

    this.dispatcherControl = stage().actorFor(
      DispatcherControl.class,
      Definition.has(
        DispatcherControlActor.class,
        new DispatcherControlInstantiator(
          dispatchers,
          dispatcherControlDelegate,
          checkConfirmationExpirationInterval,
          confirmationExpiration)));
  }

  @Override
  public void stop() {
    if (dispatcherControl != null) {
      dispatcherControl.stop();
    }
    super.stop();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <ET extends Entry<?>> Completes<StateStoreEntryReader<ET>> entryReader(final String name) {
    StateStoreEntryReader<?> reader = entryReaders.get(name);
    if (reader == null) {
      reader = childActorFor(StateStoreEntryReader.class, Definition.has(InMemoryStateStoreEntryReaderActor.class, new StateStoreEntryReaderInstantiator(entries, name)));
      entryReaders.put(name, reader);
    }
    return completes().with((StateStoreEntryReader<ET>) reader);
  }

  @Override
  public void read(final String id, final Class<?> type, final ReadResultInterest interest, final Object object) {
    readFor(id, type, interest, object);
  }

  @Override
  public void readAll(final Collection<TypedStateBundle> bundles, final ReadResultInterest interest, final Object object) {
    readAllResultCollector.prepare();

    for (final TypedStateBundle bundle : bundles) {
      readFor(bundle.id, bundle.type, readAllResultCollector, null);
    }

    final Outcome<StorageException, Result> outcome = readAllResultCollector.readResultOutcome(bundles.size());

    interest.readResultedIn(outcome, readAllResultCollector.readResultBundles(), object);
  }

  @Override
  public Completes<Stream> streamAllOf(final Class<?> stateType) {
    final String storeName = StateTypeStateStoreMap.storeNameFrom(stateType);
    Map<String, RS> typeStore = store.get(storeName);
    if (typeStore == null) {
      typeStore = new HashMap<>();
    }
    return completes().with(new StateStream<>(stage(), typeStore, stateAdapterProvider));
  }

  @Override
  public Completes<Stream> streamSomeUsing(final QueryExpression query) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public <S,C> void write(final String id, final S state, final int stateVersion, final List<Source<C>> sources, final Metadata metadata, final WriteResultInterest interest, final Object object) {
    writeWith(id, state, stateVersion, sources, metadata, interest, object);
  }

  private void readFor(final String id, final Class<?> type, final ReadResultInterest interest, final Object object) {
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
        final Object state = stateAdapterProvider.fromRaw(raw);
        interest.readResultedIn(Success.of(Result.Success), id, state, raw.dataVersion, raw.metadata, object);
      } else {
        for (final String storeId : typeStore.keySet()) {
          logger().debug("UNFOUND STATES\n=====================");
          logger().debug("STORE ID: '" + storeId + "' STATE: " + typeStore.get(storeId));
        }
        interest.readResultedIn(Failure.of(new StorageException(Result.NotFound, "Not found.")), id, null, -1, null, object);
      }
    } else {
      logger().warn(
              getClass().getSimpleName() +
              " readFor() missing ReadResultInterest for: " +
              (id == null ? "unknown id" : id));
    }
  }

  private <S,C> void writeWith(final String id, final S state, final int stateVersion, final List<Source<C>> sources, final Metadata metadata, final WriteResultInterest interest, final Object object) {
    if (interest != null) {
      if (state == null) {
        interest.writeResultedIn(Failure.of(new StorageException(Result.Error, "The state is null.")), id, state, stateVersion, sources, object);
      } else {
        try {
          final String storeName = StateTypeStateStoreMap.storeNameFrom(state.getClass());

          if (storeName == null) {
            interest.writeResultedIn(Failure.of(new StorageException(Result.NoTypeStore, "No type store for: " + state.getClass())), id, state, stateVersion, sources, object);
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
                  stateAdapterProvider.asRaw(id, state, stateVersion) :
                  stateAdapterProvider.asRaw(id, state, stateVersion, metadata);

          final RS persistedState = typeStore.putIfAbsent(raw.id, raw);
          if (persistedState != null) {
            if (persistedState.dataVersion >= raw.dataVersion) {
              interest.writeResultedIn(Failure.of(new StorageException(Result.ConcurrencyViolation, "Version conflict.")), id, state, stateVersion, sources, object);
              return;
            }
          }
          typeStore.put(id, raw);
          final List<Entry<?>> entries = appendEntries(sources, stateVersion, metadata);
          dispatch(id, storeName, raw, entries);

          interest.writeResultedIn(Success.of(Result.Success), id, state, stateVersion, sources, object);
        } catch (final Exception e) {
          logger().error(getClass().getSimpleName() + " writeText() error because: " + e.getMessage(), e);
          interest.writeResultedIn(Failure.of(new StorageException(Result.Error, e.getMessage(), e)), id, state, stateVersion, sources, object);
        }
      }
    } else {
      logger().warn(
              getClass().getSimpleName() +
              " writeText() missing WriteResultInterest for: " +
              (state == null ? "unknown id" : id));
    }
  }

  private <C> List<Entry<?>> appendEntries(final List<Source<C>> sources, final int stateVersion, final Metadata metadata) {
    final List<Entry<?>> adapted = entryAdapterProvider.asEntries(sources, stateVersion, metadata);
    for (final Entry<?> each : adapted) {
      ((BaseEntry<?>) each).__internal__setId(String.valueOf(entries.size()));
      entries.add(each);
    }
    return adapted;
  }

  private void dispatch(final String id, final String storeName, final RS raw, final List<Entry<?>> entries) {
    final String dispatchId = storeName + ":" + id;
    final Dispatchable<Entry<?>, RS> dispatchable = new Dispatchable<>(dispatchId, LocalDateTime.now(), raw, entries);
    this.dispatchables.add(dispatchable);
    this.dispatchers.forEach(p -> p.dispatch(dispatchable));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private static class StateStoreEntryReaderInstantiator implements ActorInstantiator<InMemoryStateStoreEntryReaderActor> {
    private static final long serialVersionUID = 8463366612347915854L;

    final String name;
    final List<Entry<?>> entries;

    StateStoreEntryReaderInstantiator(final List<Entry<?>> entries, final String name) {
      this.entries = entries;
      this.name = name;
    }

    @Override
    public InMemoryStateStoreEntryReaderActor instantiate() {
      return new InMemoryStateStoreEntryReaderActor(entries, name);
    }

    @Override
    public Class<InMemoryStateStoreEntryReaderActor> type() {
      return InMemoryStateStoreEntryReaderActor.class;
    }
  }
}
