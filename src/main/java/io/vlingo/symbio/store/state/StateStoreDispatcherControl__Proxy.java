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
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;

public class StateStoreDispatcherControl__Proxy implements io.vlingo.symbio.store.state.StateStore.DispatcherControl {

  private static final String dispatchUnconfirmedRepresentation1 = "dispatchUnconfirmed()";
  private static final String confirmDispatchedRepresentation2 = "confirmDispatched(java.lang.String, io.vlingo.symbio.store.state.StateStore.ConfirmDispatchedResultInterest)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStoreDispatcherControl__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public void dispatchUnconfirmed() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<DispatcherControl> consumer = (actor) -> actor.dispatchUnconfirmed();
      if (mailbox.isPreallocated()) { mailbox.send(actor, DispatcherControl.class, consumer, null, dispatchUnconfirmedRepresentation1); }
      else { mailbox.send(new LocalMessage<DispatcherControl>(actor, DispatcherControl.class, consumer, dispatchUnconfirmedRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchUnconfirmedRepresentation1));
    }
  }
  public void confirmDispatched(java.lang.String arg0, io.vlingo.symbio.store.state.StateStore.ConfirmDispatchedResultInterest arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<DispatcherControl> consumer = (actor) -> actor.confirmDispatched(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, DispatcherControl.class, consumer, null, confirmDispatchedRepresentation2); }
      else { mailbox.send(new LocalMessage<DispatcherControl>(actor, DispatcherControl.class, consumer, confirmDispatchedRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, confirmDispatchedRepresentation2));
    }
  }
  public void stop() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<DispatcherControl> consumer = (actor) -> actor.stop();
      if (mailbox.isPreallocated()) { mailbox.send(actor, DispatcherControl.class, consumer, null, dispatchUnconfirmedRepresentation1); }
      else { mailbox.send(new LocalMessage<DispatcherControl>(actor, DispatcherControl.class, consumer, dispatchUnconfirmedRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchUnconfirmedRepresentation1));
    }
  }
}
