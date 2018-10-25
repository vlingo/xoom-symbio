// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.eventjournal.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.symbio.Event;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.eventjournal.EventStream;
import io.vlingo.symbio.store.eventjournal.EventStreamReader;

public class InMemoryEventStreamReaderActor<T> extends Actor implements EventStreamReader<T> {
  private final List<Event<T>> journalView;
  private final Map<String, State<T>> snapshotsView;
  private final Map<String, Map<Integer,Integer>> streamIndexesView;
  private final String name;

  public InMemoryEventStreamReaderActor(
          final List<Event<T>> journalView,
          final Map<String, Map<Integer,Integer>> streamIndexesView,
          final Map<String, State<T>> snapshotsView,
          final String name) {

    this.journalView = journalView;
    this.streamIndexesView = streamIndexesView;
    this.snapshotsView = snapshotsView;
    this.name = name;
  }

  @Override
  public Completes<EventStream<T>> streamFor(final String streamName) {
    return streamFor(streamName, 1);
  }

  @Override
  public Completes<EventStream<T>> streamFor(final String streamName, final int fromStreamVersion) {
    int version = fromStreamVersion;
    State<T> snapshot = snapshotsView.get(streamName);
    if (snapshot != null) {
      if (snapshot.dataVersion > version) {
        version = snapshot.dataVersion + 1;
      } else {
        snapshot = null; // reading from beyond snapshot
      }
    }
    final Map<Integer,Integer> versionIndexes = streamIndexesView.get(streamName);
    final List<Event<T>> events = new ArrayList<>();
    Integer journalIndex = versionIndexes.get(version);

    while (journalIndex != null) {
      final Event<T> event = journalView.get(journalIndex);
      events.add(event);
      journalIndex = versionIndexes.get(++version);
    }
    return completes().with(new EventStream<>(name, version - 1, events, snapshot));
  }
}
