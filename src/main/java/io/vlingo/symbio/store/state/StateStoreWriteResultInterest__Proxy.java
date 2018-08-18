package io.vlingo.symbio.store.state;

import java.util.function.Consumer;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.symbio.store.state.StateStore.WriteResultInterest;

public class StateStoreWriteResultInterest__Proxy<T> implements WriteResultInterest<T> {

  private static final String writeResultedInRepresentation1 = "writeResultedIn(io.vlingo.symbio.store.state.StateStore.Result, java.lang.String, io.vlingo.symbio.State<T>)";
  private static final String writeResultedInRepresentation2 = "writeResultedIn(io.vlingo.symbio.store.state.StateStore.Result, java.lang.Exception, java.lang.String, io.vlingo.symbio.State<T>)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStoreWriteResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void writeResultedIn(io.vlingo.symbio.store.state.StateStore.Result arg0, java.lang.String arg1, io.vlingo.symbio.State<T> arg2) {
    if (!actor.isStopped()) {
      final Consumer<WriteResultInterest<T>> consumer = (actor) -> actor.writeResultedIn(arg0, arg1, arg2);
      mailbox.send(new LocalMessage(actor, WriteResultInterest.class, consumer, writeResultedInRepresentation1));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeResultedInRepresentation1));
    }
  }
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void writeResultedIn(io.vlingo.symbio.store.state.StateStore.Result arg0, java.lang.Exception arg1, java.lang.String arg2, io.vlingo.symbio.State<T> arg3) {
    if (!actor.isStopped()) {
      final Consumer<WriteResultInterest<T>> consumer = (actor) -> actor.writeResultedIn(arg0, arg1, arg2, arg3);
      mailbox.send(new LocalMessage(actor, WriteResultInterest.class, consumer, writeResultedInRepresentation2));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, writeResultedInRepresentation2));
    }
  }
}
