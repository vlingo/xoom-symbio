// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.Collection;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.state.StateStore.Dispatcher;

public class StateStoreDispatcher__Proxy implements io.vlingo.symbio.store.state.StateStore.Dispatcher {

  private static final String dispatchRepresentation1 = "dispatch(java.lang.String, S, Collection<C>)";
  private static final String controlWithRepresentation2 = "controlWith(io.vlingo.symbio.store.state.StateStore.DispatcherControl)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStoreDispatcher__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public <S extends io.vlingo.symbio.State<?>, E extends Entry<?>> void dispatch(java.lang.String arg0, S arg1, final Collection<E> arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Dispatcher> consumer = (actor) -> actor.dispatch(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Dispatcher.class, consumer, null, dispatchRepresentation1); }
      else { mailbox.send(new LocalMessage<Dispatcher>(actor, Dispatcher.class, consumer, dispatchRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchRepresentation1));
    }
  }
  @Override
  public void controlWith(io.vlingo.symbio.store.state.StateStore.DispatcherControl arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Dispatcher> consumer = (actor) -> actor.controlWith(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Dispatcher.class, consumer, null, controlWithRepresentation2); }
      else { mailbox.send(new LocalMessage<Dispatcher>(actor, Dispatcher.class, consumer, controlWithRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, controlWithRepresentation2));
    }
  }
}
