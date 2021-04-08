// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state;

import java.util.Collection;
import java.util.List;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.DeadLetter;
import io.vlingo.xoom.actors.LocalMessage;
import io.vlingo.xoom.actors.Mailbox;
import io.vlingo.xoom.actors.Returns;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.SerializableConsumer;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.QueryExpression;

public class StateStore__Proxy implements io.vlingo.xoom.symbio.store.state.StateStore {

  private static final String writeRepresentation1 = "write(java.lang.String, S, int, java.util.List<Source<?>>, io.vlingo.xoom.symbio.Metadata, io.vlingo.xoom.symbio.store.state.StateStore.WriteResultInterest, java.lang.Object)";
  private static final String readRepresentation2 = "read(java.lang.String, java.lang.Class<?>, io.vlingo.xoom.symbio.store.state.StateStore.ReadResultInterest, java.lang.Object)";
  private static final String readRepresentation2a = "readAll(java.util.Collection<io.vlingo.xoom.symbio.store.state.StateStore.TypedStateBundle>, io.vlingo.xoom.symbio.store.state.StateStore.ReadResultInterest, java.lang.Object)";
  private static final String entryReaderRepresentation3 = "entryReader(java.lang.String)";
  private static final String streamAllOfRepresentation4 = "streamAllOf(java.lang.Class<?>)";
  private static final String streamSomeUsingRepresentation5 = "streamSomeUsing(io.vlingo.xoom.symbio.store.QueryExpression.QueryExpression)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStore__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public <S,C> void write(java.lang.String arg0, S arg1, int arg2, final List<Source<C>> arg3, io.vlingo.xoom.symbio.Metadata arg4, io.vlingo.xoom.symbio.store.state.StateStore.WriteResultInterest arg5, java.lang.Object arg6) {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStore> consumer = (actor) -> actor.write(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, null, writeRepresentation1); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, writeRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeRepresentation1));
    }
  }
  @Override
  public void read(java.lang.String arg0, java.lang.Class<?> arg1, io.vlingo.xoom.symbio.store.state.StateStore.ReadResultInterest arg2, java.lang.Object arg3) {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStore> consumer = (actor) -> actor.read(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, null, readRepresentation2); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, readRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readRepresentation2));
    }
  }
  @Override
  public void readAll(final Collection<TypedStateBundle> arg0, final ReadResultInterest arg1, final Object arg2) {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStore> consumer = (actor) -> actor.readAll(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, null, readRepresentation2a); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, readRepresentation2a)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readRepresentation2a));
    }
  }

  @Override
  public <ET extends Entry<?>> Completes<StateStoreEntryReader<ET>> entryReader(String arg0) {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStore> consumer = (actor) -> actor.entryReader(arg0);
      final Completes<StateStoreEntryReader<ET>> completes = Completes.using(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, Returns.value(completes), entryReaderRepresentation3); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, Returns.value(completes), entryReaderRepresentation3)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, entryReaderRepresentation3));
    }
    return null;
  }
  @Override
  public Completes<Stream> streamAllOf(final Class<?> stateType) {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStore> consumer = (actor) -> actor.streamAllOf(stateType);
      final Completes<Stream> completes = Completes.using(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, Returns.value(completes), streamAllOfRepresentation4); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, Returns.value(completes), streamAllOfRepresentation4)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, streamAllOfRepresentation4));
    }
    return null;
  }
  @Override
  public Completes<Stream> streamSomeUsing(final QueryExpression query) {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStore> consumer = (actor) -> actor.streamSomeUsing(query);
      final Completes<Stream> completes = Completes.using(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStore.class, consumer, Returns.value(completes), streamSomeUsingRepresentation5); }
      else { mailbox.send(new LocalMessage<StateStore>(actor, StateStore.class, consumer, Returns.value(completes), streamSomeUsingRepresentation5)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, streamSomeUsingRepresentation5));
    }
    return null;
  }
}
