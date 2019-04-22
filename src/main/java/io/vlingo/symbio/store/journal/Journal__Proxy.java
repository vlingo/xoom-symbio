package io.vlingo.symbio.store.journal;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.common.BasicCompletes;
import io.vlingo.symbio.Entry;

public class Journal__Proxy<T> implements io.vlingo.symbio.store.journal.Journal<T> {

  private static final String appendRepresentation1 = "append(java.lang.String, int, io.vlingo.symbio.Source<S>, io.vlingo.symbio.store.journal.Journal.io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String appendAllRepresentation2 = "appendAll(java.lang.String, int, java.util.List<io.vlingo.symbio.Source<S>>, io.vlingo.symbio.store.journal.Journal.io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String appendAllWithRepresentation3 = "appendAllWith(java.lang.String, int, java.util.List<io.vlingo.symbio.Source<S>>, ST, io.vlingo.symbio.store.journal.Journal.io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String appendWithRepresentation4 = "appendWith(java.lang.String, int, io.vlingo.symbio.Source<S>, ST, io.vlingo.symbio.store.journal.Journal.io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String journalReaderRepresentation5 = "journalReader(java.lang.String)";
  private static final String streamReaderRepresentation6 = "streamReader(java.lang.String)";

  private final Actor actor;
  private final Mailbox mailbox;

  public Journal__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <S, ST>void append(java.lang.String arg0, int arg1, io.vlingo.symbio.Source<S> arg2, io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.append(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, null, appendRepresentation1); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, appendRepresentation1)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendRepresentation1));
    }
  }
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <S, ST>void appendAll(java.lang.String arg0, int arg1, java.util.List<io.vlingo.symbio.Source<S>> arg2, io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg3, java.lang.Object arg4) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.appendAll(arg0, arg1, arg2, arg3, arg4);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, null, appendAllRepresentation2); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, appendAllRepresentation2)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendAllRepresentation2));
    }
  }
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <S, ST>void appendAllWith(java.lang.String arg0, int arg1, java.util.List<io.vlingo.symbio.Source<S>> arg2, ST arg3, io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.appendAllWith(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, null, appendAllWithRepresentation3); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, appendAllWithRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendAllWithRepresentation3));
    }
  }
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public <S, ST>void appendWith(java.lang.String arg0, int arg1, io.vlingo.symbio.Source<S> arg2, ST arg3, io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg4, java.lang.Object arg5) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.appendWith(arg0, arg1, arg2, arg3, arg4, arg5);
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, null, appendWithRepresentation4); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, appendWithRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, appendWithRepresentation4));
    }
  }
  @Override
  @SuppressWarnings("rawtypes")
  public <ET extends Entry<?>> io.vlingo.common.Completes<io.vlingo.symbio.store.journal.JournalReader<ET>> journalReader(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.journalReader(arg0);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.journal.JournalReader<ET>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, Journal.class, consumer, completes, journalReaderRepresentation5); }
      else { mailbox.send(new LocalMessage<Journal>(actor, Journal.class, consumer, completes, journalReaderRepresentation5)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, journalReaderRepresentation5));
    }
    return null;
  }
  @Override
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
}
