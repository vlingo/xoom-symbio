package io.vlingo.symbio.store.object;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;

public class ObjectStore__Proxy implements io.vlingo.symbio.store.object.ObjectStore {

  private static final String closeRepresentation1 = "close()";
  private static final String registerMapperRepresentation2 = "registerMapper(io.vlingo.symbio.store.object.PersistentObjectMapper)";
  private static final String queryObjectRepresentation3 = "queryObject(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest, java.lang.Object)";
  private static final String queryObjectRepresentation4 = "queryObject(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest)";
  private static final String queryAllRepresentation5 = "queryAll(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest, java.lang.Object)";
  private static final String queryAllRepresentation6 = "queryAll(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest)";
  private static final String persistRepresentation7 = "persist(java.lang.Object, java.util.List<io.vlingo.symbio.Source<E>>, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistRepresentation8 = "persist(java.lang.Object, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistRepresentation9 = "persist(java.lang.Object, java.util.List<io.vlingo.symbio.Source<E>>, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistRepresentation10 = "persist(java.lang.Object, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistRepresentation11 = "persist(java.lang.Object, java.util.List<io.vlingo.symbio.Source<E>>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistRepresentation12 = "persist(java.lang.Object, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistRepresentation13 = "persist(java.lang.Object, java.util.List<io.vlingo.symbio.Source<E>>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistRepresentation14 = "persist(java.lang.Object, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistAllRepresentation15 = "persistAll(java.util.Collection<java.lang.Object>, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation16 = "persistAll(java.util.Collection<java.lang.Object>, java.util.List<io.vlingo.symbio.Source<E>>, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistAllRepresentation17 = "persistAll(java.util.Collection<java.lang.Object>, java.util.List<io.vlingo.symbio.Source<E>>, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation18 = "persistAll(java.util.Collection<java.lang.Object>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistAllRepresentation19 = "persistAll(java.util.Collection<java.lang.Object>, java.util.List<io.vlingo.symbio.Source<E>>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistAllRepresentation20 = "persistAll(java.util.Collection<java.lang.Object>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation21 = "persistAll(java.util.Collection<java.lang.Object>, java.util.List<io.vlingo.symbio.Source<E>>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation22 = "persistAll(java.util.Collection<java.lang.Object>, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ObjectStore__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void close() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.close();
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, closeRepresentation1); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, closeRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, closeRepresentation1));
    }
  }
  @Override
  public void registerMapper(io.vlingo.symbio.store.object.PersistentObjectMapper arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.registerMapper(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, registerMapperRepresentation2); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, registerMapperRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, registerMapperRepresentation2));
    }
  }
  @Override
  public void queryObject(io.vlingo.symbio.store.object.QueryExpression arg0, io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryObject(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, queryObjectRepresentation3); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, queryObjectRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryObjectRepresentation3));
    }
  }
  @Override
  public void queryObject(io.vlingo.symbio.store.object.QueryExpression arg0, io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryObject(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, queryObjectRepresentation4); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, queryObjectRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryObjectRepresentation4));
    }
  }
  @Override
  public void queryAll(io.vlingo.symbio.store.object.QueryExpression arg0, io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryAll(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, queryAllRepresentation5); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, queryAllRepresentation5)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryAllRepresentation5));
    }
  }
  @Override
  public void queryAll(io.vlingo.symbio.store.object.QueryExpression arg0, io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryAll(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, queryAllRepresentation6); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, queryAllRepresentation6)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryAllRepresentation6));
    }
  }
  @Override
  public <T extends PersistentObject, E>void persist(T arg0, java.util.List<io.vlingo.symbio.Source<E>> arg1, long arg2, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg3) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation7); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation7)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation7));
    }
  }
  @Override
  public <T extends PersistentObject> void persist(T arg0, long arg1, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2, java.lang.Object arg3) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation8); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation8)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation8));
    }
  }
  @Override
  public <T extends PersistentObject, E>void persist(T arg0, java.util.List<io.vlingo.symbio.Source<E>> arg1, long arg2, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation9); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation9)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation9));
    }
  }
  @Override
  public <T extends PersistentObject> void persist(T arg0, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation10); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation10)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation10));
    }
  }
  @Override
  public <T extends PersistentObject, E>void persist(T arg0, java.util.List<io.vlingo.symbio.Source<E>> arg1, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation11); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation11)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation11));
    }
  }
  @Override
  public <T extends PersistentObject> void persist(T arg0, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation12); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation12)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation12));
    }
  }
  @Override
  public <T extends PersistentObject, E>void persist(T arg0, java.util.List<io.vlingo.symbio.Source<E>> arg1, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2, java.lang.Object arg3) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation13); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation13)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation13));
    }
  }
  @Override
  public <T extends PersistentObject> void persist(T arg0, long arg1, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation14); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation14)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation14));
    }
  }
  @Override
  public <T extends PersistentObject> void persistAll(java.util.Collection<T> arg0, long arg1, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2, java.lang.Object arg3) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation15); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation15)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation15));
    }
  }
  @Override
  public <T extends PersistentObject, E>void persistAll(java.util.Collection<T> arg0, java.util.List<io.vlingo.symbio.Source<E>> arg1, long arg2, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg3) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation16); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation16)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation16));
    }
  }
  @Override
  public <T extends PersistentObject, E>void persistAll(java.util.Collection<T> arg0, java.util.List<io.vlingo.symbio.Source<E>> arg1, long arg2, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation17); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation17)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation17));
    }
  }
  @Override
  public <T extends PersistentObject> void persistAll(java.util.Collection<T> arg0, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation18); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation18)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation18));
    }
  }
  @Override
  public <T extends PersistentObject, E>void persistAll(java.util.Collection<T> arg0, java.util.List<io.vlingo.symbio.Source<E>> arg1, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation19); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation19)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation19));
    }
  }
  @Override
  public <T extends PersistentObject> void persistAll(java.util.Collection<T> arg0, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation20); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation20)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation20));
    }
  }
  @Override
  public <T extends PersistentObject, E>void persistAll(java.util.Collection<T> arg0, java.util.List<io.vlingo.symbio.Source<E>> arg1, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2, java.lang.Object arg3) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation21); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation21)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation21));
    }
  }
  @Override
  public <T extends PersistentObject> void persistAll(java.util.Collection<T> arg0, long arg1, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation22); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation22)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation22));
    }
  }
}
