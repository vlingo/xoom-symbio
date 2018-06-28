// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.function.Consumer;

import io.vlingo.actors.Actor;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.symbio.State;

public class BinaryStateStore__Proxy implements BinaryStateStore {
  private final Actor actor;
  private final Mailbox mailbox;

  public BinaryStateStore__Proxy(final Actor actor, final Mailbox mailbox) {
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void read(final String id, final Class<?> type, final ResultInterest<byte[]> interest) {
    final Consumer<BinaryStateStore> consumer = (actor) -> actor.read(id, type, interest);
    mailbox.send(new LocalMessage<BinaryStateStore>(actor, BinaryStateStore.class, consumer, "readText()"));
  }

  @Override
  public void write(final State<byte[]> data, final ResultInterest<byte[]> interest) {
    final Consumer<BinaryStateStore> consumer = (actor) -> actor.write(data, interest);
    mailbox.send(new LocalMessage<BinaryStateStore>(actor, BinaryStateStore.class, consumer, "writeText()"));
  }
}
