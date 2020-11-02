// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.Configuration;
import io.vlingo.actors.World;
import io.vlingo.actors.plugin.PluginProperties;
import io.vlingo.actors.plugin.mailbox.concurrentqueue.ConcurrentQueueMailboxPlugin.ConcurrentQueueMailboxPluginConfiguration;
import io.vlingo.symbio.store.state.MessageCountingStateStoreActor.MessageCountingInstantiatorProvider;
import io.vlingo.symbio.store.state.MessageCountingStateStoreActor.MessageCountingResults;
import io.vlingo.symbio.store.state.StateStore.TypedStateBundle;

public class PartitioningStateStoreTest {
  private World world;

  @Test
  public void testThatMinimumStoresCreated() {
    final int readers = 3;
    final int writers = 2;
    final int times = PartitioningStateStore.MinimumReaders + PartitioningStateStore.MinimumWriters;

    final MessageCountingResults results = new MessageCountingResults(times);
    final MessageCountingInstantiatorProvider instantiatorProvider = new MessageCountingInstantiatorProvider(results, true); // use Definition

    // 3, 2 must be minimum MinimumReaders, MinimumWriters and must default to that if below minimum is given
    final StateStore store = PartitioningStateStore.using(world.stage(), MessageCountingStateStoreActor.class, instantiatorProvider, readers, writers);

    Assert.assertNotNull(store);
    Assert.assertEquals(times, results.ctor());
    Assert.assertNotEquals(readers, results.readerCtor());
    Assert.assertEquals(PartitioningStateStore.MinimumReaders, results.readerCtor());
    Assert.assertNotEquals(writers, results.writerCtor());
    Assert.assertEquals(PartitioningStateStore.MinimumWriters, results.writerCtor());
  }

  @Test
  public void testThatMaximumStoresCreated() {
    final int readers = Integer.MAX_VALUE / 2;
    final int writers = Integer.MAX_VALUE / 10;
    final int times = PartitioningStateStore.MaximumReaders + PartitioningStateStore.MaximumWriters;

    final MessageCountingResults results = new MessageCountingResults(times);
    final MessageCountingInstantiatorProvider instantiatorProvider = new MessageCountingInstantiatorProvider(results, false); // don't use Definition

    // 3, 2 must be minimum 5, 3 and must default to that below minimum given
    final StateStore store = PartitioningStateStore.using(world.stage(), MessageCountingStateStoreActor.class, instantiatorProvider, readers, writers);

    Assert.assertNotNull(store);
    Assert.assertEquals(times, results.ctor());
    Assert.assertNotEquals(readers, results.readerCtor());
    Assert.assertEquals(PartitioningStateStore.MaximumReaders, results.readerCtor());
    Assert.assertNotEquals(writers, results.writerCtor());
    Assert.assertEquals(PartitioningStateStore.MaximumWriters, results.writerCtor());
  }

  @Test
  public void testThatExactStoresCreated() {
    final int readers = 10;
    final int writers = 7;
    final int times = readers + writers;

    final MessageCountingResults results = new MessageCountingResults(times);
    final MessageCountingInstantiatorProvider instantiatorProvider = new MessageCountingInstantiatorProvider(results, false); // don't use Definition

    // must be exact
    final StateStore store = PartitioningStateStore.using(world.stage(), MessageCountingStateStoreActor.class, instantiatorProvider, readers, writers);

    Assert.assertNotNull(store);
    Assert.assertEquals(times, results.ctor());
    Assert.assertEquals(readers, results.readerCtor());
    Assert.assertEquals(writers, results.writerCtor());
  }

  @Test
  public void testThatStoreCreationFailsFromWrongMailboxType() {

  }

  @Test
  public void testThatReadersReceive() {
    final int ctors = PartitioningStateStore.MinimumReaders + PartitioningStateStore.MinimumWriters;
    final int interations = 5;
    final int partitionReads = 2;
    final int reads = interations * partitionReads;
    final int readAlls = 1;

    final int times = ctors + reads + readAlls;

    final MessageCountingResults results = new MessageCountingResults(times);
    final MessageCountingInstantiatorProvider instantiatorProvider = new MessageCountingInstantiatorProvider(results, true); // use Definition

    final StateStore store = PartitioningStateStore.using(world.stage(), MessageCountingStateStoreActor.class, instantiatorProvider, 0, 0);

    Assert.assertNotNull(store);

    for (int read = 0; read < interations; ++read) {
      store.read(idFor(read, PartitioningStateStore.MinimumReaders), getClass(), null);
      store.read(idFor(read, PartitioningStateStore.MinimumReaders), getClass(), null, null);
    }

    store.readAll(Arrays.asList(new TypedStateBundle("3", getClass()), new TypedStateBundle("4", getClass())), null, null);

    Assert.assertEquals(ctors, results.ctor());
    Assert.assertEquals(PartitioningStateStore.MinimumReaders, results.readerCtor());
    Assert.assertEquals(PartitioningStateStore.MinimumWriters, results.writerCtor());

    Assert.assertEquals(reads, results.read());
    Assert.assertEquals(readAlls, results.readAll());

    Assert.assertEquals(partitionReads, results.readPartitionCount(0));
    Assert.assertEquals(partitionReads, results.readPartitionCount(1));
    Assert.assertEquals(partitionReads, results.readPartitionCount(2));
    Assert.assertEquals(partitionReads, results.readPartitionCount(3));
    Assert.assertEquals(partitionReads, results.readPartitionCount(4));
  }

  @Test
  public void testThatWritersReceive() {
    final int iterations = 3;

    final int ctors = PartitioningStateStore.MinimumReaders + PartitioningStateStore.MinimumWriters;
    final int writes = 3;

    final int times = ctors + (writes * iterations);

    final MessageCountingResults results = new MessageCountingResults(times);
    final MessageCountingInstantiatorProvider instantiatorProvider = new MessageCountingInstantiatorProvider(results, false); // don't use Definition

    final StateStore store = PartitioningStateStore.using(world.stage(), MessageCountingStateStoreActor.class, instantiatorProvider, 0, 0);

    Assert.assertNotNull(store);

    for (int outer = 0; outer < iterations; ++outer) {
      for (int inner = 0; inner < writes; ++inner) {
        store.write(idFor(inner, PartitioningStateStore.MinimumWriters), this, 1, null);
      }
    }

    Assert.assertEquals(ctors, results.ctor());
    Assert.assertEquals(PartitioningStateStore.MinimumReaders, results.readerCtor());
    Assert.assertEquals(PartitioningStateStore.MinimumWriters, results.writerCtor());

    Assert.assertEquals(iterations * writes, results.write());

    Assert.assertEquals(writes, results.writePartitionCount(0));
    Assert.assertEquals(writes, results.writePartitionCount(1));
    Assert.assertEquals(writes, results.writePartitionCount(2));
  }

  @Before
  public void setUp() {
//    world = World.startWithDefaults("test-partitioning-statestore");

    final Configuration configuration = Configuration.define();

    JDBCConcurrentQueueMailboxPluginConfiguration.register(configuration);

    world = World.start("test-partitioning-statestore", configuration);
  }

  private static class JDBCConcurrentQueueMailboxPluginConfiguration extends ConcurrentQueueMailboxPluginConfiguration {
    public static JDBCConcurrentQueueMailboxPluginConfiguration register(final Configuration configuration) {
      return new JDBCConcurrentQueueMailboxPluginConfiguration(configuration);
    }

    JDBCConcurrentQueueMailboxPluginConfiguration(final Configuration configuration) {
      final String name = "jdbcQueueMailbox";

      final Properties properties = new Properties();

      properties.setProperty("plugin." + name + ".defaultMailbox", "false");

      this.buildWith(configuration, new PluginProperties(name, properties));

      configuration.with(this);
    }
  }

  private String idFor(final int targetPartition, final int max) {
    while (true) {
      final String id = UUID.randomUUID().toString();
      final int hashCode = id.hashCode();
      final int partition = hashCode % max;

      if (partition == targetPartition) {
        return id;
      }
    }
  }
}
