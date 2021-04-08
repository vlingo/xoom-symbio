// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.dispatch;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.DeadLetter;
import io.vlingo.xoom.actors.LocalMessage;
import io.vlingo.xoom.actors.Mailbox;
import io.vlingo.xoom.common.SerializableConsumer;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Dispatcher__Proxy implements Dispatcher {

  private static final String dispatchRepresentation1 = "dispatch(io.vlingo.xoom.symbio.store.dispatch.Dispatchable)";
  private static final String controlWithRepresentation2 = "controlWith(io.vlingo.xoom.symbio.store.state.StateStore.DispatcherControl)";

  private final Actor actor;
  private final Mailbox mailbox;

  public Dispatcher__Proxy(final Actor actor, final Mailbox mailbox) {
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void dispatch(final Dispatchable arg0) {
    final SerializableConsumer<Dispatcher> consumer = (actor) -> actor.dispatch(arg0);
    send(consumer, dispatchRepresentation1);
  }

  @Override
  public void controlWith(final DispatcherControl arg0) {
    final SerializableConsumer<Dispatcher> consumer = (actor) -> actor.controlWith(arg0);
    send(consumer, controlWithRepresentation2);
  }

  private void send(final SerializableConsumer<Dispatcher> consumer, final String representation) {
    if (!actor.isStopped()) {
      if (mailbox.isPreallocated()) {
        mailbox.send(actor, Dispatcher.class, consumer, null, representation);
      } else {
        mailbox.send(new LocalMessage<>(actor, Dispatcher.class, consumer, representation));
      }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, representation));
    }
  }
}
