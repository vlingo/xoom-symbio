// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state.inmemory;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.store.EntryReaderStream;
import io.vlingo.xoom.symbio.store.journal.JournalReader;
import io.vlingo.xoom.symbio.store.state.StateStoreEntryReader;

public class InMemoryStateStoreEntryReaderActor<T extends Entry<?>> extends Actor implements StateStoreEntryReader<T> {
  private int currentIndex;
  private final EntryAdapterProvider entryAdapterProvider;
  private final List<Entry<T>> entriesView;
  private final String name;

  public InMemoryStateStoreEntryReaderActor(final List<Entry<T>> entriesView, final String name) {
    this.entriesView = entriesView;
    this.name = name;
    this.entryAdapterProvider = EntryAdapterProvider.instance(stage().world());
    this.currentIndex = 0;
  }

  @Override
  public void close() {
    currentIndex = -1;
    entriesView.clear();
  }

  @Override
  public Completes<String> name() {
    return completes().with(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<T> readNext() {
    if (currentIndex < entriesView.size()) {
      return completes().with((T) entriesView.get(currentIndex++));
    }
    return completes().with(null);
  }

  @Override
  public Completes<T> readNext(final String fromId) {
    seekTo(fromId);
    return readNext();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<List<T>> readNext(final int maximumEntries) {
    final List<Entry<T>> entries = new ArrayList<>(maximumEntries);

    for (int count = 0; count < maximumEntries; ++count) {
      if (currentIndex < entriesView.size()) {
        entries.add(entriesView.get(currentIndex++));
      } else {
        break;
      }
    }
    return completes().with((List<T>) entries);
  }

  @Override
  public Completes<List<T>> readNext(final String fromId, final int maximumEntries) {
    seekTo(fromId);
    return readNext(maximumEntries);
  }

  @Override
  public void rewind() {
    currentIndex = 0;
  }

  @Override
  public Completes<String> seekTo(final String id) {
    final String currentId;

    switch (id) {
    case Beginning:
      rewind();
      currentId = readCurrentId();
      break;
    case End:
      end();
      currentId = readCurrentId();
      break;
    case Query:
      currentId = readCurrentId();
      break;
    default:
      to(id);
      currentId = readCurrentId();
      break;
    }

    return completes().with(currentId);
  }

  @Override
  public Completes<Long> size() {
    return completes().with((long) entriesView.size());
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<Stream> streamAll() {
    return completes().with(new EntryReaderStream<>(stage(), selfAs(JournalReader.class), entryAdapterProvider));
  }

  private void end() {
    currentIndex = entriesView.size();
  }

  private String readCurrentId() {
    if (currentIndex < entriesView.size()) {
      final String currentId = entriesView.get(currentIndex).id();
      return currentId;
    }
    return "-1";
  }

  private void to(final String id) {
    rewind();
    while (currentIndex < entriesView.size()) {
      final Entry<T> entry = entriesView.get(currentIndex);
      if (entry.id().equals(id)) {
        return;
      }
      ++currentIndex;
    }
  }
}
