// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.journal.JournalListener;

public class MockEventJournalListener<T> implements JournalListener<T> {
  public final List<Entry<T>> entries = new ArrayList<>();
  public State<T> snapshot;
  public TestUntil until = TestUntil.happenings(0);

  @Override
  public void appended(final Entry<T> event) {
    this.entries.add(event);
    until.happened();
  }

  @Override
  public void appendedWith(final Entry<T> event, final State<T> snapshot) {
    this.entries.add(event);
    this.snapshot = snapshot;
    until.happened();
  }

  @Override
  public void appendedAll(final List<Entry<T>> events) {
    this.entries.addAll(events);
    until.happened();
  }

  @Override
  public void appendedAllWith(final List<Entry<T>> events, final State<T> snapshot) {
    this.entries.addAll(events);
    this.snapshot = snapshot;
    until.happened();
  }
}
