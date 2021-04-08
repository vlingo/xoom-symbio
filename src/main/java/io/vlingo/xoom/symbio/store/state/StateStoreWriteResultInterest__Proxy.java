// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
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
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore.WriteResultInterest;

import java.util.List;

public class StateStoreWriteResultInterest__Proxy implements io.vlingo.xoom.symbio.store.state.StateStore.WriteResultInterest {

  private static final String writeResultedInRepresentation1 = "writeResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result>, java.lang.String, S, int, List<Source<C>>, java.lang.Object)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStoreWriteResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public <S,C> void writeResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result> arg0, java.lang.String arg1, S arg2, int arg3, final List<Source<C>> arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final SerializableConsumer<WriteResultInterest> consumer = (actor) -> actor.writeResultedIn(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, WriteResultInterest.class, consumer, null, writeResultedInRepresentation1); }
      else { mailbox.send(new LocalMessage<WriteResultInterest>(actor, WriteResultInterest.class, consumer, writeResultedInRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeResultedInRepresentation1));
    }
  }
}
