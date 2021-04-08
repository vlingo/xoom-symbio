package io.vlingo.xoom.symbio.store.object;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.DeadLetter;
import io.vlingo.xoom.actors.LocalMessage;
import io.vlingo.xoom.actors.Mailbox;
import io.vlingo.xoom.common.SerializableConsumer;
import io.vlingo.xoom.symbio.store.object.ObjectStoreWriter.PersistResultInterest;

public class ObjectStoreWriterPersistResultInterest__Proxy implements io.vlingo.xoom.symbio.store.object.ObjectStoreWriter.PersistResultInterest {

  private static final String persistResultedInRepresentation1 = "persistResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result>, java.lang.Object, int, int, java.lang.Object)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ObjectStoreWriterPersistResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void persistResultedIn(io.vlingo.xoom.common.Outcome<io.vlingo.xoom.symbio.store.StorageException, io.vlingo.xoom.symbio.store.Result> arg0, java.lang.Object arg1, int arg2, int arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final SerializableConsumer<PersistResultInterest> consumer = (actor) -> actor.persistResultedIn(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, PersistResultInterest.class, consumer, null, persistResultedInRepresentation1); }
      else { mailbox.send(new LocalMessage<PersistResultInterest>(actor, PersistResultInterest.class, consumer, persistResultedInRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistResultedInRepresentation1));
    }
  }
}
