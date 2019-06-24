// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.dispatch.ConfirmDispatchedResultInterest;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;
import io.vlingo.symbio.store.journal.dispatch.JournalDispatchable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public class MockJournalDispatcher<T, ST extends State<?>> implements Dispatcher<JournalDispatchable<T,ST>> {
  private AccessSafely access = AccessSafely.afterCompleting(0);

  private final ConfirmDispatchedResultInterest confirmDispatchedResultInterest;
  private DispatcherControl control;
  private final Map<String, JournalDispatchable> dispatched = new HashMap<>();
  private final AtomicBoolean processDispatch = new AtomicBoolean(true);
  private int dispatchAttemptCount = 0;

  public MockJournalDispatcher(final ConfirmDispatchedResultInterest confirmDispatchedResultInterest) {
    this.confirmDispatchedResultInterest = confirmDispatchedResultInterest;
    this.access = AccessSafely.afterCompleting(0);
  }

  @Override
  public void controlWith(final DispatcherControl control) {
    this.control = control;
  }

  @Override
  public void dispatch(JournalDispatchable dispatchable) {
    dispatchAttemptCount++;
    if (processDispatch.get()) {
      final String dispatchId = dispatchable.id();
      access.writeUsing("dispatched", dispatchId, dispatchable);
      control.confirmDispatched(dispatchId, confirmDispatchedResultInterest);
    }
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public AccessSafely afterCompleting(final int times) {
    access = AccessSafely.afterCompleting(times)
            .writingWith("dispatched", dispatched::put)
            .readingWith("dispatched", () -> dispatched)
            .readingWith("dispatchedState", (Function<String, JournalDispatchable>) dispatched::get)
            .readingWith("dispatchedStateCount", dispatched::size)

            .writingWith("processDispatch", processDispatch::set)
            .readingWith("processDispatch", processDispatch::get)
            .readingWith("dispatchAttemptCount", () -> dispatchAttemptCount);

    return access;
  }
  
  public int dispatchedCount() {
    Map<String, JournalDispatchable> dispatched = access.readFrom("dispatched");
    return dispatched.size();
  }

  public List<JournalDispatchable<T,ST>> getDispached(){
    final Map<String, JournalDispatchable<T, ST>> dispatched = access.readFrom("dispatched");
    return new ArrayList<>(dispatched.values());
  }
}
