// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.journal.inmemory;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.symbio.store.journal.EntityStream;
import io.vlingo.xoom.symbio.store.journal.StreamReader;

public class InMemoryStreamReaderActor<T> extends Actor implements StreamReader<T> {
  private final InMemoryStreamReader<T> reader;

  public InMemoryStreamReaderActor(final InMemoryStreamReader<T> reader) {
    this.reader = reader;
  }

  @Override
  public void start() {
    logger().debug("Starting InMemoryStreamReaderActor named: " + reader.name());
    super.start();
  }

  @Override
  public Completes<EntityStream<T>> streamFor(final String streamName) {
    return completes().with(reader.streamFor(streamName).outcome());
  }

  @Override
  public Completes<EntityStream<T>> streamFor(final String streamName, final int fromStreamVersion) {
    return completes().with(reader.streamFor(streamName, fromStreamVersion).outcome());
  }
}
