// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
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
import io.vlingo.xoom.common.SerializableConsumer;
import io.vlingo.xoom.symbio.store.object.ObjectStoreReader.QueryResultInterest;

public class ObjectStoreQueryResultInterest__Proxy implements io.vlingo.xoom.symbio.store.object.ObjectStore.QueryResultInterest {

  private static final String queryAllResultedInRepresentation1 = "queryAllResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result>, io.vlingo.xoom.symbio.store.object.ObjectStore.QueryMultiResults, java.lang.Object)";
  private static final String queryObjectResultedInRepresentation2 = "queryObjectResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result>, io.vlingo.xoom.symbio.store.object.ObjectStore.QuerySingleResult, java.lang.Object)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ObjectStoreQueryResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void queryAllResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result> arg0, io.vlingo.xoom.symbio.store.object.ObjectStore.QueryMultiResults arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final SerializableConsumer<QueryResultInterest> consumer = (actor) -> actor.queryAllResultedIn(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, QueryResultInterest.class, consumer, null, queryAllResultedInRepresentation1); }
      else { mailbox.send(new LocalMessage<QueryResultInterest>(actor, QueryResultInterest.class, consumer, queryAllResultedInRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryAllResultedInRepresentation1));
    }
  }
  @Override
  public void queryObjectResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result> arg0, io.vlingo.xoom.symbio.store.object.ObjectStore.QuerySingleResult arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final SerializableConsumer<QueryResultInterest> consumer = (actor) -> actor.queryObjectResultedIn(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, QueryResultInterest.class, consumer, null, queryObjectResultedInRepresentation2); }
      else { mailbox.send(new LocalMessage<QueryResultInterest>(actor, QueryResultInterest.class, consumer, queryObjectResultedInRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryObjectResultedInRepresentation2));
    }
  }
}
