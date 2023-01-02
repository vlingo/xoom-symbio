// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.DeadLetter;
import io.vlingo.xoom.actors.LocalMessage;
import io.vlingo.xoom.actors.Mailbox;
import io.vlingo.xoom.common.SerializableConsumer;
import io.vlingo.xoom.symbio.store.state.StateStore.ReadResultInterest;

public class StateStoreReadResultInterest__Proxy implements io.vlingo.xoom.symbio.store.state.StateStore.ReadResultInterest {

  private static final String readResultedInRepresentation1 = "readResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result>, java.lang.String, S, int, io.vlingo.xoom.symbio.Metadata, java.lang.Object)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStoreReadResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public <S>void readResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result> arg0, java.lang.String arg1, S arg2, int arg3, io.vlingo.xoom.symbio.Metadata arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final SerializableConsumer<ReadResultInterest> consumer = (actor) -> actor.readResultedIn(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ReadResultInterest.class, consumer, null, readResultedInRepresentation1); }
      else { mailbox.send(new LocalMessage<ReadResultInterest>(actor, ReadResultInterest.class, consumer, readResultedInRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readResultedInRepresentation1));
    }
  }
}
