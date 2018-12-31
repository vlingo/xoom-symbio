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
import io.vlingo.symbio.store.state.TextStateStore.TextDispatcher;

public class TextStateStoreTextDispatcher__Proxy implements io.vlingo.symbio.store.state.TextStateStore.TextDispatcher {

  private static final String dispatchTextRepresentation1 = "dispatchText(java.lang.String, io.vlingo.symbio.State.TextState)";
  private static final String dispatchRepresentation2 = "dispatch(java.lang.String, io.vlingo.symbio.State.TextState)";
  private static final String dispatchRepresentation3 = "dispatch(java.lang.String, io.vlingo.symbio.State.BinaryState)";
  private static final String dispatchRepresentation4 = "dispatch(java.lang.String, io.vlingo.symbio.State.io.vlingo.symbio.State.ObjectState<?>)";
  private static final String controlWithRepresentation5 = "controlWith(io.vlingo.symbio.store.state.StateStore.DispatcherControl)";

  private final Actor actor;
  private final Mailbox mailbox;

  public TextStateStoreTextDispatcher__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public void dispatchText(java.lang.String arg0, io.vlingo.symbio.State.TextState arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextDispatcher> consumer = (actor) -> actor.dispatchText(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextDispatcher.class, consumer, null, dispatchTextRepresentation1); }
      else { mailbox.send(new LocalMessage<TextDispatcher>(actor, TextDispatcher.class, consumer, dispatchTextRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchTextRepresentation1));
    }
  }
  public void dispatch(java.lang.String arg0, io.vlingo.symbio.State.TextState arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextDispatcher> consumer = (actor) -> actor.dispatch(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextDispatcher.class, consumer, null, dispatchRepresentation2); }
      else { mailbox.send(new LocalMessage<TextDispatcher>(actor, TextDispatcher.class, consumer, dispatchRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchRepresentation2));
    }
  }
  public void dispatch(java.lang.String arg0, io.vlingo.symbio.State.BinaryState arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextDispatcher> consumer = (actor) -> actor.dispatch(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextDispatcher.class, consumer, null, dispatchRepresentation3); }
      else { mailbox.send(new LocalMessage<TextDispatcher>(actor, TextDispatcher.class, consumer, dispatchRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchRepresentation3));
    }
  }
  public void dispatch(java.lang.String arg0, io.vlingo.symbio.State.ObjectState<?> arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextDispatcher> consumer = (actor) -> actor.dispatch(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextDispatcher.class, consumer, null, dispatchRepresentation4); }
      else { mailbox.send(new LocalMessage<TextDispatcher>(actor, TextDispatcher.class, consumer, dispatchRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchRepresentation4));
    }
  }
  public void controlWith(io.vlingo.symbio.store.state.StateStore.DispatcherControl arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextDispatcher> consumer = (actor) -> actor.controlWith(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextDispatcher.class, consumer, null, controlWithRepresentation5); }
      else { mailbox.send(new LocalMessage<TextDispatcher>(actor, TextDispatcher.class, consumer, controlWithRepresentation5)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, controlWithRepresentation5));
    }
  }
}
