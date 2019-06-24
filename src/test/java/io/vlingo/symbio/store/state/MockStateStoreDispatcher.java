// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.dispatch.ConfirmDispatchedResultInterest;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.dispatch.DispatcherControl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class MockStateStoreDispatcher implements Dispatcher<StateStore.StateDispatchable> {
  private AccessSafely access = AccessSafely.afterCompleting(0);

  private final ConfirmDispatchedResultInterest confirmDispatchedResultInterest;
  private DispatcherControl control;
  private final Map<String,Object> dispatched = new HashMap<>();
  private final ConcurrentLinkedQueue<Entry<?>> dispatchedEntries = new ConcurrentLinkedQueue<>();
  private final AtomicBoolean processDispatch = new AtomicBoolean(true);
  private int dispatchAttemptCount = 0;

  public MockStateStoreDispatcher(final ConfirmDispatchedResultInterest confirmDispatchedResultInterest) {
    this.confirmDispatchedResultInterest = confirmDispatchedResultInterest;
    this.access = AccessSafely.afterCompleting(0);
  }

  @Override
  public void controlWith(final DispatcherControl control) {
    this.control = control;
  }

  @Override
  public void dispatch(StateStore.StateDispatchable dispatchable) {
    dispatchAttemptCount++;
    if (processDispatch.get()) {
      final String dispatchId = dispatchable.id();
      access.writeUsing("dispatched", dispatchId, new Dispatch(dispatchable.getState(), dispatchable.getEntries()));
      control.confirmDispatched(dispatchId, confirmDispatchedResultInterest);
    }
  }

  @SuppressWarnings({"unchecked", "rawtypes"})
  public AccessSafely afterCompleting(final int times) {
    access = AccessSafely
              .afterCompleting(times)
              .writingWith("dispatched", (String id, Dispatch dispatch) -> { dispatched.put(id, dispatch.state); dispatchedEntries.addAll(dispatch.entries); })

              .readingWith("dispatchedState", (String id) -> dispatched.get(id))
              .readingWith("dispatchedStateCount", () -> dispatched.size())

              .readingWith("dispatchedEntries", () ->  dispatchedEntries)
              .readingWith("dispatchedEntriesCount", () -> dispatchedEntries.size())

              .writingWith("processDispatch", (Boolean flag) -> processDispatch.set(flag))
              .readingWith("processDispatch", () -> processDispatch.get())

              .readingWith("dispatchAttemptCount", () -> dispatchAttemptCount)

              .readingWith("dispatched", () -> dispatched);

    return access;
  }

  public State<?> dispatched(final String id) {
    final State<?> dispatched = access.readFrom("dispatchedState", id);
    return dispatched;
  }

  public int dispatchedCount() {
    Map<String,State<?>> dispatched = access.readFrom("dispatched");
    return dispatched.size();
  }

  public void dispatchUnconfirmed() {
    control.dispatchUnconfirmed();
  }

  private static class Dispatch<S extends State<?>,E extends Entry<?>> {
    final Collection<E> entries;
    final S state;

    Dispatch(final S state, final Collection<E> entries) {
      this.state = state;
      this.entries = entries;
    }
  }
}
