package io.vlingo.symbio.store.journal;

import java.util.function.Consumer;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.actors.Returns;
import io.vlingo.common.BasicCompletes;
import io.vlingo.symbio.Entry;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Journal__Proxy<T> implements io.vlingo.symbio.store.journal.Journal<T> {

  private static final String appendRepresentation1 = "append(java.lang.String, int, io.vlingo.symbio.Source<S>, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String appendRepresentation2 = "append(java.lang.String, int, io.vlingo.symbio.Source<S>, io.vlingo.symbio.Metadata, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String appendAllRepresentation = "appendAll(java.lang.String, int, java.util.List<io.vlingo.symbio.Source<S>>, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String appendAllRepresentation2 = "appendAll(java.lang.String, int, java.util.List<io.vlingo.symbio.Source<S>>,io.vlingo.symbio.Metadata, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String appendAllWithRepresentation1 = "appendAllWith(java.lang.String, int, java.util.List<io.vlingo.symbio.Source<S>>, ST, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String appendAllWithRepresentation2 = "appendAllWith(java.lang.String, int, java.util.List<io.vlingo.symbio.Source<S>>, io.vlingo.symbio.Metadata, ST, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String appendWithRepresentation1 = "appendWith(java.lang.String, int, io.vlingo.symbio.Source<S>, ST, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String appendWithRepresentation2 = "appendWith(java.lang.String, int, io.vlingo.symbio.Source<S>, io.vlingo.symbio.Metadata, ST, io.vlingo.symbio.store.journal.Journal.AppendResultInterest<ST>, java.lang.Object)";
  private static final String journalReaderRepresentation5 = "journalReader(java.lang.String)";
  private static final String streamReaderRepresentation6 = "streamReader(java.lang.String)";

  private final Actor actor;
  private final Mailbox mailbox;

  public Journal__Proxy(final Actor actor, final Mailbox mailbox) {
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public <S, ST> void append(final java.lang.String arg0, final int arg1, final io.vlingo.symbio.Source<S> arg2,
          final io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg3, final java.lang.Object arg4) {
    final java.util.function.Consumer<Journal> consumer = (actor) -> actor.append(arg0, arg1, arg2, arg3, arg4);
    send(Journal__Proxy.appendRepresentation1, consumer);
  }

  @Override
  public <S, ST> void append(final java.lang.String arg0, final int arg1, final io.vlingo.symbio.Source<S> arg2,
          final io.vlingo.symbio.Metadata arg3, final io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg4,
          final java.lang.Object arg5) {
    final java.util.function.Consumer<Journal> consumer = (actor) -> actor.append(arg0, arg1, arg2, arg3, arg4, arg5);
    send(Journal__Proxy.appendRepresentation2, consumer);
  }

  @Override
  public <S, ST> void appendAll(final java.lang.String arg0, final int arg1,
          final java.util.List<io.vlingo.symbio.Source<S>> arg2,
          final io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg3, final java.lang.Object arg4) {
    final java.util.function.Consumer<Journal> consumer = (actor) -> actor.appendAll(arg0, arg1, arg2, arg3, arg4);
    send(Journal__Proxy.appendAllRepresentation, consumer);
  }

  @Override
  public <S, ST> void appendAll(final java.lang.String arg0, final int arg1,
          final java.util.List<io.vlingo.symbio.Source<S>> arg2, final io.vlingo.symbio.Metadata arg3,
          final io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg4, final java.lang.Object arg5) {
    final java.util.function.Consumer<Journal> consumer = (actor) -> actor
            .appendAll(arg0, arg1, arg2, arg3, arg4, arg5);
    send(Journal__Proxy.appendAllRepresentation2, consumer);
  }

  @Override
  public <S, ST> void appendAllWith(final java.lang.String arg0, final int arg1,
          final java.util.List<io.vlingo.symbio.Source<S>> arg2, final ST arg3,
          final io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg4, final java.lang.Object arg5) {
    final java.util.function.Consumer<Journal> consumer = (actor) -> actor
            .appendAllWith(arg0, arg1, arg2, arg3, arg4, arg5);
    send(Journal__Proxy.appendAllWithRepresentation1, consumer);
  }

  @Override
  public <S, ST> void appendAllWith(final java.lang.String arg0, final int arg1,
          final java.util.List<io.vlingo.symbio.Source<S>> arg2, final io.vlingo.symbio.Metadata arg3, final ST arg4,
          final io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg5, final java.lang.Object arg6) {
    final java.util.function.Consumer<Journal> consumer = (actor) -> actor
            .appendAllWith(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    send(Journal__Proxy.appendAllWithRepresentation2, consumer);
  }

  @Override
  public <S, ST> void appendWith(final java.lang.String arg0, final int arg1, final io.vlingo.symbio.Source<S> arg2,
          final ST arg3, final io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg4,
          final java.lang.Object arg5) {
    final java.util.function.Consumer<Journal> consumer = (actor) -> actor
            .appendWith(arg0, arg1, arg2, arg3, arg4, arg5);
    send(Journal__Proxy.appendWithRepresentation1, consumer);
  }

  @Override
  public <S, ST> void appendWith(final java.lang.String arg0, final int arg1, final io.vlingo.symbio.Source<S> arg2,
          final io.vlingo.symbio.Metadata arg3, final ST arg4,
          final io.vlingo.symbio.store.journal.Journal.AppendResultInterest arg5, final java.lang.Object arg6) {
    final java.util.function.Consumer<Journal> consumer = (actor) -> actor
            .appendWith(arg0, arg1, arg2, arg3, arg4, arg5, arg6);
    send(Journal__Proxy.appendWithRepresentation2, consumer);
  }

  private void send(final String representation, final Consumer<Journal> consumer) {
    if (!actor.isStopped()) {
      if (mailbox.isPreallocated()) {
        mailbox.send(actor, Journal.class, consumer, null, representation);
      } else {
        mailbox.send(new LocalMessage<>(actor, Journal.class, consumer, representation));
      }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, representation));
    }
  }

  @Override
  public <ET extends Entry<?>> io.vlingo.common.Completes<io.vlingo.symbio.store.journal.JournalReader<ET>> journalReader(final java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.journalReader(arg0);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.journal.JournalReader<ET>> completes = new BasicCompletes<>(
              actor.scheduler());
      if (mailbox.isPreallocated()) {
        mailbox.send(actor, Journal.class, consumer, Returns.value(completes), journalReaderRepresentation5);
      } else {
        mailbox.send(new LocalMessage<>(actor, Journal.class, consumer, Returns.value(completes), journalReaderRepresentation5));
      }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, journalReaderRepresentation5));
    }
    return null;
  }

  @Override
  public io.vlingo.common.Completes<io.vlingo.symbio.store.journal.StreamReader<T>> streamReader(final java.lang.String arg0) {
    if (!actor.isStopped()) {
      final java.util.function.Consumer<Journal> consumer = (actor) -> actor.streamReader(arg0);
      final io.vlingo.common.Completes<io.vlingo.symbio.store.journal.StreamReader<T>> completes = new BasicCompletes<>(
              actor.scheduler());
      if (mailbox.isPreallocated()) {
        mailbox.send(actor, Journal.class, consumer, Returns.value(completes), streamReaderRepresentation6);
      } else {
        mailbox.send(new LocalMessage<>(actor, Journal.class, consumer, Returns.value(completes), streamReaderRepresentation6));
      }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, streamReaderRepresentation6));
    }
    return null;
  }
}
