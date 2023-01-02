// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.State;

@SuppressWarnings("rawtypes")
public class MockDispatcher <T, ST extends State<?>> implements Dispatcher<Dispatchable<Entry<T>,ST>> {
  private AccessSafely access;

  private final ConfirmDispatchedResultInterest confirmDispatchedResultInterest;
  private DispatcherControl control;
  private final List<Dispatchable> dispatched = new ArrayList<>();
  private final AtomicBoolean processDispatch = new AtomicBoolean(true);
  private int dispatchAttemptCount = 0;

  public MockDispatcher(final ConfirmDispatchedResultInterest confirmDispatchedResultInterest) {
    this.confirmDispatchedResultInterest = confirmDispatchedResultInterest;
    this.access = afterCompleting(0);
  }

  @Override
  public void controlWith(final DispatcherControl control) {
    this.control = control;
  }

  @Override
  public void dispatch(final Dispatchable dispatchable) {
    dispatchAttemptCount++;
    if (processDispatch.get()) {
      final String dispatchId = dispatchable.id();
      access.writeUsing("dispatched", dispatchable);
      control.confirmDispatched(dispatchId, confirmDispatchedResultInterest);
    }
  }

  public AccessSafely afterCompleting(final int times) {
    access = AccessSafely.afterCompleting(times)
            .writingWith("dispatched", (Consumer<Dispatchable>) this.dispatched::add)
            .readingWith("dispatched", () -> dispatched)

            .writingWith("processDispatch", processDispatch::set)
            .readingWith("processDispatch", processDispatch::get)
            .readingWith("dispatchAttemptCount", () -> dispatchAttemptCount);

    return access;
  }

  public int dispatchedCount() {
    List<Dispatchable> dispatched = access.readFrom("dispatched");
    return dispatched.size();
  }

  public List<Dispatchable<Entry<?>, State<?>>> getDispatched(){
    return access.readFrom("dispatched");
  }
}
