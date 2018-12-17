// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Definition;
import io.vlingo.common.Completes;
import io.vlingo.common.Success;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapter;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.JournalListener;
import io.vlingo.symbio.store.journal.JournalReader;
import io.vlingo.symbio.store.journal.StreamReader;

public class InMemoryJournalActor<T> extends Actor implements Journal<T> {
  private final Map<Class<?>,EntryAdapter<? extends Source<?>,? extends Entry<?>>> adapters;
  private final List<Entry<T>> journal;
  private final JournalListener<T> listener;
  private final Map<String,JournalReader<T>> journalReaders;
  private final Map<String,StreamReader<T>> streamReaders;
  private final Map<String, Map<Integer,Integer>> streamIndexes;
  private final Map<String, State<T>> snapshots;

  public InMemoryJournalActor(final JournalListener<T> listener) {
    this.listener = listener;
    this.adapters = new HashMap<>();
    this.journal = new ArrayList<>();
    this.journalReaders = new HashMap<>(1);
    this.streamReaders = new HashMap<>(1);
    this.streamIndexes = new HashMap<>();
    this.snapshots = new HashMap<>();
  }

  @Override
  public <S> void append(final String streamName, final int streamVersion, final Source<S> source, final AppendResultInterest<T> interest, final Object object) {
    final Entry<T> entry = asEntry(source);
    insert(streamName, streamVersion, entry);
    listener.appended(entry);
    interest.appendResultedIn(Success.of(Result.Success), streamName, streamVersion, source, Optional.empty(), object);
  }

  @Override
  public <S> void appendWith(final String streamName, final int streamVersion, final Source<S> source, final State<T> snapshot, final AppendResultInterest<T> interest, final Object object) {
    final Entry<T> entry = asEntry(source);
    insert(streamName, streamVersion, entry);
    snapshots.put(streamName, snapshot);
    listener.appendedWith(entry, snapshot);
    interest.appendResultedIn(Success.of(Result.Success), streamName, streamVersion, source, Optional.of(snapshot), object);
  }

  @Override
  public <S> void appendAll(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final AppendResultInterest<T> interest, final Object object) {
    final List<Entry<T>> entries = asEntries(sources);
    insert(streamName, fromStreamVersion, entries);
    listener.appendedAll(entries);
    interest.appendAllResultedIn(Success.of(Result.Success), streamName, fromStreamVersion, sources, Optional.empty(), object);
  }

  @Override
  public <S> void appendAllWith(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final State<T> snapshot, final AppendResultInterest<T> interest, final Object object) {
    final List<Entry<T>> entries = asEntries(sources);
    insert(streamName, fromStreamVersion, entries);
    snapshots.put(streamName, snapshot);
    listener.appendedAllWith(entries, snapshot);
    interest.appendAllResultedIn(Success.of(Result.Success), streamName, fromStreamVersion, sources, Optional.of(snapshot), object);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<JournalReader<T>> journalReader(final String name) {
    JournalReader<T> reader = journalReaders.get(name);
    if (reader == null) {
      reader = stage().actorFor(Definition.has(InMemoryJournalReaderActor.class, Definition.parameters(journal.listIterator(), name)), JournalReader.class);
      journalReaders.put(name, reader);
    }
    return completes().with(reader);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<StreamReader<T>> streamReader(final String name) {
    StreamReader<T> reader = streamReaders.get(name);
    if (reader == null) {
      final List<Entry<T>> journalView = Collections.unmodifiableList(journal);
      final Map<String, Map<Integer,Integer>> streamIndexesView = Collections.unmodifiableMap(streamIndexes);
      final Map<String, State<T>> snapshotsView = Collections.unmodifiableMap(snapshots);
      reader = childActorFor(Definition.has(InMemoryStreamReaderActor.class, Definition.parameters(journalView, streamIndexesView, snapshotsView, name)), StreamReader.class);
      streamReaders.put(name, reader);
    }
    return completes().with(reader);
  }

  @Override
  public <S extends Source<?>,E extends Entry<?>> void registerAdapter(final Class<S> sourceType, final EntryAdapter<S,E> adapter) {
    adapters.put(sourceType, adapter);
  }

  @SuppressWarnings("unchecked")
  private <S extends Source<?>,E extends Entry<?>> EntryAdapter<S,E> adapter(final Class<S> sourceType) {
    final EntryAdapter<S,E> adapter = (EntryAdapter<S,E>) adapters.get(sourceType);
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
    entry.__internal__setId(id);
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
