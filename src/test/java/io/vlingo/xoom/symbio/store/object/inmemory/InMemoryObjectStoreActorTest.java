// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.object.inmemory;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.symbio.BaseEntry.ObjectEntry;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.EntryAdapter;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.MapQueryExpression;
import io.vlingo.xoom.symbio.store.QueryExpression;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.MockConfirmDispatchedResultInterest;
import io.vlingo.xoom.symbio.store.dispatch.MockDispatcher;
import io.vlingo.xoom.symbio.store.object.ObjectStore;
import io.vlingo.xoom.symbio.store.object.StateSources;

public class InMemoryObjectStoreActorTest {
  private MockPersistResultInterest persistInterest;
  private MockQueryResultInterest queryResultInterest;
  private ObjectStore objectStore;
  private World world;
  private MockDispatcher<Entry<?>, State<?>> dispatcher;

  @Test
  public void testThatObjectPersistsQueries() {
    dispatcher.afterCompleting(1);
    final AccessSafely persistAccess = persistInterest.afterCompleting(1);
    final Person person = new Person("Tom Jones", 85);
    final Test1Source source = new Test1Source();
    objectStore.persist(StateSources.of(person, source), persistInterest);
    final int persistSize = persistAccess.readFrom("size");
    assertEquals(1, persistSize);
    assertEquals(person, persistAccess.readFrom("object", 0));

    final QueryExpression query = MapQueryExpression
            .using(Person.class, "find", MapQueryExpression.map("id", "" + person.persistenceId()));

    final AccessSafely queryAccess = queryResultInterest.afterCompleting(1);
    objectStore.queryObject(query, queryResultInterest, null);
    final int querySize = queryAccess.readFrom("size");
    assertEquals(1, querySize);
    assertEquals(person, queryAccess.readFrom("object", 0));

    assertEquals(1, dispatcher.dispatchedCount());
    final Dispatchable<Entry<?>, State<?>> dispatched = dispatcher.getDispatched().get(0);
    validateDispatchedState(person, dispatched);

    final List<Entry<?>> dispatchedEntries = dispatched.entries();
    Assert.assertEquals(1, dispatchedEntries.size());
    final Entry<?> entry = dispatchedEntries.get(0);
    Assert.assertNotNull(entry.id());
    Assert.assertEquals(source.getClass().getName(), entry.typeName());
    Assert.assertEquals(Metadata.nullMetadata(), entry.metadata());
  }

  @Test
  public void testThatMultiPersistQueryResolves() {
    dispatcher.afterCompleting(3);
    final AccessSafely persistAllAccess = persistInterest.afterCompleting(1);

    final Person person1 = new Person("Tom Jones", 78);
    final Person person2 = new Person("Dean Martin", 78);
    final Person person3 = new Person("Sally Struthers", 71);
    objectStore.persistAll(Arrays.asList(StateSources.of(person1), StateSources.of(person2), StateSources.of(person3)), persistInterest);
    final int persistSize = persistAllAccess.readFrom("size");
    assertEquals(3, persistSize);

    final AccessSafely queryAllAccess = queryResultInterest.afterCompleting(1);
    objectStore.queryAll(QueryExpression.using(Person.class, "findAll"), queryResultInterest, null);
    final int querySize = queryAllAccess.readFrom("size");
    assertEquals(3, querySize);
    assertEquals(person1, queryAllAccess.readFrom("object", 0));
    assertEquals(person2, queryAllAccess.readFrom("object", 1));
    assertEquals(person3, queryAllAccess.readFrom("object", 2));

    assertEquals(3, dispatcher.dispatchedCount());

    Dispatchable<Entry<?>, State<?>> dispatched = dispatcher.getDispatched().get(0);
    validateDispatchedState(person1, dispatched);
    List<Entry<?>> dispatchedEntries = dispatched.entries();
    Assert.assertTrue(dispatchedEntries.isEmpty());

    dispatched = dispatcher.getDispatched().get(1);
    validateDispatchedState(person2, dispatched);
    dispatchedEntries = dispatched.entries();
    Assert.assertTrue(dispatchedEntries.isEmpty());

    dispatched = dispatcher.getDispatched().get(2);
    validateDispatchedState(person3, dispatched);
    dispatchedEntries = dispatched.entries();
    Assert.assertTrue(dispatchedEntries.isEmpty());
  }

  private void validateDispatchedState(Person persistedObject, Dispatchable<Entry<?>, State<?>> dispatched) {
    Assert.assertNotNull(dispatched);
    Assert.assertNotNull(dispatched.createdOn());
    Assert.assertNotNull(dispatched.id());

    Assert.assertTrue(dispatched.state().isPresent());
    final State<?> state = dispatched.typedState();
    Assert.assertEquals(String.valueOf(persistedObject.persistenceId()), state.id);
    Assert.assertEquals(persistedObject.getClass().getName(), state.type);
    Assert.assertEquals(Metadata.nullMetadata(), state.metadata);
  }

  @Before
  public void setUp() {
    persistInterest = new MockPersistResultInterest();
    queryResultInterest = new MockQueryResultInterest();
    world = World.startWithDefaults("test-object-store");
    final EntryAdapterProvider entryAdapterProvider = new EntryAdapterProvider(world);
    entryAdapterProvider.registerAdapter(Test1Source.class, new Test1SourceAdapter());

    this.dispatcher = new MockDispatcher<>(new MockConfirmDispatchedResultInterest());
    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class, Arrays.asList(this.dispatcher));
  }

  public static final class Test1Source extends Source<String> {
    private final int one = 1;

    public int one() {
      return one;
    }
  }

  private static final class Test1SourceAdapter implements EntryAdapter<Test1Source, ObjectEntry<Test1Source>> {
    @Override
    public Test1Source fromEntry(final ObjectEntry<Test1Source> entry) {
      return (Test1Source) entry.entryData();
    }

    @Override
    public ObjectEntry<Test1Source> toEntry(Test1Source source, Metadata metadata) {
      return new ObjectEntry<>(Test1Source.class, 1, source, metadata);
    }

    @Override
    public ObjectEntry<Test1Source> toEntry(Test1Source source, String id, Metadata metadata) {
      return new ObjectEntry<>(id, Test1Source.class, 1, source, metadata);
    }

    @Override
    public ObjectEntry<Test1Source> toEntry(final Test1Source source, final int version, final String id, final Metadata metadata) {
      final String serialization = JsonSerialization.serialized(source);
      return new ObjectEntry<>(id, Test1Source.class, 1, serialization, version, metadata);
    }
  }
}
