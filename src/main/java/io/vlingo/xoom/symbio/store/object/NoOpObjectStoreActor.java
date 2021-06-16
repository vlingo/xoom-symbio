// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.object;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.DeadLetters;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.store.EntryReader;
import io.vlingo.xoom.symbio.store.QueryExpression;

import java.util.Collection;

public class NoOpObjectStoreActor extends Actor implements ObjectStore {

  private static final String warningMessage =
          "\n================================================================================================\n" +
          "                                                                                              \n" +
          " All object store operations are stopped. Please check your DB settings and user credentials. \n" +
          "                                                                                              \n" +
          "===============================================================================================-\n";

  public NoOpObjectStoreActor() {
    logger().warn(warningMessage);
    stop();
  }

  @Override
  public Completes<EntryReader<? extends Entry<?>>> entryReader(String name) {
    logger().error(warningMessage);
    return Completes.withFailure();
  }

  @Override
  public void queryAll(QueryExpression expression, QueryResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public void queryObject(QueryExpression expression, QueryResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public <T extends StateObject, E> void persist(StateSources<T, E> stateSources, Metadata metadata, long updateId, PersistResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public <T extends StateObject, E> void persistAll(Collection<StateSources<T, E>> allStateSources, Metadata metadata, long updateId, PersistResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public void close() {
    logger().error(warningMessage);
    this.completes().failed();
  }

}
