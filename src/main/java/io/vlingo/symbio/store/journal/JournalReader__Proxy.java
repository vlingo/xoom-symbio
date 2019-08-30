// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.actors.Returns;
import io.vlingo.common.BasicCompletes;
import io.vlingo.common.Completes;
import io.vlingo.symbio.Entry;

public class JournalReader__Proxy<T extends Entry<?>> implements io.vlingo.symbio.store.journal.JournalReader<T> {

  private static final String nameRepresentation0 = "close()";
  private static final String nameRepresentation1 = "name()";
  private static final String rewindRepresentation2 = "rewind()";
  private static final String seekToRepresentation3 = "seekTo(java.lang.String)";
  private static final String readNextRepresentation4 = "readNext()";
  private static final String readNextRepresentation5 = "readNext(int)";
  private static final String sizeRepresentation6 = "size()";

  private final Actor actor;
  private final Mailbox mailbox;

  public JournalReader__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public void close() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.close();
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, null, nameRepresentation0); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, null, nameRepresentation0)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, nameRepresentation0));
    }
  }

  @Override
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<java.lang.String> name() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.name();
      final io.vlingo.common.Completes<java.lang.String> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, Returns.value(completes), nameRepresentation1); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, Returns.value(completes), nameRepresentation1)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, nameRepresentation1));
    }
    return null;
  }
  @Override
  @SuppressWarnings("rawtypes")
  public void rewind() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.rewind();
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, null, rewindRepresentation2); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, rewindRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, rewindRepresentation2));
    }
  }
  @Override
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<java.lang.String> seekTo(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.seekTo(arg0);
      final io.vlingo.common.Completes<java.lang.String> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, Returns.value(completes), seekToRepresentation3); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, Returns.value(completes), seekToRepresentation3)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, seekToRepresentation3));
    }
    return null;
  }
  @Override
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<T> readNext() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.readNext();
      final io.vlingo.common.Completes<T> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, Returns.value(completes), readNextRepresentation4); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, Returns.value(completes), readNextRepresentation4)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readNextRepresentation4));
    }
    return null;
  }
  @Override
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<T> readNext(final String fromId) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.readNext(fromId);
      final io.vlingo.common.Completes<T> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, Returns.value(completes), readNextRepresentation4); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, Returns.value(completes), readNextRepresentation4)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readNextRepresentation4));
    }
    return null;
  }
  @Override
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<java.util.List<T>> readNext(int arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.readNext(arg0);
      final io.vlingo.common.Completes<java.util.List<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, Returns.value(completes), readNextRepresentation5); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, Returns.value(completes), readNextRepresentation5)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readNextRepresentation5));
    }
    return null;
  }
  @Override
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<java.util.List<T>> readNext(final String fromId, int arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.readNext(fromId, arg0);
      final io.vlingo.common.Completes<java.util.List<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, Returns.value(completes), readNextRepresentation5); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, Returns.value(completes), readNextRepresentation5)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readNextRepresentation5));
    }
    return null;
  }

  @Override
  @SuppressWarnings("rawtypes")
  public Completes<Long> size() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.size();
      final io.vlingo.common.Completes<Long> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, Returns.value(completes), sizeRepresentation6); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, Returns.value(completes), sizeRepresentation6)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, sizeRepresentation6));
    }
    return null;
  }
}
