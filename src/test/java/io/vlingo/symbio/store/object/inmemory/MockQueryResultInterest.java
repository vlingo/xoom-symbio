// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.object.inmemory;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.object.ObjectStore.QueryMultiResults;
import io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest;
import io.vlingo.symbio.store.object.ObjectStore.QuerySingleResult;

public class MockQueryResultInterest implements QueryResultInterest {
  private final Object lock = new Object();
  private final List<Object> persistentObjects = new ArrayList<>();
  private TestUntil until;

  @Override
  public void queryAllResultedIn(
          final Outcome<StorageException, Result> outcome,
          final QueryMultiResults results,
          final Object object) {

    synchronized (lock) {
      persistentObjects.addAll(results.persistentObjects);
      until.happened();
    }
  }

  @Override
  public void queryObjectResultedIn(
          final Outcome<StorageException, Result> outcome,
          final QuerySingleResult result,
          final Object object) {

    outcome
      .andThen(good -> good)
      .otherwise(bad -> { throw new IllegalStateException("Bogus outcome: " + bad.getMessage()); });

    synchronized (lock) {
      persistentObjects.add(result.persistentObject);
      until.happened();
    }
  }

  @SuppressWarnings("unchecked")
  public <T> T persistentObject(final int index) {
    synchronized (lock) {
      return (T) persistentObjects.get(index);
    }    
  }

  public int size() {
    synchronized (lock) {
      return persistentObjects.size();
    }    
  }

  public TestUntil untilHappenings(final int times) {
    synchronized (lock) {
      this.until = TestUntil.happenings(times);
      return this.until;
    }
  }
}
