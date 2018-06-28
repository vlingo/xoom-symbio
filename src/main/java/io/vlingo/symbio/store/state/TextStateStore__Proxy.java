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

public class TextStateStore__Proxy implements TextStateStore {
  private final Actor actor;
  private final Mailbox mailbox;

  public TextStateStore__Proxy(final Actor actor, final Mailbox mailbox) {
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void read(final String id, final Class<?> type, final ResultInterest<String> interest) {
    final Consumer<TextStateStore> consumer = (actor) -> actor.read(id, type, interest);
    mailbox.send(new LocalMessage<TextStateStore>(actor, TextStateStore.class, consumer, "readBinary()"));
  }

  @Override
  public void write(final State<String> data, final ResultInterest<String> interest) {
    final Consumer<TextStateStore> consumer = (actor) -> actor.write(data, interest);
    mailbox.send(new LocalMessage<TextStateStore>(actor, TextStateStore.class, consumer, "writeText()"));
  }
}
