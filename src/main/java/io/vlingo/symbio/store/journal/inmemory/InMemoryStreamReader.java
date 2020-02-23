// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.vlingo.common.Completes;
import io.vlingo.symbio.BaseEntry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.journal.EntityStream;
import io.vlingo.symbio.store.journal.StreamReader;

public class InMemoryStreamReader<T> implements StreamReader<T> {
  private final List<BaseEntry<T>> journalView;
  private final Map<String, State<T>> snapshotsView;
  private final Map<String, Map<Integer,Integer>> streamIndexesView;
  private final String name;

  public InMemoryStreamReader(
          final List<BaseEntry<T>> journalView,
          final Map<String, Map<Integer,Integer>> streamIndexesView,
          final Map<String, State<T>> snapshotsView,
          final String name) {

    this.journalView = journalView;
    this.streamIndexesView = streamIndexesView;
    this.snapshotsView = snapshotsView;
    this.name = name;
  }

  @Override
  public Completes<EntityStream<T>> streamFor(final String streamName) {
    return streamFor(streamName, 1);
  }

  @Override
  public Completes<EntityStream<T>> streamFor(final String streamName, final int fromStreamVersion) {
    int version = fromStreamVersion;
    State<T> snapshot = snapshotsView.get(streamName);
    if (snapshot != null) {
      if (snapshot.dataVersion > version) {
        version = snapshot.dataVersion;
      } else {
        snapshot = null; // reading from beyond snapshot
      }
    }
    final List<BaseEntry<T>> entries = new ArrayList<>();
    final Map<Integer,Integer> versionIndexes = streamIndexesView.get(streamName);
    if (versionIndexes != null) {
      Integer journalIndex = versionIndexes.get(version);

      while (journalIndex != null) {
        final BaseEntry<T> entry = journalView.get(journalIndex);
        entries.add(entry);
        journalIndex = versionIndexes.get(++version);
      }
    }
    return Completes.withSuccess(new EntityStream<>(streamName, version - 1, entries, snapshot));
  }

  String name() {
    return name;
  }
}
