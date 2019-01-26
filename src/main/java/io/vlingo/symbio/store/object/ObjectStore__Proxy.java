// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.object;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;

public class ObjectStore__Proxy implements io.vlingo.symbio.store.object.ObjectStore {

  private static final String registerMapperRepresentation1 = "registerMapper(io.vlingo.symbio.store.object.PersistentObjectMapper)";
  private static final String persistRepresentation2 = "persist(java.lang.Object, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest, java.lang.Object)";
  private static final String persistRepresentation3 = "persist(java.lang.Object, long, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest)";
  private static final String persistRepresentation4 = "persist(java.lang.Object, long, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest, java.lang.Object)";
  private static final String persistRepresentation5 = "persist(java.lang.Object, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest)";
  private static final String persistAllRepresentation6 = "persistAll(java.util.Collection<java.lang.Object>, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation7 = "persistAll(java.util.Collection<java.lang.Object>, long, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest)";
  private static final String persistAllRepresentation8 = "persistAll(java.util.Collection<java.lang.Object>, long, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest, java.lang.Object)";
  private static final String persistAllRepresentation9 = "persistAll(java.util.Collection<java.lang.Object>, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest)";
  private static final String queryAllRepresentation10 = "queryAll(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest, java.lang.Object)";
  private static final String queryAllRepresentation11 = "queryAll(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest)";
  private static final String queryObjectRepresentation12 = "queryObject(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest, java.lang.Object)";
  private static final String queryObjectRepresentation13 = "queryObject(io.vlingo.symbio.store.object.QueryExpression, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest)";
  private static final String queryObjectRepresentation14 = "close()";

  private final Actor actor;
  private final Mailbox mailbox;

  public ObjectStore__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public void registerMapper(io.vlingo.symbio.store.object.PersistentObjectMapper arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.registerMapper(arg0);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, registerMapperRepresentation1); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, registerMapperRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, registerMapperRepresentation1));
    }
  }
  public void persist(java.lang.Object arg0, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation2); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation2));
    }
  }
  public void persist(java.lang.Object arg0, long arg1, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation3); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation3));
    }
  }
  public void persist(java.lang.Object arg0, long arg1, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest arg2, java.lang.Object arg3) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation4); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation4));
    }
  }
  public void persist(java.lang.Object arg0, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persist(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistRepresentation5); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistRepresentation5)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistRepresentation5));
    }
  }
  public void persistAll(java.util.Collection<java.lang.Object> arg0, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation6); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation6)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation6));
    }
  }
  public void persistAll(java.util.Collection<java.lang.Object> arg0, long arg1, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation7); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation7)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation7));
    }
  }
  public void persistAll(java.util.Collection<java.lang.Object> arg0, long arg1, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest arg2, java.lang.Object arg3) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1, arg2, arg3);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation8); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation8)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation8));
    }
  }
  public void persistAll(java.util.Collection<java.lang.Object> arg0, io.vlingo.symbio.store.object.ObjectStore.PersistResultInterest arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.persistAll(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, persistAllRepresentation9); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, persistAllRepresentation9)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, persistAllRepresentation9));
    }
  }
  public void queryAll(io.vlingo.symbio.store.object.QueryExpression arg0, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryAll(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, queryAllRepresentation10); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, queryAllRepresentation10)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryAllRepresentation10));
    }
  }
  public void queryAll(io.vlingo.symbio.store.object.QueryExpression arg0, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryAll(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, queryAllRepresentation11); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, queryAllRepresentation11)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryAllRepresentation11));
    }
  }
  public void queryObject(io.vlingo.symbio.store.object.QueryExpression arg0, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest arg1, java.lang.Object arg2) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryObject(arg0, arg1, arg2);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, queryObjectRepresentation12); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, queryObjectRepresentation12)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryObjectRepresentation12));
    }
  }
  public void queryObject(io.vlingo.symbio.store.object.QueryExpression arg0, io.vlingo.symbio.store.object.ObjectStore.QueryResultInterest arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.queryObject(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, queryObjectRepresentation13); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, queryObjectRepresentation13)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryObjectRepresentation13));
    }
  }
  public void close() {
    // 
    if (!actor.isStopped()) {
      final java.util.function.Consumer<ObjectStore> consumer = (actor) -> actor.close();
      if (mailbox.isPreallocated()) { mailbox.send(actor, ObjectStore.class, consumer, null, queryObjectRepresentation14); }
      else { mailbox.send(new LocalMessage<ObjectStore>(actor, ObjectStore.class, consumer, queryObjectRepresentation14)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, queryObjectRepresentation14));
    }
  }
}
