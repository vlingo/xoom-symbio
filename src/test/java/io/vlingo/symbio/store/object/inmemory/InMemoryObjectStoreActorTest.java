// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.object.inmemory;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.QueryExpression;

public class InMemoryObjectStoreActorTest {
  private MockPersistResultInterest persistInterest;
  private MockQueryResultInterest queryResultInterest;
  private ObjectStore objectStore;
  private World world;

  @Test
  public void testThatObjectPersistsQuerys() {
    final TestUntil untilPersist = persistInterest.untilHappenings(1);
    final Person person = new Person("Tom Jones", 85);
    objectStore.persist(person, persistInterest);
    untilPersist.completes();
    assertEquals(1, persistInterest.size());
    assertEquals(person, persistInterest.persistentObject(0));

    final QueryExpression query =
            MapQueryExpression.using(
                    Person.class,
                    "find",
                    MapQueryExpression.map("id", "" + person.persistenceId()));

    final TestUntil untilQuery = queryResultInterest.untilHappenings(1);
    objectStore.queryObject(query, queryResultInterest, untilQuery);
    untilQuery.completes();
    assertEquals(1, queryResultInterest.size());
    assertEquals(person, queryResultInterest.persistentObject(0));
  }

  @Test
  public void testThatMultiPersistQueryResolves() {
    final TestUntil untilPersist = persistInterest.untilHappenings(1);
    final Person person1 = new Person("Tom Jones", 78);
    final Person person2 = new Person("Dean Martin", 78);
    final Person person3 = new Person("Sally Struthers", 71);
    objectStore.persistAll(Arrays.asList(person1, person2, person3), persistInterest);
    untilPersist.completes();
    assertEquals(3, persistInterest.size());

    final TestUntil untilQuery = queryResultInterest.untilHappenings(1);
    objectStore.queryAll(QueryExpression.using(Person.class, "findAll"), queryResultInterest, untilQuery);
    untilQuery.completes();
    assertEquals(3, queryResultInterest.size());
    assertEquals(person1, queryResultInterest.persistentObject(0));
    assertEquals(person2, queryResultInterest.persistentObject(1));
    assertEquals(person3, queryResultInterest.persistentObject(2));
  }

  @Before
  public void setUp() {
    persistInterest = new MockPersistResultInterest();
    queryResultInterest = new MockQueryResultInterest();
    world = World.startWithDefaults("test-object-store");
    objectStore = world.actorFor(ObjectStore.class, InMemoryObjectStoreActor.class);
  }
}
