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

public class StateStore__Proxy implements io.vlingo.symbio.store.state.StateStore {

  private static final String writeRepresentation1 = "write(java.lang.String, S, int, io.vlingo.symbio.store.state.StateStore.WriteResultInterest)";
  private static final String writeRepresentation2 = "write(java.lang.String, S, int, io.vlingo.symbio.Metadata, io.vlingo.symbio.store.state.StateStore.WriteResultInterest)";
  private static final String writeRepresentation3 = "write(java.lang.String, S, int, io.vlingo.symbio.store.state.StateStore.WriteResultInterest, java.lang.Object)";
  private static final String writeRepresentation4 = "write(java.lang.String, S, int, io.vlingo.symbio.Metadata, io.vlingo.symbio.store.state.StateStore.WriteResultInterest, java.lang.Object)";
  private static final String readRepresentation5 = "read(java.lang.String, java.lang.Class<?>, io.vlingo.symbio.store.state.StateStore.ReadResultInterest)";
  private static final String readRepresentation6 = "read(java.lang.String, java.lang.Class<?>, io.vlingo.symbio.store.state.StateStore.ReadResultInterest, java.lang.Object)";
  private static final String registerAdapterRepresentation5 = "registerAdapter(java.lang.Class<S>, io.vlingo.symbio.StateAdapter<S, R>)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStore__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public <S>void write(java.lang.String arg0, S arg1, int arg2, io.vlingo.symbio.store.state.StateStore.WriteResultInterest arg3) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<StateStore> consumer = (actor) -> actor.write(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, null, writeRepresentation1); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, writeRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeRepresentation1));
    }
  }
  @Override
  public <S> void write(java.lang.String arg0, S arg1, int arg2, io.vlingo.symbio.Metadata arg3, WriteResultInterest arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<StateStore> consumer = (actor) -> actor.write(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, null, writeRepresentation2); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, writeRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeRepresentation2));
    }
  }
  public <S>void write(java.lang.String arg0, S arg1, int arg2, io.vlingo.symbio.store.state.StateStore.WriteResultInterest arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<StateStore> consumer = (actor) -> actor.write(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, null, writeRepresentation3); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, writeRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeRepresentation3));
    }
  }
  public <S>void write(java.lang.String arg0, S arg1, int arg2, io.vlingo.symbio.Metadata arg3, io.vlingo.symbio.store.state.StateStore.WriteResultInterest arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<StateStore> consumer = (actor) -> actor.write(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, null, writeRepresentation4); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, writeRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeRepresentation4));
    }
  }
  public void read(java.lang.String arg0, java.lang.Class<?> arg1, io.vlingo.symbio.store.state.StateStore.ReadResultInterest arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<StateStore> consumer = (actor) -> actor.read(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, null, readRepresentation5); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, readRepresentation5)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readRepresentation5));
    }
  }
  public void read(java.lang.String arg0, java.lang.Class<?> arg1, io.vlingo.symbio.store.state.StateStore.ReadResultInterest arg2, java.lang.Object arg3) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<StateStore> consumer = (actor) -> actor.read(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, null, readRepresentation6); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, readRepresentation6)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readRepresentation6));
    }
  }
  public <S, R extends io.vlingo.symbio.State<?>> void registerAdapter(java.lang.Class<S> arg0, io.vlingo.symbio.StateAdapter<S, R> arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<StateStore> consumer = (actor) -> actor.registerAdapter(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, null, registerAdapterRepresentation5); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, registerAdapterRepresentation5)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, registerAdapterRepresentation5));
    }
  }
}
