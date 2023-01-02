// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.journal.inmemory;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.store.journal.JournalReader;

public class InMemoryJournalReader<T extends Entry<?>> implements JournalReader<T> {
  private int currentIndex;
  private final List<Entry<T>> journalView;
  private final String name;

  public InMemoryJournalReader(final List<Entry<T>> journalView, final String name) {
    this.journalView = journalView;
    this.name = name;
    this.currentIndex = 0;
  }

  @Override
  public void close() {
    journalView.clear();
  }

  @Override
  public Completes<String> name() {
    return Completes.withSuccess(name);
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<T> readNext() {
    if (currentIndex < journalView.size()) {
      return Completes.withSuccess((T) journalView.get(currentIndex++));
    }
    return null;
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
      if (currentIndex < journalView.size()) {
        entries.add(journalView.get(currentIndex++));
      } else {
        break;
      }
    }
    return Completes.withSuccess((List<T>) entries);
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

    return Completes.withSuccess(currentId);
  }

  @Override
  public Completes<Long> size() {
    return Completes.withSuccess((long) journalView.size());
  }

  @Override
  public Completes<Stream> streamAll() {
    return null; // provided by InMemoryJournalReaderActor
  }

  private void end() {
    currentIndex = journalView.size() - 1;
  }

  private String readCurrentId() {
    if (currentIndex < journalView.size()) {
      final String currentId = journalView.get(currentIndex).id();
      return currentId;
    }
    return "-1";
  }

  private void to(final String id) {
    rewind();
    while (currentIndex < journalView.size()) {
      final Entry<T> entry = journalView.get(currentIndex);
      if (entry.id().equals(id)) {
        return;
      }
      ++currentIndex;
    }
  }
}
