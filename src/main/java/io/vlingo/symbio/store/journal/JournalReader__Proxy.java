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
import io.vlingo.common.BasicCompletes;

public class JournalReader__Proxy<T> implements io.vlingo.symbio.store.journal.JournalReader<T> {

  private static final String nameRepresentation1 = "name()";
  private static final String rewindRepresentation2 = "rewind()";
  private static final String seekToRepresentation3 = "seekTo(java.lang.String)";
  private static final String readNextRepresentation4 = "readNext()";
  private static final String readNextRepresentation5 = "readNext(int)";

  private final Actor actor;
  private final Mailbox mailbox;

  public JournalReader__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<java.lang.String> name() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.name();
      final io.vlingo.common.Completes<java.lang.String> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, completes, nameRepresentation1); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, completes, nameRepresentation1)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, nameRepresentation1));
    }
    return null;
  }
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
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<java.lang.String> seekTo(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.seekTo(arg0);
      final io.vlingo.common.Completes<java.lang.String> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, completes, seekToRepresentation3); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, completes, seekToRepresentation3)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, seekToRepresentation3));
    }
    return null;
  }
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<io.vlingo.symbio.Entry<T>> readNext() {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.readNext();
      final io.vlingo.common.Completes<io.vlingo.symbio.Entry<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, completes, readNextRepresentation4); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, completes, readNextRepresentation4)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readNextRepresentation4));
    }
    return null;
  }
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<java.util.List<io.vlingo.symbio.Entry<T>>> readNext(int arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<JournalReader> consumer = (actor) -> actor.readNext(arg0);
      final io.vlingo.common.Completes<java.util.List<io.vlingo.symbio.Entry<T>>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, JournalReader.class, consumer, completes, readNextRepresentation5); }
      else { mailbox.send(new LocalMessage<JournalReader>(actor, JournalReader.class, consumer, completes, readNextRepresentation5)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readNextRepresentation5));
    }
    return null;
  }
}
