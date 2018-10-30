// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.eventjournal;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.common.BasicCompletes;
import io.vlingo.symbio.store.eventjournal.EventJournal;
import java.lang.String;

public class EventJournal__Proxy<T> implements io.vlingo.symbio.store.eventjournal.EventJournal<T> {

  private static final String appendRepresentation1 = "append(java.lang.String, int, io.vlingo.symbio.Event<T>, io.vlingo.symbio.store.eventjournal.EventJournal.AppendResultInterest<T>, java.lang.Object)";
  private static final String appendAllWithRepresentation2 = "appendAllWith(java.lang.String, int, java.util.List<io.vlingo.symbio.Event<T>>, io.vlingo.symbio.State<T>, io.vlingo.symbio.store.eventjournal.EventJournal.AppendResultInterest<T>, java.lang.Object)";
  private static final String appendWithRepresentation3 = "appendWith(java.lang.String, int, io.vlingo.symbio.Event<T>, io.vlingo.symbio.State<T>, io.vlingo.symbio.store.eventjournal.EventJournal.AppendResultInterest<T>, java.lang.Object)";
  private static final String appendAllRepresentation4 = "appendAll(java.lang.String, int, java.util.List<io.vlingo.symbio.Event<T>>, io.vlingo.symbio.store.eventjournal.EventJournal.AppendResultInterest<T>, java.lang.Object)";
  private static final String eventJournalReaderRepresentation5 = "eventJournalReader(java.lang.String)";
  private static final String eventStreamReaderRepresentation6 = "eventStreamReader(java.lang.String)";

  private final Actor actor;
  private final Mailbox mailbox;

  public EventJournal__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void append(java.lang.String arg0, int arg1, io.vlingo.symbio.Event<T> arg2, io.vlingo.symbio.store.eventjournal.EventJournal.AppendResultInterest<T> arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<EventJournal> consumer = (actor) -> actor.append(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, EventJournal.class, consumer, null, appendRepresentation1); }
      else { mailbox.send(new LocalMessage<EventJournal>(actor, EventJournal.class, consumer, appendRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendRepresentation1));
    }
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void appendAllWith(java.lang.String arg0, int arg1, java.util.List<io.vlingo.symbio.Event<T>> arg2, io.vlingo.symbio.State<T> arg3, io.vlingo.symbio.store.eventjournal.EventJournal.AppendResultInterest<T> arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<EventJournal> consumer = (actor) -> actor.appendAllWith(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, EventJournal.class, consumer, null, appendAllWithRepresentation2); }
      else { mailbox.send(new LocalMessage<EventJournal>(actor, EventJournal.class, consumer, appendAllWithRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendAllWithRepresentation2));
    }
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void appendWith(java.lang.String arg0, int arg1, io.vlingo.symbio.Event<T> arg2, io.vlingo.symbio.State<T> arg3, io.vlingo.symbio.store.eventjournal.EventJournal.AppendResultInterest<T> arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<EventJournal> consumer = (actor) -> actor.appendWith(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, EventJournal.class, consumer, null, appendWithRepresentation3); }
      else { mailbox.send(new LocalMessage<EventJournal>(actor, EventJournal.class, consumer, appendWithRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendWithRepresentation3));
    }
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void appendAll(java.lang.String arg0, int arg1, java.util.List<io.vlingo.symbio.Event<T>> arg2, io.vlingo.symbio.store.eventjournal.EventJournal.AppendResultInterest<T> arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<EventJournal> consumer = (actor) -> actor.appendAll(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, EventJournal.class, consumer, null, appendAllRepresentation4); }
      else { mailbox.send(new LocalMessage<EventJournal>(actor, EventJournal.class, consumer, appendAllRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendAllRepresentation4));
    }
  }
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<io.vlingo.symbio.store.eventjournal.EventJournalReader<T>> eventJournalReader(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<EventJournal> consumer = (actor) -> actor.eventJournalReader(arg0);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.eventjournal.EventJournalReader<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, EventJournal.class, consumer, completes, eventJournalReaderRepresentation5); }
      else { mailbox.send(new LocalMessage<EventJournal>(actor, EventJournal.class, consumer, completes, eventJournalReaderRepresentation5)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, eventJournalReaderRepresentation5));
    }
    return null;
  }
  @SuppressWarnings("rawtypes")
  public io.vlingo.common.Completes<io.vlingo.symbio.store.eventjournal.EventStreamReader<T>> eventStreamReader(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<EventJournal> consumer = (actor) -> actor.eventStreamReader(arg0);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.eventjournal.EventStreamReader<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, EventJournal.class, consumer, completes, eventStreamReaderRepresentation6); }
      else { mailbox.send(new LocalMessage<EventJournal>(actor, EventJournal.class, consumer, completes, eventStreamReaderRepresentation6)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, eventStreamReaderRepresentation6));
    }
    return null;
  }
}
