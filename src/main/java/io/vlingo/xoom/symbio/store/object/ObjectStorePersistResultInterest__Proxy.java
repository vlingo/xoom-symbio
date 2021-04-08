// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.object;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.DeadLetter;
import io.vlingo.xoom.actors.LocalMessage;
import io.vlingo.xoom.actors.Mailbox;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.common.SerializableConsumer;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.object.ObjectStoreWriter.PersistResultInterest;

public class ObjectStorePersistResultInterest__Proxy implements io.vlingo.xoom.symbio.store.object.ObjectStore.PersistResultInterest {

  private static final String persistResultedInRepresentation1 = "persistResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result>, java.lang.Object, int, int, java.lang.Object)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ObjectStorePersistResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  /* @see io.vlingo.xoom.symbio.store.object.ObjectStoreWriter.PersistResultInterest#persistResultedIn(io.vlingo.xoom.common.Outcome, io.vlingo.xoom.symbio.store.object.stateObject, int, int, java.lang.Object) */
  @Override
  public void persistResultedIn(Outcome<StorageException, Result> arg0, java.lang.Object arg1, int arg2, int arg3, Object arg4) {
    if (!actor.isStopped()) {
      final SerializableConsumer<PersistResultInterest> consumer = (actor) -> actor.persistResultedIn(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, PersistResultInterest.class, consumer, null, persistResultedInRepresentation1); }
      else { mailbox.send(new LocalMessage<PersistResultInterest>(actor, PersistResultInterest.class, consumer, persistResultedInRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistResultedInRepresentation1));
    }
  }
}
