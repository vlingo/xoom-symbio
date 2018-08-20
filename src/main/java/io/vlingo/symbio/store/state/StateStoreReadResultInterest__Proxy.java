package io.vlingo.symbio.store.state;

import java.util.function.Consumer;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.symbio.store.state.StateStore.ReadResultInterest;

public class StateStoreReadResultInterest__Proxy<T> implements ReadResultInterest<T> {

  private static final String readResultedInRepresentation1 = "readResultedIn(io.vlingo.symbio.store.state.StateStore.Result, java.lang.String, io.vlingo.symbio.State<T>)";
  private static final String readResultedInRepresentation2 = "readResultedIn(io.vlingo.symbio.store.state.StateStore.Result, java.lang.Exception, java.lang.String, io.vlingo.symbio.State<T>)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStoreReadResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void readResultedIn(io.vlingo.symbio.store.state.StateStore.Result arg0, java.lang.String arg1, io.vlingo.symbio.State<T> arg2, final Object arg3) {
    if (!actor.isStopped()) {
      final Consumer<ReadResultInterest<T>> consumer = (actor) -> actor.readResultedIn(arg0, arg1, arg2, arg3);
      mailbox.send(new LocalMessage(actor, ReadResultInterest.class, consumer, readResultedInRepresentation1));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readResultedInRepresentation1));
    }
  }
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public void readResultedIn(io.vlingo.symbio.store.state.StateStore.Result arg0, java.lang.Exception arg1, java.lang.String arg2, io.vlingo.symbio.State<T> arg3, final Object arg4) {
    if (!actor.isStopped()) {
      final Consumer<ReadResultInterest<T>> consumer = (actor) -> actor.readResultedIn(arg0, arg1, arg2, arg3, arg4);
      mailbox.send(new LocalMessage(actor, ReadResultInterest.class, consumer, readResultedInRepresentation2));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readResultedInRepresentation2));
    }
  }
}
