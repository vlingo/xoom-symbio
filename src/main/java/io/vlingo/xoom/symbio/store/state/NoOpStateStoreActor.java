// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.symbio.store.state;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.DeadLetters;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.QueryExpression;

import java.util.Collection;
import java.util.List;

public class NoOpStateStoreActor<RS extends State<?>> extends Actor implements StateStore  {

  private static final String warningMessage =
          "\n===============================================================================================\n" +
          "                                                                                             \n" +
          " All state store operations are stopped. Please check your DB settings and user credentials. \n" +
          "                                                                                             \n" +
          "===============================================================================================\n";

  public NoOpStateStoreActor() {
    logger().warn(warningMessage);
    stop();
  }

  @Override
  public <ET extends Entry<?>> Completes<StateStoreEntryReader<ET>> entryReader(String name) {
    logger().error(warningMessage);
    return Completes.withFailure();
  }

  @Override
  public void read(String id, Class<?> type, ReadResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public void readAll(Collection<TypedStateBundle> bundles, ReadResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public Completes<Stream> streamAllOf(Class<?> stateType) {
    logger().error(warningMessage);
    return Completes.withFailure();
  }

  @Override
  public Completes<Stream> streamSomeUsing(QueryExpression query) {
    logger().error(warningMessage);
    return Completes.withFailure();
  }

  @Override
  public <S, C> void write(String id, S state, int stateVersion, List<Source<C>> sources, Metadata metadata, WriteResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public DeadLetters deadLetters() {
    logger().warn(warningMessage);
    return super.deadLetters();
  }
}
