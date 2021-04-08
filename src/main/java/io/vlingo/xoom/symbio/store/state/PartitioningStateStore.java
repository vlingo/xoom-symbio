// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.actors.ActorInstantiator;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Environment;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Tuple2;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.QueryExpression;

/**
 * Provides a partitioning {@code StateStore}. All reads and writes are from/to the same storage and tables.
 * The partitioning is based on id hashing, which is meant to read and write in parallel using more than one
 * connection. For reader operations not subject to id, such as queries and streaming, the partitioning is
 * based on smallest mailbox.
 * <p>
 * WARNING: (1) When utilizing the smallest mailbox (least busy reader) operations, it is important to not request
 * additional operations of the same type if the earlier query must complete before the subsequent request.
 * (2) The underlying {@code Actor} must use a {@code Mailbox} that supports {@code int pendingMessages()}. Otherwise,
 * the smallest mailbox (least busy reader) operations cannot be supported, and it does not make much sense to use
 * this a partitioning {@code StateStore}.
 */
public class PartitioningStateStore implements StateStore {

  /**
   * Set on the {@code ActorInstantiator<A>} prior to {@code instantiate()}.
   */
  public static enum StateStoreRole {
    Reader,
    Writer;

    public boolean isReader() {
      return this == Reader;
    }

    public boolean isWriter() {
      return this == Writer;
    }
  };

  /**
   * A provider of {@code ActorInstantiator<A>} instances, one for each read
   * partition and one for each write partition.
   */
  public static interface InstantiatorProvider {
    /**
     * Answer an {@code Optional<Definition>} that, if defined, will be used to pass to the {@code Stage.actorOf(protocolClass, definition)}.
     * <p>NOTE: This {@code definitionFor()} is sent following the creation of {@code instantiator} by {@code instantiatorFor()} and includes that {@code instantiator}.
     * @param stateStoreActorType the {@code Class<A>} of the concrete StateStore protocol implementing Actor type
     * @param instantiator the {@code ActorInstantiator<A>} instance that must be included in the Definition should one be provided
     * @param role the StateStoreRole of the {@code ActorInstantiator<A>}
     * @param currentPartition the int index of the current partition instantiator, 0 to totalPartitions - 1
     * @param totalPartitions the int total number of partitions
     * @param <A> the concrete type of Actor
     * @return {@code Optional<Definition>}
     */
    <A extends Actor> Optional<Definition> definitionFor(final Class<A> stateStoreActorType, final ActorInstantiator<A> instantiator, final StateStoreRole role, final int currentPartition, final int totalPartitions);

    /**
     * Answer a new instance of an {@code ActorInstantiator<A>} for the {@code type}
     * and {@code currentPartition}.
     * @param stateStoreActorType the {@code Class<A>} of the concrete StateStore protocol implementing Actor type
     * @param role the StateStoreRole of the instance to come from the resulting {@code ActorInstantiator<A>}
     * @param currentPartition the int index of the current partition to be instantiated, 0 to totalPartitions - 1
     * @param totalPartitions the int total number of partitions
     * @param <A> the concrete type of Actor
     * @return {@code ActorInstantiator<A>}
     */
    <A extends Actor> ActorInstantiator<A> instantiatorFor(final Class<A> stateStoreActorType, final StateStoreRole role, final int currentPartition, final int totalPartitions);
  }

  public static int MinimumReaders = 5;
  public static int MaximumReaders = 256;

  public static int MinimumWriters = 3;
  public static int MaximumWriters = 256;

  private final InstantiatorProvider instantiatorProvider;
  private Tuple2<StateStore, Actor>[] readers;
  private Tuple2<StateStore, Actor>[] writers;

  /**
   * Answer the partition of {@code identity} when there are {@code totalPartitions}.
   * @param identity the String identity
   * @param totalPartitions the int number of partitions
   * @return int
   */
  public static int partitionOf(final String identity, final int totalPartitions) {
    return Math.abs(identity.hashCode() % totalPartitions);
  }

  /**
   * Answer a new {@code PartitioningStateStore} as a {@code StateStore} with
   * {@code totalReaders} and {@code totalWriters}. Each actor is created by
   * means of the given {@code instantiator}, which must be renewable on each
   * use of {@code instantiate()} if that is necessary for the type of
   * {@code ActorInstantiator<A>} in use.
   * @param <A> the concrete Actor type of each StateStore actor
   * @param stage the Stage within which the StateStore actors are created
   * @param stateStoreActorType the {@code Class<A>} of the Actor that implements StateStore
   * @param instantiatorProvider the {@code InstantiatorProvider} that is used to get an {@code ActorInstantiator<A>} for every reader and writer actor
   * @param totalReaders the int total number of readers, which may be between {@code MinimumReaders} and {@code MaximumReaders}
   * @param totalWriters the int total number of writers, which may be between {@code MinimumWriters} and {@code MaxWriters}
   * @return StateStore
   */
  public static <A extends Actor> StateStore using(
          final Stage stage,
          final Class<A> stateStoreActorType,
          final InstantiatorProvider instantiatorProvider,
          final int totalReaders,
          final int totalWriters) {

    return new PartitioningStateStore(stage, stateStoreActorType, instantiatorProvider, totalReaders, totalWriters);
  }

  /**
   * @see io.vlingo.xoom.symbio.store.state.StateStoreReader#read(java.lang.String, java.lang.Class, io.vlingo.xoom.symbio.store.state.StateStore.ReadResultInterest, java.lang.Object)
   */
  @Override
  public void read(final String id, final Class<?> type, final ReadResultInterest interest, final Object object) {
    readerOf(id).read(id, type, interest, object);
  }

  /**
   * @see io.vlingo.xoom.symbio.store.state.StateStoreReader#readAll(java.util.Collection, io.vlingo.xoom.symbio.store.state.StateStore.ReadResultInterest, java.lang.Object)
   */
  @Override
  public void readAll(final Collection<TypedStateBundle> bundles, final ReadResultInterest interest, final Object object) {
    leastBusyReader().readAll(bundles, interest, object);
  }

  /**
   * @see io.vlingo.xoom.symbio.store.state.StateStoreReader#streamAllOf(java.lang.Class)
   */
  @Override
  public Completes<Stream> streamAllOf(final Class<?> stateType) {
    return leastBusyReader().streamAllOf(stateType);
  }

  /**
   * @see io.vlingo.xoom.symbio.store.state.StateStoreReader#streamSomeUsing(io.vlingo.xoom.symbio.store.QueryExpression)
   */
  @Override
  public Completes<Stream> streamSomeUsing(final QueryExpression query) {
    return leastBusyReader().streamSomeUsing(query);
  }

  /**
   * @see io.vlingo.xoom.symbio.store.state.StateStoreWriter#write(java.lang.String, java.lang.Object, int, java.util.List, io.vlingo.xoom.symbio.Metadata, io.vlingo.xoom.symbio.store.state.StateStore.WriteResultInterest, java.lang.Object)
   */
  @Override
  public <S, C> void write(final String id, final S state, final int stateVersion, final List<Source<C>> sources, final Metadata metadata, final WriteResultInterest interest, final Object object) {
    writerOf(id).write(id, state, stateVersion, sources, metadata, interest, object);
  }

  /*
   * @see io.vlingo.xoom.symbio.store.state.StateStore#entryReader(java.lang.String)
   */
  @Override
  public <ET extends Entry<?>> Completes<StateStoreEntryReader<ET>> entryReader(final String name) {
    return readerOf(name).entryReader(name);
  }

  private <A extends Actor> PartitioningStateStore(
          final Stage stage,
          final Class<A> stateStoreActorType,
          final InstantiatorProvider instantiatorProvider,
          final int totalReaders,
          final int totalWriters) {

    this.instantiatorProvider = instantiatorProvider;

    this.readers = createStateStores(stage, stateStoreActorType, StateStoreRole.Reader, actualTotal(totalReaders, MinimumReaders, MaximumReaders));

    this.writers = createStateStores(stage, stateStoreActorType, StateStoreRole.Writer, actualTotal(totalWriters, MinimumWriters, MaximumWriters));
  }

  private int actualTotal(final int total, final int minimum, final int maximum) {
    if (total < minimum) return minimum;

    if (total > maximum) return maximum;

    return total;
  }

  @SuppressWarnings("unchecked")
  private <A extends Actor> Tuple2<StateStore, Actor>[] createStateStores(
          final Stage stage,
          final Class<A> stateStoreActorType,
          final StateStoreRole type,
          final int total) {

    final Tuple2<StateStore, Actor>[] stateStores = new Tuple2[total];

    for (int idx = 0; idx < total; ++idx) {
      final ActorInstantiator<A> instantiator = instantiatorProvider.instantiatorFor(stateStoreActorType, type, idx, total);
      final HookInstantiator<A> hook = new HookInstantiator<>(instantiator);
      final Optional<Definition> definition = instantiatorProvider.definitionFor(stateStoreActorType, hook, type, idx, total);
      final StateStore stateStore = stage.actorFor(StateStore.class, definition.orElse(Definition.has(stateStoreActorType, hook)));
      pending(hook.actor); // assertion that Mailbox supports pendingMessages()
      stateStores[idx] = Tuple2.from(stateStore, hook.actor);
    }

    return stateStores;
  }

  private StateStore leastBusyReader() {
    int totalMessages = Integer.MAX_VALUE;
    StateStore reader = null;

    for (int idx = 0; idx < readers.length; ++idx) {
      final int pending = pending(readers[idx]._2);

      if (pending < totalMessages) {
        totalMessages = pending;
        reader = readers[idx]._1;
      }
    }

    return reader;
  }

  private int pending(final Actor actor) {
    return Environment.of(actor).pendingMessages();
  }

  private StateStore readerOf(final String identity) {
    final int index = partitionOf(identity, readers.length);
    return readers[index]._1;
  }

  private StateStore writerOf(final String identity) {
    final int index = partitionOf(identity, writers.length);
    return writers[index]._1;
  }

  private class HookInstantiator<A extends Actor> implements ActorInstantiator<A> {
    private static final long serialVersionUID = 1L;

    private A actor;
    private final ActorInstantiator<A> instantiator;

    @Override
    public A instantiate() {
      actor = instantiator.instantiate();
      return actor;
    }

    private HookInstantiator(final ActorInstantiator<A> instantiator) {
      this.instantiator = instantiator;
    }
  }
}
