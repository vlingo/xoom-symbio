// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.Collection;
import java.util.List;

import io.vlingo.actors.Actor;
import io.vlingo.actors.ActorInstantiator;
import io.vlingo.actors.Environment;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.common.Tuple2;
import io.vlingo.reactivestreams.Stream;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.QueryExpression;

/**
 * Provides a partitioning {@code StateStore}. All reads and writes are from/to the same storage and tables.
 * The partitioning based on id hashing, which is meant to read and write in parallel using more than one
 * connection. For reader operations, such as queries and streaming, the partitioning is based on smallest mailbox.
 * <p>
 * WARNING: When utilizing the smallest mailbox (least busy reader) operations, it is important not request
 * additional operations of the same type
 */
public class PartitioningStateStore implements StateStore {
  public static int MinimumReaders = 5;
  public static int MaximumReaders = 128;

  public static int MinimumWriters = 3;
  public static int MaxWriters = 128;

  private Tuple2<StateStore, Actor>[] readers;
  private Tuple2<StateStore, Actor>[] writers;

  /**
   * Answer a new {@code PartitioningStateStore} as a {@code StateStore} with
   * {@code totalReaders} and {@code totalWriters}. Each actor is created by
   * means of the given {@code instantiator}, which must be renewable on each
   * use of {@code instantate()} if that is necessary for the type of
   * {@code ActorInstantiator<A>} in use.
   * @param <A> the concrete Actor type of each StateStore actor
   * @param stage the Stage within which the StateStore actors are created
   * @param stateStoreActorType the {@code Class<A>} of the Actor that implements StateStore
   * @param instantiator the {@code ActorInstantiator<A>} that is used for every reader and writer actor, and thus must be renewed on each use if needed
   * @param totalReaders the int total number of readers, which may be between {@code MinimumReaders} and {@code MaximumReaders}
   * @param totalWriters the int total number of writers, which may be between {@code MinimumWriters} and {@code MaxWriters}
   * @return StateStore
   */
  public static <A extends Actor> StateStore stateStoreUsing(
          final Stage stage,
          final Class<A> stateStoreActorType,
          final ActorInstantiator<A> instantiator,
          final int totalReaders,
          final int totalWriters) {

    return new PartitioningStateStore(stage, stateStoreActorType, instantiator, totalReaders, totalWriters);
  }

  /**
   * @see io.vlingo.symbio.store.state.StateStoreReader#read(java.lang.String, java.lang.Class, io.vlingo.symbio.store.state.StateStore.ReadResultInterest, java.lang.Object)
   */
  @Override
  public void read(final String id, final Class<?> type, final ReadResultInterest interest, final Object object) {
    readerOf(id).read(id, type, interest, object);
  }

  /**
   * @see io.vlingo.symbio.store.state.StateStoreReader#readAll(java.util.Collection, io.vlingo.symbio.store.state.StateStore.ReadResultInterest, java.lang.Object)
   */
  @Override
  public void readAll(final Collection<TypedStateBundle> bundles, final ReadResultInterest interest, final Object object) {
    leastBusyReader().readAll(bundles, interest, object);
  }

  /**
   * @see io.vlingo.symbio.store.state.StateStoreReader#streamAllOf(java.lang.Class)
   */
  @Override
  public Completes<Stream> streamAllOf(final Class<?> stateType) {
    return leastBusyReader().streamAllOf(stateType);
  }

  /**
   * @see io.vlingo.symbio.store.state.StateStoreReader#streamSomeUsing(io.vlingo.symbio.store.QueryExpression)
   */
  @Override
  public Completes<Stream> streamSomeUsing(final QueryExpression query) {
    return leastBusyReader().streamSomeUsing(query);
  }

  /**
   * @see io.vlingo.symbio.store.state.StateStoreWriter#write(java.lang.String, java.lang.Object, int, java.util.List, io.vlingo.symbio.Metadata, io.vlingo.symbio.store.state.StateStore.WriteResultInterest, java.lang.Object)
   */
  @Override
  public <S, C> void write(final String id, final S state, final int stateVersion, final List<Source<C>> sources, final Metadata metadata, final WriteResultInterest interest, final Object object) {
    writerOf(id).write(id, state, stateVersion, sources, metadata, interest, object);
  }

  /*
   * @see io.vlingo.symbio.store.state.StateStore#entryReader(java.lang.String)
   */
  @Override
  public <ET extends Entry<?>> Completes<StateStoreEntryReader<ET>> entryReader(final String name) {
    return readerOf(name).entryReader(name);
  }

  private <A extends Actor> PartitioningStateStore(
          final Stage stage,
          final Class<A> stateStoreActorType,
          final ActorInstantiator<A> instantiator,
          final int totalReaders,
          final int totalWriters) {

    this.readers = createStateStores(stage, stateStoreActorType, instantiator, actualTotal(totalReaders, MinimumReaders, MaximumReaders));

    this.writers = createStateStores(stage, stateStoreActorType, instantiator, actualTotal(totalWriters, MinimumWriters, MaxWriters));
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
          final ActorInstantiator<A> instantiator,
          final int total) {

    final Tuple2<StateStore, Actor>[] stateStores = new Tuple2[total];

    final HookInstantiator<A> hook = new HookInstantiator<>(instantiator);

    for (int idx = 0; idx < total; ++idx) {
      final StateStore stateStore = stage.actorFor(StateStore.class, stateStoreActorType, hook);
      stateStores[idx] = Tuple2.from(stateStore, hook.actor);
    }

    return stateStores;
  }

  private StateStore leastBusyReader() {
    int totalMessages = Integer.MAX_VALUE;
    StateStore reader = null;

    for (int idx = 0; idx < readers.length; ++idx) {
      final int pending = Environment.environmentOf(readers[idx]._2).pendingMessages();

      if (pending < totalMessages) {
        totalMessages = pending;
        reader = readers[idx]._1;
      }
    }

    return reader;
  }

  private StateStore readerOf(final String identity) {
    return readers[identity.hashCode() / readers.length]._1;
  }

  private StateStore writerOf(final String identity) {
    return writers[identity.hashCode() / readers.length]._1;
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
