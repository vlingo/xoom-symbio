package io.vlingo.symbio.store.object;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest;

public class ObjectStoreWriterPersistResultInterest__Proxy implements io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest {

  private static final String persistResultedInRepresentation1 = "persistResultedIn(io.vlingo.common.Outcome<io.vlingo.symbio.store.StorageException, io.vlingo.symbio.store.Result>, java.lang.Object, int, int, java.lang.Object)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ObjectStoreWriterPersistResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void persistResultedIn(io.vlingo.common.Outcome<io.vlingo.symbio.store.StorageException, io.vlingo.symbio.store.Result> arg0, java.lang.Object arg1, int arg2, int arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<PersistResultInterest> consumer = (actor) -> actor.persistResultedIn(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, PersistResultInterest.class, consumer, null, persistResultedInRepresentation1); }
      else { mailbox.send(new LocalMessage<PersistResultInterest>(actor, PersistResultInterest.class, consumer, persistResultedInRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistResultedInRepresentation1));
    }
  }
}
