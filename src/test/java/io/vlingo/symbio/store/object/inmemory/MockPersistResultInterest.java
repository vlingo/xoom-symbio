// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.object.inmemory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest;

public class MockPersistResultInterest implements PersistResultInterest {
  private final Object lock = new Object();
  private final List<Object> persistentObjects = new ArrayList<>();
  private TestUntil until;

  @Override
  @SuppressWarnings("unchecked")
  public void persistResultedIn(
          final Outcome<StorageException, Result> outcome,
          final Object persistentObject,
          final int possible,
          final int actual,
          final Object object) {

    synchronized (lock) {
      if (actual == 1) {
        persistentObjects.add(persistentObject);
      } else if (actual > 1) {
        persistentObjects.addAll((Collection<Object>) persistentObject);
      } else {
        throw new IllegalArgumentException("Possible is:" + possible + " Actual is: " + actual);
      }
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
