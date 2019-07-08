// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.dispatch;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;

public class ConfirmDispatchedResultInterest__Proxy implements ConfirmDispatchedResultInterest {

  private static final String confirmDispatchedResultedInRepresentation1 = "confirmDispatchedResultedIn(io.vlingo.symbio.store.Result, java.lang.String)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ConfirmDispatchedResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public void confirmDispatchedResultedIn(io.vlingo.symbio.store.Result arg0, java.lang.String arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ConfirmDispatchedResultInterest> consumer = (actor) -> actor.confirmDispatchedResultedIn(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ConfirmDispatchedResultInterest.class, consumer, null, confirmDispatchedResultedInRepresentation1); }
      else { mailbox.send(new LocalMessage<ConfirmDispatchedResultInterest>(actor, ConfirmDispatchedResultInterest.class, consumer, confirmDispatchedResultedInRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, confirmDispatchedResultedInRepresentation1));
    }
  }
}
