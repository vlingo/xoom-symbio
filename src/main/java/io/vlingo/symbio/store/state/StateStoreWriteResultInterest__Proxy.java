// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.symbio.store.state.StateStore.WriteResultInterest;

public class StateStoreWriteResultInterest__Proxy implements io.vlingo.symbio.store.state.StateStore.WriteResultInterest {

  private static final String writeResultedInRepresentation1 = "writeResultedIn(io.vlingo.common.Outcome<io.vlingo.symbio.store.StorageException, io.vlingo.symbio.store.Result>, java.lang.String, S, int, java.lang.Object)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStoreWriteResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public <S>void writeResultedIn(io.vlingo.common.Outcome<io.vlingo.symbio.store.StorageException, io.vlingo.symbio.store.Result> arg0, java.lang.String arg1, S arg2, int arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<WriteResultInterest> consumer = (actor) -> actor.writeResultedIn(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, WriteResultInterest.class, consumer, null, writeResultedInRepresentation1); }
      else { mailbox.send(new LocalMessage<WriteResultInterest>(actor, WriteResultInterest.class, consumer, writeResultedInRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeResultedInRepresentation1));
    }
  }
}
