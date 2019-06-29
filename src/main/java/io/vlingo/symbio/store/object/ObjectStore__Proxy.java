package io.vlingo.symbio.store.object;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;

import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

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
  private static final String persistRepresentation15 = "persist(java.lang.Object, java.util.List<io.vlingo.symbio.Source<E>>, io.vlingo.symbio.Metadata, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistRepresentation10 = "persist(java.lang.Object, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistRepresentation11 = "persist(java.lang.Object, java.util.List<io.vlingo.symbio.Source<E>>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistRepresentation12 = "persist(java.lang.Object, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistRepresentation13 = "persist(java.lang.Object, java.util.List<io.vlingo.symbio.Source<E>>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistRepresentation14 = "persist(java.lang.Object, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistAllRepresentation15 = "persistAll(java.util.Collection<java.lang.Object>, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation16 = "persistAll(java.util.Collection<java.lang.Object>, java.util.List<io.vlingo.symbio.Source<E>>, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistAllRepresentation17 = "persistAll(java.util.Collection<java.lang.Object>, java.util.List<io.vlingo.symbio.Source<E>>, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation23 = "persistAll(java.util.Collection<java.lang.Object>, java.util.List<io.vlingo.symbio.Source<E>>, io.vlingo.symbio.Metadata, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation18 = "persistAll(java.util.Collection<java.lang.Object>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistAllRepresentation19 = "persistAll(java.util.Collection<java.lang.Object>, java.util.List<io.vlingo.symbio.Source<E>>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";
  private static final String persistAllRepresentation20 = "persistAll(java.util.Collection<java.lang.Object>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation21 = "persistAll(java.util.Collection<java.lang.Object>, java.util.List<io.vlingo.symbio.Source<E>>, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation22 = "persistAll(java.util.Collection<java.lang.Object>, long, io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest)";

  private final Actor actor;
  private final Mailbox mailbox;

  public ObjectStore__Proxy(final Actor actor, final Mailbox mailbox) {
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public void close() {
    send(ObjectStore__Proxy.closeRepresentation1, ObjectStore::close);
  }

  @Override
  public void registerMapper(final io.vlingo.symbio.store.object.PersistentObjectMapper arg0) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.registerMapper(arg0);
    send(ObjectStore__Proxy.registerMapperRepresentation2, consumer);
  }

  @Override
  public void queryObject(final io.vlingo.symbio.store.object.QueryExpression arg0,
          final io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest arg1, final java.lang.Object arg2) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryObject(arg0, arg1, arg2);
    send(ObjectStore__Proxy.queryObjectRepresentation3, consumer);
  }

  @Override
  public void queryObject(final io.vlingo.symbio.store.object.QueryExpression arg0,
          final io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest arg1) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryObject(arg0, arg1);
    send(ObjectStore__Proxy.queryObjectRepresentation4, consumer);
  }

  @Override
  public void queryAll(final io.vlingo.symbio.store.object.QueryExpression arg0,
          final io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest arg1, final java.lang.Object arg2) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryAll(arg0, arg1, arg2);
    send(ObjectStore__Proxy.queryAllRepresentation5, consumer);
  }

  @Override
  public void queryAll(final io.vlingo.symbio.store.object.QueryExpression arg0,
          final io.vlingo.symbio.store.object.ObjectStoreReader.QueryResultInterest arg1) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryAll(arg0, arg1);
    send(ObjectStore__Proxy.queryAllRepresentation6, consumer);
  }

  @Override
  public <T extends PersistentObject, E> void persist(final T arg0, final java.util.List<io.vlingo.symbio.Source<E>> arg1,
          final long arg2, final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg3) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2, arg3);
    send(ObjectStore__Proxy.persistRepresentation7, consumer);
  }

  @Override
  public <T extends PersistentObject> void persist(final T arg0, final long arg1,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2, final java.lang.Object arg3) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2, arg3);
    send(ObjectStore__Proxy.persistRepresentation8, consumer);
  }

  @Override
  public <T extends PersistentObject, E> void persist(final T arg0, final java.util.List<io.vlingo.symbio.Source<E>> arg1,
          final long arg2, final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg3,
          final java.lang.Object arg4) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2, arg3, arg4);
    send(ObjectStore__Proxy.persistRepresentation9, consumer);
  }

  @Override
  public <T extends PersistentObject, E> void persist(final T arg1, final List<Source<E>> arg2, final Metadata arg3,
          final long arg4, final PersistResultInterest arg5, final Object arg6) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor
            .persist(arg1, arg2, arg3, arg4, arg5, arg6);
    send(ObjectStore__Proxy.persistRepresentation15, consumer);
  }

  @Override
  public <T extends PersistentObject> void persist(final T arg0,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg1) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1);
    send(ObjectStore__Proxy.persistRepresentation10, consumer);
  }

  @Override
  public <T extends PersistentObject, E> void persist(final T arg0, final java.util.List<io.vlingo.symbio.Source<E>> arg1,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2);
    send(ObjectStore__Proxy.persistRepresentation11, consumer);
  }

  @Override
  public <T extends PersistentObject> void persist(final T arg0,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg1, final java.lang.Object arg2) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2);
    send(ObjectStore__Proxy.persistRepresentation12, consumer);
  }

  @Override
  public <T extends PersistentObject, E> void persist(final T arg0, final java.util.List<io.vlingo.symbio.Source<E>> arg1,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2, final java.lang.Object arg3) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2, arg3);
    send(ObjectStore__Proxy.persistRepresentation13, consumer);
  }

  @Override
  public <T extends PersistentObject> void persist(final T arg0, final long arg1,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2);
    send(ObjectStore__Proxy.persistRepresentation14, consumer);
  }

  @Override
  public <T extends PersistentObject> void persistAll(final java.util.Collection<T> arg0, final long arg1,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2, final java.lang.Object arg3) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2, arg3);
    send(ObjectStore__Proxy.persistAllRepresentation15, consumer);
  }

  @Override
  public <T extends PersistentObject, E> void persistAll(final java.util.Collection<T> arg0,
          final java.util.List<io.vlingo.symbio.Source<E>> arg1, final long arg2,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg3) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2, arg3);
    send(ObjectStore__Proxy.persistAllRepresentation16, consumer);
  }

  @Override
  public <T extends PersistentObject, E> void persistAll(final java.util.Collection<T> arg0,
          final java.util.List<io.vlingo.symbio.Source<E>> arg1, final long arg2,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg3, final java.lang.Object arg4) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2, arg3, arg4);
    send(ObjectStore__Proxy.persistAllRepresentation17, consumer);
  }

  @Override
  public <T extends PersistentObject, E> void persistAll(final Collection<T> arg0, final List<Source<E>> arg1,
          final Metadata arg2, final long arg3, final PersistResultInterest arg4, final Object arg5) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2, arg3, arg4, arg5);
    send(ObjectStore__Proxy.persistAllRepresentation23, consumer);
  }

  @Override
  public <T extends PersistentObject> void persistAll(final java.util.Collection<T> arg0,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg1) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1);
    send(ObjectStore__Proxy.persistAllRepresentation18, consumer);
  }

  @Override
  public <T extends PersistentObject, E> void persistAll(final java.util.Collection<T> arg0,
          final java.util.List<io.vlingo.symbio.Source<E>> arg1,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2);
    send(ObjectStore__Proxy.persistAllRepresentation19, consumer);
  }

  @Override
  public <T extends PersistentObject> void persistAll(final java.util.Collection<T> arg0,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg1, final java.lang.Object arg2) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2);
    send(ObjectStore__Proxy.persistAllRepresentation20, consumer);
  }

  @Override
  public <T extends PersistentObject, E> void persistAll(final java.util.Collection<T> arg0,
          final java.util.List<io.vlingo.symbio.Source<E>> arg1,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2, final java.lang.Object arg3) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2, arg3);
    send(ObjectStore__Proxy.persistAllRepresentation21, consumer);
  }

  @Override
  public <T extends PersistentObject> void persistAll(final java.util.Collection<T> arg0, final long arg1,
          final io.vlingo.symbio.store.object.ObjectStoreWriter.PersistResultInterest arg2) {
    final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2);
    send(ObjectStore__Proxy.persistAllRepresentation22, consumer);
  }

  private void send(final String representation, final Consumer<ObjectStore> consumer) {
    if (!actor.isStopped()) {
      if (mailbox.isPreallocated()) {
        mailbox.send(actor, ObjectStore.class, consumer, null, representation);
      } else {
        mailbox.send(new LocalMessage<>(actor, ObjectStore.class, consumer, representation));
      }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, representation));
    }
  }
}
