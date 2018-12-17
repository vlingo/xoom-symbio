// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.eventjournal.inmemory;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.symbio.Event;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.eventjournal.EventJournalListener;

public class MockEventJournalListener<T> implements EventJournalListener<T> {
  public final List<Event<T>> events = new ArrayList<>();
  public State<T> snapshot;
  public TestUntil until = TestUntil.happenings(0);

  @Override
  public void appended(final Event<T> event) {
    this.events.add(event);
    until.happened();
  }

  @Override
  public void appendedWith(final Event<T> event, final State<T> snapshot) {
    this.events.add(event);
    this.snapshot = snapshot;
    until.happened();
  }

  @Override
  public void appendedAll(final List<Event<T>> events) {
    this.events.addAll(events);
    until.happened();
  }

  @Override
  public void appendedAllWith(final List<Event<T>> events, final State<T> snapshot) {
    this.events.addAll(events);
    this.snapshot = snapshot;
    until.happened();
  }
}
