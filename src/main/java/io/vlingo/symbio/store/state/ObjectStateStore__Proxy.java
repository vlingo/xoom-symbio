package io.vlingo.symbio.store.state;

import java.util.function.Consumer;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;

public class ObjectStateStore__Proxy implements ObjectStateStore {

  private static final String writeRepresentation1 = "write(io.vlingo.symbio.State<java.lang.Object>, io.vlingo.symbio.store.state.StateStore.WriteResultInterest<java.lang.Object>, java.lang.Object)";
  private static final String writeRepresentation2 = "write(io.vlingo.symbio.State<java.lang.Object>, io.vlingo.symbio.store.state.StateStore.WriteResultInterest<java.lang.Object>)";
  private static final String readRepresentation3 = "read(java.lang.String, java.lang.Class<?>, io.vlingo.symbio.store.state.StateStore.ReadResultInterest<java.lang.Object>, java.lang.Object)";
  private static final String readRepresentation4 = "read(java.lang.String, java.lang.Class<?>, io.vlingo.symbio.store.state.StateStore.ReadResultInterest<java.lang.Object>)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ObjectStateStore__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public void write(io.vlingo.symbio.State<java.lang.Object> arg0, io.vlingo.symbio.store.state.StateStore.WriteResultInterest<java.lang.Object> arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final Consumer<ObjectStateStore> consumer = (actor) -> actor.write(arg0, arg1, arg2);
      mailbox.send(new LocalMessage<ObjectStateStore>(actor, ObjectStateStore.class, consumer, writeRepresentation1));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeRepresentation1));
    }
  }
  public void write(io.vlingo.symbio.State<java.lang.Object> arg0, io.vlingo.symbio.store.state.StateStore.WriteResultInterest<java.lang.Object> arg1) {
    if (!actor.isStopped()) {
      final Consumer<ObjectStateStore> consumer = (actor) -> actor.write(arg0, arg1);
      mailbox.send(new LocalMessage<ObjectStateStore>(actor, ObjectStateStore.class, consumer, writeRepresentation2));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeRepresentation2));
    }
  }
  public void read(java.lang.String arg0, java.lang.Class<?> arg1, io.vlingo.symbio.store.state.StateStore.ReadResultInterest<java.lang.Object> arg2, java.lang.Object arg3) {
    if (!actor.isStopped()) {
      final Consumer<ObjectStateStore> consumer = (actor) -> actor.read(arg0, arg1, arg2, arg3);
      mailbox.send(new LocalMessage<ObjectStateStore>(actor, ObjectStateStore.class, consumer, readRepresentation3));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readRepresentation3));
    }
  }
  public void read(java.lang.String arg0, java.lang.Class<?> arg1, io.vlingo.symbio.store.state.StateStore.ReadResultInterest<java.lang.Object> arg2) {
    if (!actor.isStopped()) {
      final Consumer<ObjectStateStore> consumer = (actor) -> actor.read(arg0, arg1, arg2);
      mailbox.send(new LocalMessage<ObjectStateStore>(actor, ObjectStateStore.class, consumer, readRepresentation4));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readRepresentation4));
    }
  }
}
