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

public class ConfirmDispatchedResultInterest__Proxy implements ConfirmDispatchedResultInterest {

  private static final String confirmDispatchedResultedInRepresentation1 = "confirmDispatchedResultedIn(io.vlingo.xoom.symbio.store.Result, java.lang.String)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ConfirmDispatchedResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public void confirmDispatchedResultedIn(io.vlingo.xoom.symbio.store.Result arg0, java.lang.String arg1) {
    if (!actor.isStopped()) {
      final SerializableConsumer<ConfirmDispatchedResultInterest> consumer = (actor) -> actor.confirmDispatchedResultedIn(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ConfirmDispatchedResultInterest.class, consumer, null, confirmDispatchedResultedInRepresentation1); }
      else { mailbox.send(new LocalMessage<ConfirmDispatchedResultInterest>(actor, ConfirmDispatchedResultInterest.class, consumer, confirmDispatchedResultedInRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, confirmDispatchedResultedInRepresentation1));
    }
  }
}
