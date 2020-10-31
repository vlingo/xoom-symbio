// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.vlingo.actors.Actor;
import io.vlingo.actors.ActorInstantiator;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.common.Completes;
import io.vlingo.reactivestreams.Stream;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.QueryExpression;
import io.vlingo.symbio.store.state.PartitioningStateStore.InstantiationType;
import io.vlingo.symbio.store.state.PartitioningStateStore.InstantiatorProvider;

public abstract class MessageCountingStateStoreActor extends Actor implements StateStore {
  private final MessageCountingResults results;
  private final int totalPartitions;

  public MessageCountingStateStoreActor(final MessageCountingResults results, final int totalPartitions) {
    this.results = results;
    this.totalPartitions = totalPartitions;
  }

  @Override
  public void read(String id, Class<?> type, ReadResultInterest interest, Object object) {
    results.incrementRead(id, totalPartitions);
  }

  @Override
  public void readAll(Collection<TypedStateBundle> bundles, ReadResultInterest interest, Object object) {
    results.incrementReadAll();
  }

  @Override
  public Completes<Stream> streamAllOf(Class<?> stateType) {
    results.incrementStreamAllOf();

    return completes().with(null);
  }

  @Override
  public Completes<Stream> streamSomeUsing(QueryExpression query) {
    results.incrementStreamSomeUsing();

    return completes().with(null);
  }

  @Override
  public <S, C> void write(String id, S state, int stateVersion, List<Source<C>> sources, Metadata metadata, WriteResultInterest interest, Object object) {
    results.incrementWrite(id, totalPartitions);
  }

  @Override
  public <ET extends Entry<?>> Completes<StateStoreEntryReader<ET>> entryReader(String name) {
    results.incrementEntryReader();

    return completes().with(null);
  }

  public static class ReaderMessageCountingStateStoreActor extends MessageCountingStateStoreActor {
    public ReaderMessageCountingStateStoreActor(MessageCountingResults results, final int totalPartitions) {
      super(results, totalPartitions);

      results.incrementCtor(InstantiationType.Reader);
    }
  }

  public static class WriterMessageCountingStateStoreActor extends MessageCountingStateStoreActor {
    public WriterMessageCountingStateStoreActor(MessageCountingResults results, final int totalPartitions) {
      super(results, totalPartitions);

      results.incrementCtor(InstantiationType.Writer);
    }
  }

  public static class MessageCountingInstantiatorProvider implements InstantiatorProvider {
    private final MessageCountingResults results;

    public MessageCountingInstantiatorProvider(final MessageCountingResults results) {
      this.results = results;
    }

    @Override
    public <A extends Actor> ActorInstantiator<A> instantiator(
            final InstantiationType type,
            final int currentPartition,
            final int totalPartitions) {

      return new MessageCountingInstantiator<>(results, type, totalPartitions);
    }
  }

  public static class MessageCountingInstantiator<A extends Actor> implements ActorInstantiator<A> {
    private static final long serialVersionUID = 1L;

    private final MessageCountingResults results;
    private final InstantiationType type;
    private final int totalPartitions;

    public MessageCountingInstantiator(final MessageCountingResults results, final InstantiationType type, final int totalPartitions) {
      this.results = results;
      this.type = type;
      this.totalPartitions = totalPartitions;
    }

    @Override
    @SuppressWarnings("unchecked")
    public A instantiate() {
      if (type.isReader()) {
        return (A) new ReaderMessageCountingStateStoreActor(results, totalPartitions);
      } else if (type.isWriter()) {
        return (A) new WriterMessageCountingStateStoreActor(results, totalPartitions);
      }
      throw new IllegalStateException("Unknown PartitioningStateStore type.");
    }
  }

  public static class MessageCountingResults {
    private final AccessSafely access;

    private final AtomicInteger ctor = new AtomicInteger(0);
    private final AtomicInteger readerCtor = new AtomicInteger(0);
    private final AtomicInteger writerCtor = new AtomicInteger(0);
    private final AtomicInteger entryReader = new AtomicInteger(0);
    private final AtomicInteger read = new AtomicInteger(0);
    private final Map<Integer, Integer> readPartitions = new ConcurrentHashMap<>();
    private final AtomicInteger readAll = new AtomicInteger(0);
    private final AtomicInteger streamAllOf = new AtomicInteger(0);
    private final AtomicInteger streamSomeUsing = new AtomicInteger(0);
    private final AtomicInteger write = new AtomicInteger(0);
    private final Map<Integer, Integer> writePartitions = new ConcurrentHashMap<>();

    public MessageCountingResults(final int times) {
      this.access = AccessSafely.afterCompleting(times);

      access.writingWith("ctor", (InstantiationType type) -> {
        ctor.incrementAndGet();
        if (type.isReader()) {
          readerCtor.incrementAndGet();
        } else if (type.isWriter()) {
          writerCtor.incrementAndGet();
        }
      });
      access.readingWith("ctor", () -> ctor.get());
      access.readingWith("readerCtor", () -> readerCtor.get());
      access.readingWith("writerCtor", () -> writerCtor.get());

      access.writingWith("read", (String id, Integer totalPartitions) -> {
        read.incrementAndGet();
        final int partition = PartitioningStateStore.partitionOf(id, totalPartitions);
        final Integer count = readPartitions.get(partition);
        readPartitions.put(partition, count == null ? 1 : count + 1);
      });
      access.readingWith("read", () -> read.get());

      access.writingWith("readAll", one -> readAll.incrementAndGet());
      access.readingWith("readAll", () -> readAll.get());

      access.writingWith("streamAllOf", one -> streamAllOf.incrementAndGet());
      access.readingWith("streamAllOf", () -> streamAllOf.get());

      access.writingWith("streamSomeUsing", one -> streamSomeUsing.incrementAndGet());
      access.readingWith("streamSomeUsing", () -> streamSomeUsing.get());

      access.writingWith("write", (String id, Integer totalPartitions) -> {
        write.incrementAndGet();
        final int partition = PartitioningStateStore.partitionOf(id, totalPartitions);
        final Integer count = writePartitions.get(partition);
        writePartitions.put(partition, count == null ? 1 : count + 1);
      });
      access.readingWith("write", () -> write.get());

      access.writingWith("entryReader", one -> entryReader.incrementAndGet());
      access.readingWith("entryReader", () -> entryReader.get());
    }

    public int ctor() {
      return access.readFrom("ctor");
    }

    public int readerCtor() {
      return access.readFrom("readerCtor");
    }

    public int writerCtor() {
      return access.readFrom("writerCtor");
    }

    public int read() {
      return access.readFrom("read");
    }

    public int readPartitionCount(final int partition) {
      access.readFrom("read");
      return readPartitions.get(partition);
    }

    public int readAll() {
      return access.readFrom("readAll");
    }

    public int streamAllOf() {
      return access.readFrom("streamAllOf");
    }

    public int streamSomeUsing() {
      return access.readFrom("streamSomeUsing");
    }

    public int write() {
      return access.readFrom("write");
    }

    public int writePartitionCount(final int partition) {
      access.readFrom("read");
      return writePartitions.get(partition);
    }

    public int entryReader() {
      return access.readFrom("entryReader");
    }

    private void incrementCtor(final InstantiationType type) {
      access.writeUsing("ctor", type);
    }

    private void incrementRead(final String id, final int totalPartitions) {
      access.writeUsing("read", id, totalPartitions);
    }

    private void incrementReadAll() {
      access.writeUsing("readAll", 1);
    }

    private void incrementStreamAllOf() {
      access.writeUsing("streamAllOf", 1);
    }

    private void incrementStreamSomeUsing() {
      access.writeUsing("streamSomeUsing", 1);
    }

    private void incrementWrite(final String id, final int totalPartitions) {
      access.writeUsing("write", id, totalPartitions);
    }

    private void incrementEntryReader() {
      access.writeUsing("entryReader", 1);
    }
  }
}
