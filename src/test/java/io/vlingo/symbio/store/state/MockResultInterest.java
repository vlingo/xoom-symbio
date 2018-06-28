// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore.Result;
import io.vlingo.symbio.store.state.StateStore.ResultInterest;

public class MockResultInterest implements ResultInterest<String> {
  public AtomicInteger readTextResultedIn = new AtomicInteger(0);
  public AtomicInteger writeTextResultedIn = new AtomicInteger(0);
  public final TestUntil until;

  public AtomicReference<Result> textReadResult = new AtomicReference<>();
  public AtomicReference<Result> textWriteResult = new AtomicReference<>();
  public AtomicReference<State<String>> textState = new AtomicReference<>();

  public MockResultInterest(final int testUntilHappenings) {
    until = TestUntil.happenings(testUntilHappenings);
  }

  @Override
  public void readResultedIn(final Result result, final String id, final State<String> state) {
    readTextResultedIn.incrementAndGet();
    textReadResult.set(result);
    textState.set(state);
    until.happened();
  }

  @Override
  public void writeResultedIn(final Result result, final String id, final State<String> state) {
    writeTextResultedIn.incrementAndGet();
    textWriteResult.set(result);
    textState.set(state);
    until.happened();
  }
}
