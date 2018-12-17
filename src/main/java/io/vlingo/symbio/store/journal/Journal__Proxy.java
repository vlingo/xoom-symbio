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
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapter;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.journal.Journal;

import java.lang.String;

public class Journal__Proxy<T> implements io.vlingo.symbio.store.journal.Journal<T> {

  private static final String appendRepresentation1 = "append(java.lang.String, int, io.vlingo.symbio.Entry<T>, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T>, java.lang.Object)";
  private static final String appendAllWithRepresentation2 = "appendAllWith(java.lang.String, int, java.util.List<io.vlingo.symbio.Entry<T>>, io.vlingo.symbio.State<T>, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T>, java.lang.Object)";
  private static final String appendWithRepresentation3 = "appendWith(java.lang.String, int, io.vlingo.symbio.Entry<T>, io.vlingo.symbio.State<T>, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T>, java.lang.Object)";
  private static final String appendAllRepresentation4 = "appendAll(java.lang.String, int, java.util.List<io.vlingo.symbio.Entry<T>>, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T>, java.lang.Object)";
  private static final String journalReaderRepresentation5 = "journalReader(java.lang.String)";
  private static final String streamReaderRepresentation6 = "streamReader(java.lang.String)";
  private static final String registerAdapterRepresentation7 = "registerAdapter(java.lang.Class<S>, io.vlingo.symbio.EntryAdapter<S, R>)";

  private final Actor actor;
  private final Mailbox mailbox;

  public Journal__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <S> void append(java.lang.String arg0, int arg1, io.vlingo.symbio.Source<S> arg2, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T> arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.append(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, null, appendRepresentation1); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, appendRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendRepresentation1));
    }
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <S> void appendAllWith(java.lang.String arg0, int arg1, java.util.List<io.vlingo.symbio.Source<S>> arg2, io.vlingo.symbio.State<T> arg3, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T> arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.appendAllWith(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, null, appendAllWithRepresentation2); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, appendAllWithRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendAllWithRepresentation2));
    }
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <S> void appendWith(java.lang.String arg0, int arg1, io.vlingo.symbio.Source<S> arg2, io.vlingo.symbio.State<T> arg3, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T> arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.appendWith(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, null, appendWithRepresentation3); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, appendWithRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendWithRepresentation3));
    }
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <S> void appendAll(java.lang.String arg0, int arg1, java.util.List<io.vlingo.symbio.Source<S>> arg2, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T> arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.appendAll(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, null, appendAllRepresentation4); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, appendAllRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendAllRepresentation4));
    }
  }
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<io.vlingo.symbio.store.journal.JournalReader<T>> journalReader(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.journalReader(arg0);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.journal.JournalReader<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, completes, journalReaderRepresentation5); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, completes, journalReaderRepresentation5)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, journalReaderRepresentation5));
    }
    return null;
  }
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<io.vlingo.symbio.store.journal.StreamReader<T>> streamReader(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.streamReader(arg0);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.journal.StreamReader<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, completes, streamReaderRepresentation6); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, completes, streamReaderRepresentation6)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, streamReaderRepresentation6));
    }
    return null;
  }
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public <S extends Source<?>,E extends Entry<?>> void registerAdapter(final Class<S> arg0, final EntryAdapter<S,E> arg1) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.registerAdapter(arg0, arg1);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, null, registerAdapterRepresentation7); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, registerAdapterRepresentation7)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, registerAdapterRepresentation7));
    }
  }
}
