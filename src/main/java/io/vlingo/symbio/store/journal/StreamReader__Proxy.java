package io.vlingo.symbio.store.journal;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.common.BasicCompletes;
import io.vlingo.symbio.store.journal.StreamReader;
import java.lang.String;

public class StreamReader__Proxy<T> implements io.vlingo.symbio.store.journal.StreamReader<T> {

  private static final String streamForRepresentation1 = "streamFor(java.lang.String)";
  private static final String streamForRepresentation2 = "streamFor(java.lang.String, int)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StreamReader__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<io.vlingo.symbio.store.journal.Stream<T>> streamFor(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<StreamReader> consumer = (actor) -> actor.streamFor(arg0);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.journal.Stream<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StreamReader.class, consumer, completes, streamForRepresentation1); }
      else { mailbox.send(new LocalMessage<StreamReader>(actor, StreamReader.class, consumer, completes, streamForRepresentation1)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, streamForRepresentation1));
    }
    return null;
  }
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<io.vlingo.symbio.store.journal.Stream<T>> streamFor(java.lang.String arg0, int arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<StreamReader> consumer = (actor) -> actor.streamFor(arg0, arg1);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.journal.Stream<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StreamReader.class, consumer, completes, streamForRepresentation2); }
      else { mailbox.send(new LocalMessage<StreamReader>(actor, StreamReader.class, consumer, completes, streamForRepresentation2)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, streamForRepresentation2));
    }
    return null;
  }
}
