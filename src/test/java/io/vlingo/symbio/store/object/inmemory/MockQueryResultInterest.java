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
import io.vlingo.symbio.store.object.ObjectStore.QueryMultiResults;
import io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest;
import io.vlingo.symbio.store.object.ObjectStore.QuerySingleResult;

public class MockQueryResultInterest implements QueryResultInterest {
  private AccessSafely access = AccessSafely.afterCompleting(1);
  private final List<Object> persistentObjects = new ArrayList<>();

  @Override
  public void queryAllResultedIn(
          final Outcome<StorageException, Result> outcome,
          final QueryMultiResults results,
          final Object object) {

    access.writeUsing("addAll", results.persistentObjects);
  }

  @Override
  public void queryObjectResultedIn(
          final Outcome<StorageException, Result> outcome,
          final QuerySingleResult result,
          final Object object) {

    outcome
      .andThen(good -> good)
      .otherwise(bad -> { throw new IllegalStateException("Bogus outcome: " + bad.getMessage()); });

    access.writeUsing("add", result.persistentObject);
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
