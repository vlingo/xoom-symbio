// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.journal.JournalListener;

public class MockEventJournalListener<T> implements JournalListener<T> {
  AccessSafely access;
  public final List<Entry<T>> entries = new ArrayList<>();
  public State<T> snapshot;

  @Override
  public void appended(final Entry<T> event) {
    access.writeUsing("add", event);
  }

  @Override
  public void appendedWith(final Entry<T> event, final State<T> snapshot) {
    access.writeUsing("append", event, snapshot);
  }

  @Override
  public void appendedAll(final List<Entry<T>> events) {
    access.writeUsing("addAll", events);
  }

  @Override
  public void appendedAllWith(final List<Entry<T>> events, final State<T> snapshot) {
    access.writeUsing("addAllWith", events, snapshot);
  }
  
  @SuppressWarnings("unchecked")
  public AccessSafely afterCompleting( final int times )
  {
    access = 
            AccessSafely.afterCompleting( times )
            .writingWith("add", (value) -> this.entries.add((Entry<T>)value))
            .writingWith("append", (entry, snapshot) -> { 
              this.entries.add((Entry<T>)entry); 
              this.snapshot = (State<T>)snapshot; })
            .writingWith("addAll", (values) -> this.entries.addAll((Collection<Entry<T>>)values))
            .writingWith("addAllWith", (values, snapshot) -> {
              this.entries.addAll((Collection<Entry<T>>)values);
              this.snapshot = (State<T>)snapshot; })
            .readingWith("size", () -> entries.size())
            .readingWith("entry", (index) -> entries.get((int)index))
            .readingWith("entryId", (index) -> entries.get((int)index).id())
            .readingWith("snapshot", () -> snapshot);
    
    return access;
  }
}
