// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.vlingo.common.Completes;
import io.vlingo.common.Success;
import io.vlingo.symbio.BaseEntry;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapter;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.StateAdapter;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.JournalListener;
import io.vlingo.symbio.store.journal.JournalReader;
import io.vlingo.symbio.store.journal.StreamReader;

public class InMemoryJournal<T,RS extends State<?>> implements Journal<T> {
  private final Map<Class<?>,EntryAdapter<? extends Source<?>,? extends Entry<?>>> entryAdapters;
  private final Map<Class<?>,StateAdapter<?,?>> stateAdapters;
  private final List<Entry<T>> journal;
  private final JournalListener<T> listener;
  private final Map<String,JournalReader<? extends Entry<?>>> journalReaders;
  private final Map<String,StreamReader<T>> streamReaders;
  private final Map<String, Map<Integer,Integer>> streamIndexes;
  private final Map<String,RS> snapshots;

  public InMemoryJournal(final JournalListener<T> listener) {
    this.listener = listener;
    this.entryAdapters = new HashMap<>();
    this.stateAdapters = new HashMap<>();
    this.journal = new ArrayList<>();
    this.journalReaders = new HashMap<>(1);
    this.streamReaders = new HashMap<>(1);
    this.streamIndexes = new HashMap<>();
    this.snapshots = new HashMap<>();
  }

  @Override
  public <S,ST> void append(final String streamName, final int streamVersion, final Source<S> source, final AppendResultInterest interest, final Object object) {
    final Entry<T> entry = asEntry(source);
    insert(streamName, streamVersion, entry);
    listener.appended(entry);
    interest.appendResultedIn(Success.of(Result.Success), streamName, streamVersion, source, Optional.empty(), object);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S,ST> void appendWith(final String streamName, final int streamVersion, final Source<S> source, final ST snapshot, final AppendResultInterest interest, final Object object) {
    final Entry<T> entry = asEntry(source);
    insert(streamName, streamVersion, entry);
    final RS raw;
    final Optional<ST> snapshotResult;
    if (snapshot != null) {
      final StateAdapter<ST,RS> adapter = (StateAdapter<ST,RS>) stateAdapters.get(snapshot.getClass());
      raw = adapter.toRawState(snapshot, streamVersion);
      snapshots.put(streamName, raw);
      snapshotResult = Optional.of(snapshot);
    } else {
      raw = null;
      snapshotResult = Optional.empty();
    }
    listener.appendedWith(entry, (State<T>) raw);
    interest.appendResultedIn(Success.of(Result.Success), streamName, streamVersion, source, snapshotResult, object);
  }

  @Override
  public <S,ST> void appendAll(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final AppendResultInterest interest, final Object object) {
    final List<Entry<T>> entries = asEntries(sources);
    insert(streamName, fromStreamVersion, entries);
    listener.appendedAll(entries);
    interest.appendAllResultedIn(Success.of(Result.Success), streamName, fromStreamVersion, sources, Optional.empty(), object);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S,ST> void appendAllWith(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final ST snapshot, final AppendResultInterest interest, final Object object) {
    final List<Entry<T>> entries = asEntries(sources);
    insert(streamName, fromStreamVersion, entries);
    final RS raw;
    final Optional<ST> snapshotResult;
    if (snapshot != null) {
      final StateAdapter<ST,RS> adapter = (StateAdapter<ST,RS>) stateAdapters.get(snapshot.getClass());
      raw = adapter.toRawState(snapshot, fromStreamVersion);
      snapshots.put(streamName, raw);
      snapshotResult = Optional.of(snapshot);
    } else {
      raw = null;
      snapshotResult = Optional.empty();
    }
    listener.appendedAllWith(entries, (State<T>) raw);
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
  public <S extends Source<?>,E extends Entry<?>> void registerEntryAdapter(final Class<S> sourceType, final EntryAdapter<S,E> adapter) {
    entryAdapters.put(sourceType, adapter);
  }

  @Override
  public <S,R extends State<?>> void registerStateAdapter(final Class<S> stateType, final StateAdapter<S,R> adapter) {
    stateAdapters.put(stateType, adapter);
  }

  @SuppressWarnings("unchecked")
  private <S extends Source<?>,E extends Entry<?>> EntryAdapter<S,E> adapter(final Class<S> sourceType) {
    final EntryAdapter<S,E> adapter = (EntryAdapter<S,E>) entryAdapters.get(sourceType);
    if (adapter != null) {
      return adapter;
    }
    throw new IllegalStateException("Adapter not registrered for: " + sourceType.getName());
  }

  private <S> List<Entry<T>> asEntries(final List<Source<S>> sources) {
    final List<Entry<T>> entries = new ArrayList<>();
    for (final Source<?> source : sources) {
      entries.add(asEntry(source));
    }
    return entries;
  }

  @SuppressWarnings("unchecked")
  private Entry<T> asEntry(final Source<?> source) {
    final EntryAdapter<Source<?>,Entry<?>>  adapter = (EntryAdapter<Source<?>,Entry<?>>) adapter(source.getClass());

    return (Entry<T>) adapter.toEntry(source);
  }

  private void insert(final String streamName, final int streamVersion, final Entry<T> entry) {
    final int entryIndex = journal.size();
    final String id = "" + (entryIndex + 1);
    ((BaseEntry<T>) entry).__internal__setId(id); //questionable cast
    journal.add(entry);
    Map<Integer,Integer> versionIndexes = streamIndexes.get(streamName);
    if (versionIndexes == null) {
      versionIndexes = new HashMap<>();
      streamIndexes.put(streamName, versionIndexes);
    }
    versionIndexes.put(streamVersion, entryIndex);
  }

  private void insert(final String streamName, final int fromStreamVersion, final List<Entry<T>> entries) {
    int index = 0;
    for (final Entry<T> entry : entries) {
      insert(streamName, fromStreamVersion + index, entry);
      ++index;
    }
  }
}
