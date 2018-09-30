package io.vlingo.symbio.store.state;

import java.util.function.Consumer;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.symbio.store.state.TextStateStore.TextDispatcher;

public class TextStateStoreTextDispatcher__Proxy implements TextDispatcher {

  private static final String dispatchTextRepresentation1 = "dispatchText(java.lang.String, io.vlingo.symbio.State<java.lang.String>)";
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

  public void dispatchText(java.lang.String arg0, io.vlingo.symbio.State<java.lang.String> arg1) {
    if (!actor.isStopped()) {
      final Consumer<TextDispatcher> consumer = (actor) -> actor.dispatchText(arg0, arg1);
      mailbox.send(new LocalMessage<TextDispatcher>(actor, TextDispatcher.class, consumer, dispatchTextRepresentation1));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchTextRepresentation1));
    }
  }
  public void dispatch(java.lang.String arg0, io.vlingo.symbio.State.TextState arg1) {
    if (!actor.isStopped()) {
      final Consumer<TextDispatcher> consumer = (actor) -> actor.dispatch(arg0, arg1);
      mailbox.send(new LocalMessage<TextDispatcher>(actor, TextDispatcher.class, consumer, dispatchRepresentation2));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchRepresentation2));
    }
  }
  public void dispatch(java.lang.String arg0, io.vlingo.symbio.State.BinaryState arg1) {
    if (!actor.isStopped()) {
      final Consumer<TextDispatcher> consumer = (actor) -> actor.dispatch(arg0, arg1);
      mailbox.send(new LocalMessage<TextDispatcher>(actor, TextDispatcher.class, consumer, dispatchRepresentation3));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchRepresentation3));
    }
  }
  public void dispatch(java.lang.String arg0, io.vlingo.symbio.State.ObjectState<?> arg1) {
    if (!actor.isStopped()) {
      final Consumer<TextDispatcher> consumer = (actor) -> actor.dispatch(arg0, arg1);
      mailbox.send(new LocalMessage<TextDispatcher>(actor, TextDispatcher.class, consumer, dispatchRepresentation4));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, dispatchRepresentation4));
    }
  }
  public void controlWith(io.vlingo.symbio.store.state.StateStore.DispatcherControl arg0) {
    if (!actor.isStopped()) {
      final Consumer<TextDispatcher> consumer = (actor) -> actor.controlWith(arg0);
      mailbox.send(new LocalMessage<TextDispatcher>(actor, TextDispatcher.class, consumer, controlWithRepresentation5));
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, controlWithRepresentation5));
    }
  }
}
