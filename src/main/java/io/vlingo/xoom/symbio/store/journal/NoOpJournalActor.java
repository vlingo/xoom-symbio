// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.symbio.store.journal;

import java.util.List;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.State;

public class NoOpJournalActor<T,RS extends State<?>> extends Actor implements Journal<T> {

  private static final String warningMessage =
          "\n===============================================================================================\n" +
          "                                                                                             \n" +
          " All journal operations are stopped. Please check your DB settings and user credentials.     \n" +
          "                                                                                             \n" +
          "===============================================================================================\n";

  public NoOpJournalActor() {
    logger().warn(warningMessage);
  }

  @Override
  public <S, ST> void append(String streamName, int streamVersion, Source<S> source, Metadata metadata, AppendResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public <S, ST> void appendWith(String streamName, int streamVersion, Source<S> source, Metadata metadata, ST snapshot, AppendResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public <S, ST> void appendAll(String streamName, int fromStreamVersion, List<Source<S>> sources, Metadata metadata, AppendResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public <S, ST> void appendAllWith(String streamName, int fromStreamVersion, List<Source<S>> sources, Metadata metadata, ST snapshot, AppendResultInterest interest, Object object) {
    logger().error(warningMessage);
    this.completes().failed();
  }

  @Override
  public <ET extends Entry<?>> Completes<JournalReader<ET>> journalReader(String name) {
    logger().error(warningMessage);
    return Completes.withFailure();
  }

  @Override
  public Completes<StreamReader<T>> streamReader(String name) {
    logger().error(warningMessage);
    return Completes.withFailure();
  }
}
