// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import io.vlingo.common.Completes;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.journal.JournalReader;

public class InMemoryJournalReader<T> implements JournalReader<T> {
  private final ListIterator<Entry<T>> journalView;
  private final String name;

  public InMemoryJournalReader(final ListIterator<Entry<T>> journalView, final String name) {
    this.journalView = journalView;
    this.name = name;
  }

  @Override
  public Completes<String> name() {
    return Completes.withSuccess(name);
  }

  @Override
  public Completes<Entry<T>> readNext() {
    if (journalView.hasNext()) {
      return Completes.withSuccess(journalView.next());
    }
    return null;
  }

  @Override
  public Completes<List<Entry<T>>> readNext(final int maximumEntries) {
    final List<Entry<T>> entries = new ArrayList<>(maximumEntries);

    for (int count = 0; count < maximumEntries; ++count) {
      if (journalView.hasNext()) {
        entries.add(journalView.next());
      } else {
        count = maximumEntries + 1;
      }
    }
    return Completes.withSuccess(entries);
  }

  @Override
  public void rewind() {
    while (journalView.hasPrevious()) {
      journalView.previous();
    }
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

  private void end() {
    while (journalView.hasNext()) {
      journalView.next();
    }
  }

  private String readCurrentId() {
    if (journalView.hasNext()) {
      final String currentId = journalView.next().id();
      journalView.previous();
      return currentId;
    }
    return "-1";
  }

  private void to(final String id) {
    rewind();
    while (journalView.hasNext()) {
      final Entry<T> entry = journalView.next();
      if (entry.id().equals(id)) {
        return;
      }
    }
  }
}
