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
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.actors.testkit.TestWorld;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.Entity1.Entity1StateAdapter;
import io.vlingo.symbio.store.state.MockDispatcher;
import io.vlingo.symbio.store.state.MockStateStoreResultInterest;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;

public class InMemoryStateStoreTest {
  private final static String StoreName = Entity1.class.getSimpleName();

  private MockDispatcher dispatcher;
  private MockStateStoreResultInterest interest;
  private StateStore store;
  private TestWorld testWorld;
  private World world;

  @Test
  public void testThatStateStoreWritesText() {
    final Entity1 entity = new Entity1("123", 5);
    interest.until = TestUntil.happenings(2);

    store.write(entity.id, entity, 1, interest);

    interest.until.completes();

    assertEquals(0, interest.readObjectResultedIn.get());
    assertEquals(1, interest.writeObjectResultedIn.get());
    assertEquals(Result.Success, interest.objectWriteResult.get());
    assertEquals(entity, interest.objectState.get());
  }

  @Test
  public void testThatStateStoreWritesAndReadsObject() {
    final Entity1 entity = new Entity1("123", 5);
    interest.until = TestUntil.happenings(3);

    store.write(entity.id, entity, 1, interest);
    store.read(entity.id, Entity1.class, interest);

    interest.until.completes();

    assertEquals(1, interest.readObjectResultedIn.get());
    assertEquals(1, interest.writeObjectResultedIn.get());
    assertEquals(Result.Success, interest.objectReadResult.get());
    assertEquals(entity, interest.objectState.get());

    final Entity1 readEntity = (Entity1) interest.objectState.get();

    assertEquals("123", readEntity.id);
    assertEquals(5, readEntity.value);
  }

  @Test
  public void testThatStateStoreWritesAndReadsMetadataValue() {
    final Entity1 entity = new Entity1("123", 5);
    interest.until = TestUntil.happenings(3);

    store.write(entity.id, entity, 1, interest);
    store.read(entity.id, Entity1.class, interest);

    interest.until.completes();

    assertEquals(1, interest.readObjectResultedIn.get());
    assertEquals(1, interest.writeObjectResultedIn.get());
    assertEquals(Result.Success, interest.objectReadResult.get());
    assertEquals(entity, interest.objectState.get());
    assertNotNull(interest.metadataHolder.get());
    assertTrue(interest.metadataHolder.get().hasValue());
    assertEquals("value", interest.metadataHolder.get().value);

    final Entity1 readEntity = (Entity1) interest.objectState.get();

    assertEquals("123", readEntity.id);
    assertEquals(5, readEntity.value);
  }

  @Test
  public void testThatStateStoreWritesAndReadsMetadataOperation() {
    final Entity1 entity = new Entity1("123", 5);
    interest.until = TestUntil.happenings(3);

    store.write(entity.id, entity, 1, interest);
    store.read(entity.id, Entity1.class, interest);

    interest.until.completes();

    assertEquals(1, interest.readObjectResultedIn.get());
    assertEquals(1, interest.writeObjectResultedIn.get());
    assertEquals(Result.Success, interest.objectReadResult.get());
    assertEquals(entity, interest.objectState.get());
    assertNotNull(interest.metadataHolder.get());
    assertTrue(interest.metadataHolder.get().hasOperation());
    assertEquals("op", interest.metadataHolder.get().operation);

    final Entity1 readEntity = (Entity1) interest.objectState.get();

    assertEquals("123", readEntity.id);
    assertEquals(5, readEntity.value);
  }

  @Test
  public void testThatConcurrencyViolationsDetected() {
    final Entity1 entity = new Entity1("123", 5);

    interest.until = TestUntil.happenings(4);
    store.write(entity.id, entity, 1, interest);
    store.write(entity.id, entity, 2, interest);
    interest.until.completes();

    assertEquals(2, interest.objectWriteAccumulatedResults.size());
    assertEquals(Result.Success, interest.objectWriteAccumulatedResults.poll());
    assertEquals(Result.Success, interest.objectWriteAccumulatedResults.poll());

    interest.until = TestUntil.happenings(4);
    store.write(entity.id, entity, 1, interest);
    store.write(entity.id, entity, 2, interest);
    store.write(entity.id, entity, 3, interest);
    interest.until.completes();

    assertEquals(3, interest.objectWriteAccumulatedResults.size());
    assertEquals(Result.ConcurrentyViolation, interest.objectWriteAccumulatedResults.poll());
    assertEquals(Result.ConcurrentyViolation, interest.objectWriteAccumulatedResults.poll());
    assertEquals(Result.Success, interest.objectWriteAccumulatedResults.poll());
  }

  @Test
  public void testThatStateStoreDispatches() {
    interest.until = TestUntil.happenings(6);

    dispatcher.until = TestUntil.happenings(3);

    final Entity1 entity1 = new Entity1("123", 1);
    store.write(entity1.id, entity1, 1, interest);
    final Entity1 entity2 = new Entity1("234", 2);
    store.write(entity2.id, entity2, 1, interest);
    final Entity1 entity3 = new Entity1("345", 3);
    store.write(entity3.id, entity3, 1, interest);

    interest.until.completes();
    dispatcher.until.completes();

    assertEquals(3, dispatcher.dispatched.size());
    assertEquals("123", dispatcher.dispatched.get(dispatchId("123")).id);
    assertEquals("234", dispatcher.dispatched.get(dispatchId("234")).id);
    assertEquals("345", dispatcher.dispatched.get(dispatchId("345")).id);

    interest.until = TestUntil.happenings(5);
    dispatcher.until = TestUntil.happenings(2);

    dispatcher.processDispatch.set(false);
    final Entity1 entity4 = new Entity1("456", 4);
    store.write(entity4.id, entity4, 1, interest);
    final Entity1 entity5 = new Entity1("567", 5);
    store.write(entity5.id, entity5, 1, interest);
    dispatcher.processDispatch.set(true);
    dispatcher.control.dispatchUnconfirmed();

    dispatcher.until.completes();
    interest.until.completes();

    assertEquals(5, dispatcher.dispatched.size());
    assertEquals("456", dispatcher.dispatched.get(dispatchId("456")).id);
    assertEquals("567", dispatcher.dispatched.get(dispatchId("567")).id);
  }

  @Test
  public void testThatReadErrorIsReported() {
    interest.until = TestUntil.happenings(3);
    final Entity1 entity = new Entity1("123", 1);
    store.write(entity.id, entity, 1, interest);
    store.read(null, Entity1.class, interest);
    interest.until.completes();
    assertEquals(1, interest.errorCauses.size());
    assertEquals("The id is null.", interest.errorCauses.poll().getMessage());
    assertTrue(interest.objectReadResult.get().isError());
    
    interest.until = TestUntil.happenings(1);
    store.read(entity.id, null, interest);
    interest.until.completes();
    assertEquals("The type is null.", interest.errorCauses.poll().getMessage());
    assertTrue(interest.objectReadResult.get().isError());
    assertNull(interest.objectState.get());
  }

  @Test
  public void testThatWriteErrorIsReported() {
    interest.until = TestUntil.happenings(1);
    store.write(null, null, 0, interest);
    interest.until.completes();
    assertEquals(1, interest.errorCauses.size());
    assertEquals("The state is null.", interest.errorCauses.poll().getMessage());
    assertTrue(interest.objectWriteAccumulatedResults.poll().isError());
    assertNull(interest.objectState.get());
  }

  @Before
  public void setUp() {
    testWorld = TestWorld.startWithDefaults("test-store");
    world = testWorld.world();

    interest = new MockStateStoreResultInterest(0);
    dispatcher = new MockDispatcher(0, interest);

    store = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, dispatcher);
    store.registerAdapter(Entity1.class, new Entity1StateAdapter());

    StateTypeStateStoreMap.stateTypeToStoreName(Entity1.class, StoreName);
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  private String dispatchId(final String entityId) {
    return StoreName + ":" + entityId;
  }
}
