package io.vlingo.symbio.store.state;

import java.util.function.Consumer;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;

public class StateStoreDispatcherControl__Proxy implements DispatcherControl {

  private static final String confirmDispatchedRepresentation1 = "confirmDispatched(java.lang.String, io.vlingo.symbio.store.state.StateStore.ConfirmDispatchedResultInterest)";
  private static final String dispatchUnconfirmedRepresentation2 = "dispatchUnconfirmed()";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStoreDispatcherControl__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public void confirmDispatched(java.lang.String arg0, io.vlingo.symbio.store.state.StateStore.ConfirmDispatchedResultInterest arg1) {
    if (!actor.isStopped()) {
      final Consumer<DispatcherControl> consumer = (actor) -> actor.confirmDispatched(arg0, arg1);
      mailbox.send(new LocalMessage<DispatcherControl>(actor, DispatcherControl.class, consumer, confirmDispatchedRepresentation1));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, confirmDispatchedRepresentation1));
    }
  }
  public void dispatchUnconfirmed() {
    if (!actor.isStopped()) {
      final Consumer<DispatcherControl> consumer = (actor) -> actor.dispatchUnconfirmed();
      mailbox.send(new LocalMessage<DispatcherControl>(actor, DispatcherControl.class, consumer, dispatchUnconfirmedRepresentation2));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchUnconfirmedRepresentation2));
    }
  }
}
