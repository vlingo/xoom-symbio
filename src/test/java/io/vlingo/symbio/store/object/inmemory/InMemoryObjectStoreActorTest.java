// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.object.inmemory;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.symbio.BaseEntry.ObjectEntry;
import io.vlingo.symbio.EntryAdapter;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.QueryExpression;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class InMemoryObjectStoreActorTest {
  private MockPersistResultInterest persistInterest;
  private MockQueryResultInterest queryResultInterest;
  private ObjectStore objectStore;
  private World world;

  @Test
  public void testThatObjectPersistsQuerys() {
    final AccessSafely persistAccess = persistInterest.afterCompleting(1);
    final Person person = new Person("Tom Jones", 85);
    objectStore.persist(person, Arrays.asList(new Test1Source()), persistInterest);
    final int persistSize = persistAccess.readFrom("size");
    assertEquals(1, persistSize);
    assertEquals(person, persistAccess.readFrom("object", 0));

    final QueryExpression query =
            MapQueryExpression.using(
                    Person.class,
                    "find",
                    MapQueryExpression.map("id", "" + person.persistenceId()));

    final AccessSafely queryAccess = queryResultInterest.afterCompleting(1);
    objectStore.queryObject(query, queryResultInterest, null);
    final int querySize = queryAccess.readFrom("size");
    assertEquals(1, querySize);
    assertEquals(person, queryAccess.readFrom("object", 0));
  }

  @Test
  public void testThatMultiPersistQueryResolves() {
    final AccessSafely persistAllAccess = persistInterest.afterCompleting(1);
    final Person person1 = new Person("Tom Jones", 78);
    final Person person2 = new Person("Dean Martin", 78);
    final Person person3 = new Person("Sally Struthers", 71);
    objectStore.persistAll(Arrays.asList(person1, person2, person3), persistInterest);
    final int persistSize = persistAllAccess.readFrom("size");
    assertEquals(3, persistSize);

    final AccessSafely queryAllAccess = queryResultInterest.afterCompleting(1);
    objectStore.queryAll(QueryExpression.using(Person.class, "findAll"), queryResultInterest, null);
    final int querySize = queryAllAccess.readFrom("size");
    assertEquals(3, querySize);
    assertEquals(person1, queryAllAccess.readFrom("object", 0));
    assertEquals(person2, queryAllAccess.readFrom("object", 1));
    assertEquals(person3, queryAllAccess.readFrom("object", 2));
  }

  @Before
  public void setUp() {
    persistInterest = new MockPersistResultInterest();
    queryResultInterest = new MockQueryResultInterest();
    world = World.startWithDefaults("test-object-store");
    final EntryAdapterProvider entryAdapterProvider = new EntryAdapterProvider(world);
    entryAdapterProvider.registerAdapter(Test1Source.class, new Test1SourceAdapter());
    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class);
  }

  public static final class Test1Source extends Source<String> {
    private final int one = 1;
    public int one() { return one; }
  }
  
  private static final class Test1SourceAdapter implements EntryAdapter<Test1Source,ObjectEntry<Test1Source>> {
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
  }
}
