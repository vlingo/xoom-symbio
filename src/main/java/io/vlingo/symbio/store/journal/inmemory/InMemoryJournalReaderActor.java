// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import java.util.List;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.journal.JournalReader;

public class InMemoryJournalReaderActor<T extends Entry<?>> extends Actor implements JournalReader<T> {
  private final InMemoryJournalReader<T> reader;

  public InMemoryJournalReaderActor(final InMemoryJournalReader<T> reader) {
    this.reader = reader;
  }

  @Override
  public void close() {
    reader.close();
  }

  @Override
  public Completes<String> name() {
    return completes().with(reader.name().outcome());
  }

  @Override
  public Completes<T> readNext() {
    return completes().with(reader.readNext().outcome());
  }

  @Override
  public Completes<List<T>> readNext(final int maximumEntries) {
    return completes().with(reader.readNext(maximumEntries).outcome());
  }

  @Override
  public void rewind() {
    reader.rewind();
  }

  @Override
  public Completes<String> seekTo(final String id) {
    return completes().with(reader.seekTo(id).outcome());
  }

  @Override
  public Completes<Long> size() {
    return completes().with(reader.size().outcome());
  }
}
