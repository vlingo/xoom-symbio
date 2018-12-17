// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.state.StateStore.ConfirmDispatchedResultInterest;
import io.vlingo.symbio.store.state.StateStore.ReadResultInterest;
import io.vlingo.symbio.store.state.StateStore.WriteResultInterest;

public class MockObjectResultInterest
    implements ReadResultInterest<Object>,
               WriteResultInterest<Object>,
               ConfirmDispatchedResultInterest {

  public AtomicInteger confirmDispatchedResultedIn = new AtomicInteger(0);
  public AtomicInteger readObjectResultedIn = new AtomicInteger(0);
  public AtomicInteger writeObjectResultedIn = new AtomicInteger(0);
  public TestUntil until;

  public AtomicReference<Result> objectReadResult = new AtomicReference<>();
  public AtomicReference<Result> objectWriteResult = new AtomicReference<>();
  public ConcurrentLinkedQueue<Result> objectWriteAccumulatedResults = new ConcurrentLinkedQueue<>();
  public AtomicReference<State<Object>> objectState = new AtomicReference<>();
  public ConcurrentLinkedQueue<Exception> errorCauses = new ConcurrentLinkedQueue<>();

  public MockObjectResultInterest(final int testUntilHappenings) {
    until = TestUntil.happenings(testUntilHappenings);
  }

  @Override
  public void confirmDispatchedResultedIn(final Result result, final String dispatchId) {
    confirmDispatchedResultedIn.incrementAndGet();
    until.happened();
  }

  @Override
  public void readResultedIn(final Outcome<StorageException, Result> outcome, final String id, final State<Object> state, Object object) {
    outcome
      .andThen(result -> {
        readObjectResultedIn.incrementAndGet();
        objectReadResult.set(result);
        objectState.set(state);
        until.happened();
        return result;
      })
      .otherwise(cause -> {
        readObjectResultedIn.incrementAndGet();
        objectReadResult.set(cause.result);
        objectState.set(state);
        errorCauses.add(cause);
        until.happened();
        return cause.result;
      });
  }

  @Override
  public void writeResultedIn(final Outcome<StorageException, Result> outcome, final String id, final State<Object> state, final Object object) {
    outcome
      .andThen(result -> {
        writeObjectResultedIn.incrementAndGet();
        objectWriteResult.set(result);
        objectWriteAccumulatedResults.add(result);
        objectState.set(state);
        until.happened();
        return result;
      })
      .otherwise(cause -> {
        writeObjectResultedIn.incrementAndGet();
        objectWriteResult.set(cause.result);
        objectWriteAccumulatedResults.add(cause.result);
        objectState.set(state);
        errorCauses.add(cause);
        until.happened();
        return cause.result;
      });
  }
}
