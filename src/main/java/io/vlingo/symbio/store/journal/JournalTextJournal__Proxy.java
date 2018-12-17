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

public class JournalTextJournal__Proxy implements io.vlingo.symbio.store.journal.Journal.TextJournal {

  private static final String appendRepresentation1 = "append(java.lang.String, int, io.vlingo.symbio.Entry<T>, io.vlingo.symbio.store.journal.Journal.io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T>, java.lang.Object)";
  private static final String appendWithRepresentation2 = "appendWith(java.lang.String, int, io.vlingo.symbio.Entry<T>, io.vlingo.symbio.State<T>, io.vlingo.symbio.store.journal.Journal.io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T>, java.lang.Object)";
  private static final String appendAllRepresentation3 = "appendAll(java.lang.String, int, java.util.List<io.vlingo.symbio.Entry<T>>, io.vlingo.symbio.store.journal.Journal.io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T>, java.lang.Object)";
  private static final String appendAllWithRepresentation4 = "appendAllWith(java.lang.String, int, java.util.List<io.vlingo.symbio.Entry<T>>, io.vlingo.symbio.State<T>, io.vlingo.symbio.store.journal.Journal.io.vlingo.symbio.store.journal.Journal.AppendResultInterest<T>, java.lang.Object)";
  private static final String journalReaderRepresentation5 = "journalReader(java.lang.String)";
  private static final String streamReaderRepresentation6 = "streamReader(java.lang.String)";
  private static final String registerAdapterRepresentation7 = "registerAdapter(java.lang.Class<S>, io.vlingo.symbio.EntryAdapter<S, R>)";

  private final Actor actor;
  private final Mailbox mailbox;

  public JournalTextJournal__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public <S> void append(java.lang.String arg0, int arg1, io.vlingo.symbio.Source<S> arg2, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<String> arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextJournal> consumer = (actor) -> actor.append(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextJournal.class, consumer, null, appendRepresentation1); }
      else { mailbox.send(new LocalMessage<TextJournal>(actor, TextJournal.class, consumer, appendRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendRepresentation1));
    }
  }
  public <S> void appendWith(java.lang.String arg0, int arg1, io.vlingo.symbio.Source<S> arg2, io.vlingo.symbio.State<String> arg3, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<String> arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextJournal> consumer = (actor) -> actor.appendWith(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextJournal.class, consumer, null, appendWithRepresentation2); }
      else { mailbox.send(new LocalMessage<TextJournal>(actor, TextJournal.class, consumer, appendWithRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendWithRepresentation2));
    }
  }
  public <S> void appendAll(java.lang.String arg0, int arg1, java.util.List<io.vlingo.symbio.Source<S>> arg2, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<String> arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextJournal> consumer = (actor) -> actor.appendAll(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextJournal.class, consumer, null, appendAllRepresentation3); }
      else { mailbox.send(new LocalMessage<TextJournal>(actor, TextJournal.class, consumer, appendAllRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendAllRepresentation3));
    }
  }
  public <S> void appendAllWith(java.lang.String arg0, int arg1, java.util.List<io.vlingo.symbio.Source<S>> arg2, io.vlingo.symbio.State<String> arg3, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<String> arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextJournal> consumer = (actor) -> actor.appendAllWith(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextJournal.class, consumer, null, appendAllWithRepresentation4); }
      else { mailbox.send(new LocalMessage<TextJournal>(actor, TextJournal.class, consumer, appendAllWithRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendAllWithRepresentation4));
    }
  }
  public io.vlingo.common.Completes<io.vlingo.symbio.store.journal.JournalReader<String>> journalReader(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextJournal> consumer = (actor) -> actor.journalReader(arg0);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.journal.JournalReader<String>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextJournal.class, consumer, completes, journalReaderRepresentation5); }
      else { mailbox.send(new LocalMessage<TextJournal>(actor, TextJournal.class, consumer, completes, journalReaderRepresentation5)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, journalReaderRepresentation5));
    }
    return null;
  }
  public io.vlingo.common.Completes<io.vlingo.symbio.store.journal.StreamReader<String>> streamReader(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<TextJournal> consumer = (actor) -> actor.streamReader(arg0);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.journal.StreamReader<String>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, TextJournal.class, consumer, completes, streamReaderRepresentation6); }
      else { mailbox.send(new LocalMessage<TextJournal>(actor, TextJournal.class, consumer, completes, streamReaderRepresentation6)); }
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
