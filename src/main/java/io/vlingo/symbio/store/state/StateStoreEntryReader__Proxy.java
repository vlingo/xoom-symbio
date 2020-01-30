package io.vlingo.symbio.store.state;

import io.vlingo.actors.*;
import io.vlingo.common.BasicCompletes;
import io.vlingo.common.SerializableConsumer;

@SuppressWarnings("rawtypes")
public class StateStoreEntryReader__Proxy<T extends io.vlingo.symbio.Entry<?>> implements io.vlingo.symbio.store.state.StateStoreEntryReader<T> {

  private static final String nameRepresentation1 = "name()";
  private static final String sizeRepresentation2 = "size()";
  private static final String closeRepresentation3 = "close()";
  private static final String rewindRepresentation4 = "rewind()";
  private static final String readNextRepresentation5 = "readNext(int)";
  private static final String readNextRepresentation6 = "readNext(java.lang.String)";
  private static final String readNextRepresentation7 = "readNext(java.lang.String, int)";
  private static final String readNextRepresentation8 = "readNext()";
  private static final String seekToRepresentation9 = "seekTo(java.lang.String)";

  private final Actor actor;
  private final Mailbox mailbox;

  public StateStoreEntryReader__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  @Override
  public io.vlingo.common.Completes<java.lang.String> name() {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStoreEntryReader> consumer = (actor) -> actor.name();
      final io.vlingo.common.Completes<java.lang.String> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), nameRepresentation1); }
      else { mailbox.send(new LocalMessage<StateStoreEntryReader>(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), nameRepresentation1)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, nameRepresentation1));
    }
    return null;
  }
  @Override
  public io.vlingo.common.Completes<java.lang.Long> size() {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStoreEntryReader> consumer = (actor) -> actor.size();
      final io.vlingo.common.Completes<java.lang.Long> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), sizeRepresentation2); }
      else { mailbox.send(new LocalMessage<StateStoreEntryReader>(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), sizeRepresentation2)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, sizeRepresentation2));
    }
    return null;
  }
  @Override
  public void close() {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStoreEntryReader> consumer = (actor) -> actor.close();
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStoreEntryReader.class, consumer, null, closeRepresentation3); }
      else { mailbox.send(new LocalMessage<StateStoreEntryReader>(actor, StateStoreEntryReader.class, consumer, closeRepresentation3)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, closeRepresentation3));
    }
  }
  @Override
  public void rewind() {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStoreEntryReader> consumer = (actor) -> actor.rewind();
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStoreEntryReader.class, consumer, null, rewindRepresentation4); }
      else { mailbox.send(new LocalMessage<StateStoreEntryReader>(actor, StateStoreEntryReader.class, consumer, rewindRepresentation4)); }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, rewindRepresentation4));
    }
  }
  @Override
  public io.vlingo.common.Completes<java.util.List<T>> readNext(int arg0) {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStoreEntryReader> consumer = (actor) -> actor.readNext(arg0);
      final io.vlingo.common.Completes<java.util.List<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), readNextRepresentation5); }
      else { mailbox.send(new LocalMessage<StateStoreEntryReader>(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), readNextRepresentation5)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readNextRepresentation5));
    }
    return null;
  }
  @Override
  public io.vlingo.common.Completes<T> readNext(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStoreEntryReader> consumer = (actor) -> actor.readNext(arg0);
      final io.vlingo.common.Completes<T> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), readNextRepresentation6); }
      else { mailbox.send(new LocalMessage<StateStoreEntryReader>(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), readNextRepresentation6)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readNextRepresentation6));
    }
    return null;
  }
  @Override
  public io.vlingo.common.Completes<java.util.List<T>> readNext(java.lang.String arg0, int arg1) {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStoreEntryReader> consumer = (actor) -> actor.readNext(arg0, arg1);
      final io.vlingo.common.Completes<java.util.List<T>> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), readNextRepresentation7); }
      else { mailbox.send(new LocalMessage<StateStoreEntryReader>(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), readNextRepresentation7)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readNextRepresentation7));
    }
    return null;
  }
  @Override
  public io.vlingo.common.Completes<T> readNext() {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStoreEntryReader> consumer = (actor) -> actor.readNext();
      final io.vlingo.common.Completes<T> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), readNextRepresentation8); }
      else { mailbox.send(new LocalMessage<StateStoreEntryReader>(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), readNextRepresentation8)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, readNextRepresentation8));
    }
    return null;
  }
  @Override
  public io.vlingo.common.Completes<java.lang.String> seekTo(java.lang.String arg0) {
    if (!actor.isStopped()) {
      final SerializableConsumer<StateStoreEntryReader> consumer = (actor) -> actor.seekTo(arg0);
      final io.vlingo.common.Completes<java.lang.String> completes = new BasicCompletes<>(actor.scheduler());
      if (mailbox.isPreallocated()) { mailbox.send(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), seekToRepresentation9); }
      else { mailbox.send(new LocalMessage<StateStoreEntryReader>(actor, StateStoreEntryReader.class, consumer, Returns.value(completes), seekToRepresentation9)); }
      return completes;
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, seekToRepresentation9));
    }
    return null;
  }
}
