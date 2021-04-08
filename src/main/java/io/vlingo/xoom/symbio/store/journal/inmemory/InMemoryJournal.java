// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.journal.inmemory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Stoppable;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Success;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.StateAdapterProvider;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl.DispatcherControlInstantiator;
import io.vlingo.xoom.symbio.store.dispatch.control.DispatcherControlActor;
import io.vlingo.xoom.symbio.store.dispatch.inmemory.InMemoryDispatcherControlDelegate;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.JournalReader;
import io.vlingo.xoom.symbio.store.journal.StreamReader;

public class InMemoryJournal<T,RS extends State<?>> implements Journal<T>, Stoppable {
  private final EntryAdapterProvider entryAdapterProvider;
  private final StateAdapterProvider stateAdapterProvider;
  private final List<Entry<T>> journal;
  private final Map<String,JournalReader<? extends Entry<?>>> journalReaders;
  private final Map<String,StreamReader<T>> streamReaders;
  private final Map<String, Map<Integer,Integer>> streamIndexes;
  private final Map<String,RS> snapshots;
  private final List<Dispatchable<Entry<T>, RS>> dispatchables;
  private final List<Dispatcher<Dispatchable<Entry<T>,RS>>> dispatchers;
  private final DispatcherControl dispatcherControl;

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public InMemoryJournal(
          final List<Dispatcher<Dispatchable<Entry<T>,RS>>> dispatchers,
          final World world,
          final long checkConfirmationExpirationInterval,
          final long confirmationExpiration) {

    this.entryAdapterProvider = EntryAdapterProvider.instance(world);
    this.stateAdapterProvider = StateAdapterProvider.instance(world);
    this.journal = new ArrayList<>();
    this.journalReaders = new HashMap<>(1);
    this.streamReaders = new HashMap<>(1);
    this.streamIndexes = new HashMap<>();
    this.snapshots = new HashMap<>();

    this.dispatchers = dispatchers;
    this.dispatchables = new CopyOnWriteArrayList<>();
    final InMemoryDispatcherControlDelegate<Entry<T>, RS> dispatcherControlDelegate = new InMemoryDispatcherControlDelegate<>(dispatchables);

    this.dispatcherControl = world.stage().actorFor(
            DispatcherControl.class,
            Definition.has(
                    DispatcherControlActor.class,
                    new DispatcherControlInstantiator(
                            dispatchers,
                            dispatcherControlDelegate,
                            checkConfirmationExpirationInterval,
                            confirmationExpiration)));
  }

  public InMemoryJournal(final Dispatcher<Dispatchable<Entry<T>, RS>> dispatcher, final World world ) {
    this(Arrays.asList(dispatcher), world, 1000L, 1000L);
  }

  public InMemoryJournal(final List<Dispatcher<Dispatchable<Entry<T>,RS>>> dispatchers, final World world ) {
    this(dispatchers, world, 1000L, 1000L);
  }

  @Override
  public <S, ST> void append(final String streamName, final int streamVersion, final Source<S> source, final Metadata metadata,
          final AppendResultInterest interest, final Object object) {
    final Entry<T> entry = entryAdapterProvider.asEntry(source, streamVersion, metadata);
    insert(streamName, streamVersion, entry);
    dispatch(streamName, streamVersion, Collections.singletonList(entry), null);
    interest.appendResultedIn(Success.of(Result.Success), streamName, streamVersion, source, Optional.empty(), object);
  }

  @Override
  public <S, ST> void appendWith(final String streamName, final int streamVersion, final Source<S> source, final Metadata metadata, final ST snapshot,
          final AppendResultInterest interest, final Object object) {
    final Entry<T> entry = entryAdapterProvider.asEntry(source, streamVersion, metadata);
    insert(streamName, streamVersion, entry);
    final RS raw;
    final Optional<ST> snapshotResult;
    if (snapshot != null) {
      raw = stateAdapterProvider.asRaw(streamName, snapshot, streamVersion);
      snapshots.put(streamName, raw);
      snapshotResult = Optional.of(snapshot);
    } else {
      raw = null;
      snapshotResult = Optional.empty();
    }

    dispatch(streamName, streamVersion, Collections.singletonList(entry), raw);
    interest.appendResultedIn(Success.of(Result.Success), streamName, streamVersion, source, snapshotResult, object);
  }


  @Override
  public <S, ST> void appendAll(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final Metadata metadata,
          final AppendResultInterest interest, final Object object) {
    final List<Entry<T>> entries = entryAdapterProvider.asEntries(sources, fromStreamVersion, metadata);
    insert(streamName, fromStreamVersion, entries);

    dispatch(streamName, fromStreamVersion, entries, null);
    interest.appendAllResultedIn(Success.of(Result.Success), streamName, fromStreamVersion, sources, Optional.empty(), object);
  }


  @Override
  public <S, ST> void appendAllWith(final String streamName, final int fromStreamVersion, final List<Source<S>> sources,
          final Metadata metadata, final ST snapshot, final AppendResultInterest interest, final Object object) {
    final List<Entry<T>> entries = entryAdapterProvider.asEntries(sources, fromStreamVersion, metadata);
    insert(streamName, fromStreamVersion, entries);
    final RS raw;
    final Optional<ST> snapshotResult;
    if (snapshot != null) {
      raw = stateAdapterProvider.asRaw(streamName, snapshot, fromStreamVersion);
      snapshots.put(streamName, raw);
      snapshotResult = Optional.of(snapshot);
    } else {
      raw = null;
      snapshotResult = Optional.empty();
    }

    dispatch(streamName, fromStreamVersion, entries, raw);
    interest.appendAllResultedIn(Success.of(Result.Success), streamName, fromStreamVersion, sources, snapshotResult, object);
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <ET extends Entry<?>> Completes<JournalReader<ET>> journalReader(final String name) {
    JournalReader<?> reader = journalReaders.get(name);
    if (reader == null) {
      reader = new InMemoryJournalReader(journal, name);
      journalReaders.put(name, reader);
    }
    return Completes.withSuccess((JournalReader<ET>) reader);
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Completes<StreamReader<T>> streamReader(final String name) {
    StreamReader<T> reader = streamReaders.get(name);
    if (reader == null) {
      reader = new InMemoryStreamReader(journal, streamIndexes, snapshots, name);
      streamReaders.put(name, reader);
    }
    return Completes.withSuccess(reader);
  }

  @Override
  public void conclude() {

  }

  @Override
  public boolean isStopped() {
    return false;
  }

  @Override
  public void stop() {
    dispatcherControl.stop();
  }

  private void insert(final String streamName, final int streamVersion, final Entry<T> entry) {
    final int entryIndex = journal.size();
    final String id = "" + (entryIndex + 1);
    ((BaseEntry<T>) entry).__internal__setId(id); //questionable cast
    journal.add(entry);

    final Map<Integer, Integer> versionIndexes = streamIndexes.computeIfAbsent(streamName, k -> new HashMap<>());
    versionIndexes.put(streamVersion, entryIndex);
  }

  private void insert(final String streamName, final int fromStreamVersion, final List<Entry<T>> entries) {
    int index = 0;
    for (final Entry<T> entry : entries) {
      insert(streamName, fromStreamVersion + index, entry);
      ++index;
    }
  }

  private void dispatch(final String streamName, final int streamVersion, final List<Entry<T>> entries, final RS snapshot){
    final String id = getDispatchId(streamName, streamVersion, entries);
    final Dispatchable<Entry<T>, RS> dispatchable = new Dispatchable<>(id,  LocalDateTime.now(), snapshot, entries);
    this.dispatchables.add(dispatchable);
    this.dispatchers.forEach(d -> d.dispatch(dispatchable));
  }

  private static <T> String getDispatchId(final String streamName, final int streamVersion, final Collection<Entry<T>> entries) {
    return streamName + ":" + streamVersion + ":"
            + entries.stream().map(Entry::id).collect(Collectors.joining(":"));
  }
}
