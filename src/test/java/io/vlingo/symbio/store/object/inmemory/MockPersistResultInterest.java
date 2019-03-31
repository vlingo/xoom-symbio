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

import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest;

public class MockPersistResultInterest implements PersistResultInterest {
  private AccessSafely access = AccessSafely.afterCompleting(1);
  private final List<Object> persistentObjects = new ArrayList<>();

  @Override
  public void persistResultedIn(
          final Outcome<StorageException, Result> outcome,
          final Object persistentObject,
          final int possible,
          final int actual,
          final Object object) {

    if (actual == 1) {
      access.writeUsing("add", persistentObject);
    } else if (actual > 1) {
      access.writeUsing("addAll", persistentObject);
    } else {
      throw new IllegalArgumentException("Possible is:" + possible + " Actual is: " + actual);
    }
  }

  @SuppressWarnings("unchecked")
  public AccessSafely afterCompleting(final int times) {
    access =
            AccessSafely
              .afterCompleting(times)
              .writingWith("add", (value) -> persistentObjects.add(value))
              .writingWith("addAll", (values) -> persistentObjects.addAll((Collection<Object>) values))
              .readingWith("object", (index) -> persistentObjects.get((int) index))
              .readingWith("size", () -> persistentObjects.size());

    return access;
  }
}
