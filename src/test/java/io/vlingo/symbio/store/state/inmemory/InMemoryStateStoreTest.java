// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.inmemory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.Entity1.Entity1StateAdapter;
import io.vlingo.symbio.store.state.Entity2;
import io.vlingo.symbio.store.state.MockDispatcher;
import io.vlingo.symbio.store.state.MockStateStoreResultInterest;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;

public class InMemoryStateStoreTest {
  private final static String StoreName1 = Entity1.class.getSimpleName();
  private final static String StoreName2 = Entity2.class.getSimpleName();

  private MockDispatcher dispatcher;
  private MockStateStoreResultInterest interest;
  private StateStore store;
  private TestWorld testWorld;
  private World world;

  @Test
  public void testThatStateStoreWritesText() {
    final AccessSafely access1 = interest.afterCompleting(1);
    dispatcher.afterCompleting(1);

    final Entity1 entity = new Entity1("123", 5);

    store.write(entity.id, entity, 1, interest);

    assertEquals(0, (int) access1.readFrom("readObjectResultedIn"));
    assertEquals(1, (int) access1.readFrom("writeObjectResultedIn"));
    assertEquals(Result.Success, access1.readFrom("objectWriteResult"));
    assertEquals(entity, access1.readFrom("objectState"));
  }

  @Test
  public void testThatStateStoreWritesAndReadsObject() {
    final AccessSafely access1 = interest.afterCompleting(2);
    dispatcher.afterCompleting(2);

    final Entity1 entity = new Entity1("123", 5);

    store.write(entity.id, entity, 1, interest);
    store.read(entity.id, Entity1.class, interest);

    assertEquals(1, (int) access1.readFrom("readObjectResultedIn"));
    assertEquals(1, (int) access1.readFrom("writeObjectResultedIn"));
    assertEquals(Result.Success, access1.readFrom("objectReadResult"));
    assertEquals(entity, access1.readFrom("objectState"));

    final Entity1 readEntity = (Entity1) access1.readFrom("objectState");

    assertEquals("123", readEntity.id);
    assertEquals(5, readEntity.value);
  }

  @Test
  public void testThatStateStoreWritesAndReadsMetadataValue() {
    final AccessSafely access1 = interest.afterCompleting(2);
    dispatcher.afterCompleting(2);

    final Entity1 entity = new Entity1("123", 5);

    store.write(entity.id, entity, 1, interest);
    store.read(entity.id, Entity1.class, interest);

    assertEquals(1, (int) access1.readFrom("readObjectResultedIn"));
    assertEquals(1, (int) access1.readFrom("writeObjectResultedIn"));
    assertEquals(Result.Success, access1.readFrom("objectReadResult"));
    assertEquals(entity, access1.readFrom("objectState"));
    assertNotNull(access1.readFrom("metadataHolder"));
    final Metadata metadata = access1.readFrom("metadataHolder");
    assertTrue(metadata.hasValue());
    assertEquals("value", metadata.value);

    final Entity1 readEntity = (Entity1) access1.readFrom("objectState");

    assertEquals("123", readEntity.id);
    assertEquals(5, readEntity.value);
  }

  @Test
  public void testThatStateStoreWritesAndReadsMetadataOperation() {
    final AccessSafely access1 = interest.afterCompleting(2);
    dispatcher.afterCompleting(2);

    final Entity1 entity = new Entity1("123", 5);

    store.write(entity.id, entity, 1, interest);
    store.read(entity.id, Entity1.class, interest);

    assertEquals(1, (int) access1.readFrom("readObjectResultedIn"));
    assertEquals(1, (int) access1.readFrom("writeObjectResultedIn"));
    assertEquals(Result.Success, access1.readFrom("objectReadResult"));
    assertEquals(entity, access1.readFrom("objectState"));
    final Metadata metadata = access1.readFrom("metadataHolder");
    assertNotNull(metadata);
    assertTrue(metadata.hasOperation());
    assertEquals("op", metadata.operation);

    final Entity1 readEntity = (Entity1) access1.readFrom("objectState");

    assertEquals("123", readEntity.id);
    assertEquals(5, readEntity.value);
  }

  @Test
  public void testThatConcurrencyViolationsDetected() {
    final AccessSafely access1 = interest.afterCompleting(2);
    dispatcher.afterCompleting(2);

    final Entity1 entity = new Entity1("123", 5);

    store.write(entity.id, entity, 1, interest);
    store.write(entity.id, entity, 2, interest);

    assertEquals(2, (int) access1.readFrom("objectWriteAccumulatedResultsCount"));
    assertEquals(Result.Success, access1.readFrom("objectWriteAccumulatedResults"));
    assertEquals(Result.Success, access1.readFrom("objectWriteAccumulatedResults"));
    assertEquals(0, (int) access1.readFrom("objectWriteAccumulatedResultsCount"));

    final AccessSafely access2 = interest.afterCompleting(3);
    dispatcher.afterCompleting(3);

    store.write(entity.id, entity, 1, interest);
    store.write(entity.id, entity, 2, interest);
    store.write(entity.id, entity, 3, interest);

    assertEquals(3, (int) access2.readFrom("objectWriteAccumulatedResultsCount"));
    assertEquals(Result.ConcurrentyViolation, access2.readFrom("objectWriteAccumulatedResults"));
    assertEquals(Result.ConcurrentyViolation, access2.readFrom("objectWriteAccumulatedResults"));
    assertEquals(Result.Success, access2.readFrom("objectWriteAccumulatedResults"));
  }

  @Test
  public void testThatStateStoreDispatches() {
    interest.afterCompleting(3);
    final AccessSafely accessDispatcher = dispatcher.afterCompleting(3);

    final Entity1 entity1 = new Entity1("123", 1);
    store.write(entity1.id, entity1, 1, interest);
    final Entity1 entity2 = new Entity1("234", 2);
    store.write(entity2.id, entity2, 1, interest);
    final Entity1 entity3 = new Entity1("345", 3);
    store.write(entity3.id, entity3, 1, interest);

    assertEquals(3, (int) accessDispatcher.readFrom("dispatchedStateCount"));
    final State<?> state123 = accessDispatcher.readFrom("dispatchedState", dispatchId("123"));
    assertEquals("123", state123.id);
    final State<?> state234 = accessDispatcher.readFrom("dispatchedState", dispatchId("234"));
    assertEquals("234", state234.id);
    final State<?> state345 = accessDispatcher.readFrom("dispatchedState", dispatchId("345"));
    assertEquals("345", state345.id);

    interest.afterCompleting(4);
    final AccessSafely accessDispatcher1 = dispatcher.afterCompleting(4);

    accessDispatcher1.writeUsing("processDispatch", false);
    final Entity1 entity4 = new Entity1("456", 4);
    store.write(entity4.id, entity4, 1, interest);
    final Entity1 entity5 = new Entity1("567", 5);
    store.write(entity5.id, entity5, 1, interest);

    accessDispatcher1.writeUsing("processDispatch", true);
    dispatcher.dispatchUnconfirmed();
    accessDispatcher1.readFrom("dispatchedStateCount");

    assertEquals(5, (int) accessDispatcher1.readFrom("dispatchedStateCount"));

    final State<?> state456 = accessDispatcher1.readFrom("dispatchedState", dispatchId("456"));
    assertEquals("456", state456.id);
    final State<?> state567 = accessDispatcher1.readFrom("dispatchedState", dispatchId("567"));
    assertEquals("567", state567.id);
  }

  @Test
  public void testThatReadErrorIsReported() {
    final AccessSafely access1 = interest.afterCompleting(2);
    dispatcher.afterCompleting(2);

    final Entity1 entity = new Entity1("123", 1);
    store.write(entity.id, entity, 1, interest);
    store.read(null, Entity1.class, interest);

    assertEquals(1, (int) access1.readFrom("errorCausesCount"));
    final Exception cause1 = access1.readFrom("errorCauses");
    assertEquals("The id is null.", cause1.getMessage());
    Result result1 = access1.readFrom("objectReadResult");
    assertTrue(result1.isError());

    interest = new MockStateStoreResultInterest();
    final AccessSafely access2 = interest.afterCompleting(1);
    dispatcher.afterCompleting(1);

    store.read(entity.id, null, interest);

    final Exception cause2 = access2.readFrom("errorCauses");
    assertEquals("The type is null.", cause2.getMessage());
    Result result2 = access2.readFrom("objectReadResult");
    assertTrue(result2.isError());
    final Object objectState = access2.readFrom("objectState");
    assertNull(objectState);
  }

  @Test
  public void testThatWriteErrorIsReported() {
    final AccessSafely access1 = interest.afterCompleting(1);
    dispatcher.afterCompleting(1);

    store.write(null, null, 0, interest);

    assertEquals(1, (int) access1.readFrom("errorCausesCount"));
    final Exception cause1 = access1.readFrom("errorCauses");
    assertEquals("The state is null.", cause1.getMessage());
    final Result result1 = access1.readFrom("objectWriteAccumulatedResults");
    assertTrue(result1.isError());
    final Object objectState = access1.readFrom("objectState");
    assertNull(objectState);
  }

  @Test
  public void testThatStateStoreWritesTextWithDefaultAdapter() {
    final AccessSafely access1 = interest.afterCompleting(1);
    dispatcher.afterCompleting(1);

    final Entity2 entity = new Entity2("123", "5");

    store.write(entity.id, entity, 1, interest);

    assertEquals(0, (int) access1.readFrom("readObjectResultedIn"));
    assertEquals(1, (int) access1.readFrom("writeObjectResultedIn"));
    assertEquals(Result.Success, access1.readFrom("objectWriteResult"));
    assertEquals(entity, access1.readFrom("objectState"));
  }

  @Before
  public void setUp() {
    testWorld = TestWorld.startWithDefaults("test-store");
    world = testWorld.world();

    interest = new MockStateStoreResultInterest();
    dispatcher = new MockDispatcher(interest);

    store = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, dispatcher);
    store.registerAdapter(Entity1.class, new Entity1StateAdapter());

    StateTypeStateStoreMap.stateTypeToStoreName(Entity1.class, StoreName1);
    StateTypeStateStoreMap.stateTypeToStoreName(Entity2.class, StoreName2);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  private String dispatchId(final String entityId) {
    return StoreName1 + ":" + entityId;
  }
}
