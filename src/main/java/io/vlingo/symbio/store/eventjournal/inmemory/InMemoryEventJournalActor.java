// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.eventjournal.inmemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Definition;
import io.vlingo.common.Completes;
import io.vlingo.symbio.Event;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.eventjournal.EventJournal;
import io.vlingo.symbio.store.eventjournal.EventJournalListener;
import io.vlingo.symbio.store.eventjournal.EventJournalReader;
import io.vlingo.symbio.store.eventjournal.EventStreamReader;

public class InMemoryEventJournalActor<T> extends Actor implements EventJournal<T> {
  private final List<Event<T>> journal;
  private final EventJournalListener<T> listener;
  private final Map<String,EventJournalReader<T>> journalReaders;
  private final Map<String,EventStreamReader<T>> streamReaders;
  private final Map<String, Map<Integer,Integer>> streamIndexes;
  private final Map<String, State<T>> snapshots;

  public InMemoryEventJournalActor(final EventJournalListener<T> listener) {
    this.listener = listener;
    this.journal = new ArrayList<>();
    this.journalReaders = new HashMap<>(1);
    this.streamReaders = new HashMap<>(1);
    this.streamIndexes = new HashMap<>();
    this.snapshots = new HashMap<>();
  }

  @Override
  public void append(final String streamName, final int streamVersion, final Event<T> event, final AppendResultInterest<T> interest, final Object object) {
    insert(streamName, streamVersion, event);
    listener.appended(event);
    interest.appendResultedIn(Result.Success, streamName, streamVersion, event, object);
  }

  @Override
  public void appendWith(final String streamName, final int streamVersion, final Event<T> event, final State<T> snapshot, final AppendResultInterest<T> interest, final Object object) {
    insert(streamName, streamVersion, event);
    snapshots.put(streamName, snapshot);
    listener.appendedWith(event, snapshot);
    interest.appendResultedIn(Result.Success, streamName, streamVersion, event, snapshot, object);
  }

  @Override
  public void appendAll(final String streamName, final int fromStreamVersion, final List<Event<T>> events, final AppendResultInterest<T> interest, final Object object) {
    insert(streamName, fromStreamVersion, events);
    listener.appendedAll(events);
    interest.appendResultedIn(Result.Success, streamName, fromStreamVersion, events, object);
  }

  @Override
  public void appendAllWith(final String streamName, final int fromStreamVersion, final List<Event<T>> events, final State<T> snapshot, final AppendResultInterest<T> interest, final Object object) {
    insert(streamName, fromStreamVersion, events);
    snapshots.put(streamName, snapshot);
    listener.appendedAllWith(events, snapshot);
    interest.appendResultedIn(Result.Success, streamName, fromStreamVersion, events, snapshot, object);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<EventJournalReader<T>> eventJournalReader(final String name) {
    EventJournalReader<T> reader = journalReaders.get(name);
    if (reader == null) {
      reader = stage().actorFor(Definition.has(InMemoryEventJournalReaderActor.class, Definition.parameters(journal.listIterator(), name)), EventJournalReader.class);
      journalReaders.put(name, reader);
    }
    return completes().with(reader);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<EventStreamReader<T>> eventStreamReader(final String name) {
    EventStreamReader<T> reader = streamReaders.get(name);
    if (reader == null) {
      final List<Event<T>> journalView = Collections.unmodifiableList(journal);
      final Map<String, Map<Integer,Integer>> streamIndexesView = Collections.unmodifiableMap(streamIndexes);
      final Map<String, State<T>> snapshotsView = Collections.unmodifiableMap(snapshots);
      reader = childActorFor(Definition.has(InMemoryEventStreamReaderActor.class, Definition.parameters(journalView, streamIndexesView, snapshotsView, name)), EventStreamReader.class);
      streamReaders.put(name, reader);
    }
    return completes().with(reader);
  }

  private void insert(final String streamName, final int streamVersion, final Event<T> event) {
    final int eventIndex = journal.size();
    final String id = "" + (eventIndex + 1);
    event.__internal__setId(id);
    journal.add(event);
    Map<Integer,Integer> versionIndexes = streamIndexes.get(streamName);
    if (versionIndexes == null) {
      versionIndexes = new HashMap<>();
      streamIndexes.put(streamName, versionIndexes);
    }
    versionIndexes.put(streamVersion, eventIndex);
  }

  private void insert(final String streamName, final int fromStreamVersion, final List<Event<T>> events) {
    int index = 0;
    for (final Event<T> event : events) {
      insert(streamName, fromStreamVersion + index, event);
      ++index;
    }
  }
}
