// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.inmemory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State.ObjectState;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.MockObjectDispatcher;
import io.vlingo.symbio.store.state.MockObjectResultInterest;
import io.vlingo.symbio.store.state.ObjectStateStore;
import io.vlingo.symbio.store.state.StateStore.Result;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;

public class InMemoryObjectStateStoreTest {
  private final static String StoreName = Entity1.class.getSimpleName();

  private MockObjectDispatcher dispatcher;
  private MockObjectResultInterest interest;
  private ObjectStateStore store;
  private World world;

  @Test
  public void testThatStateStoreWritesText() {
    final Entity1 entity = new Entity1("123", 5);
    interest.until = TestUntil.happenings(1);

    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 1), interest);

    interest.until.completes();

    assertEquals(0, interest.readObjectResultedIn.get());
    assertEquals(1, interest.writeObjectResultedIn.get());
    assertEquals(Result.Success, interest.objectWriteResult.get());
    assertEquals(entity, interest.objectState.get().data);
  }

  @Test
  public void testThatStateStoreWritesAndReadsObject() {
    final Entity1 entity = new Entity1("123", 5);
    interest.until = TestUntil.happenings(3);

    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 1), interest);
    store.read(entity.id, Entity1.class, interest);

    interest.until.completes();

    assertEquals(1, interest.readObjectResultedIn.get());
    assertEquals(1, interest.writeObjectResultedIn.get());
    assertEquals(Result.Success, interest.objectReadResult.get());
    assertEquals(entity, interest.objectState.get().data);
    assertEquals(1, interest.objectState.get().typeVersion);
    assertFalse(interest.objectState.get().hasMetadata());

    final Entity1 readEntity = (Entity1) interest.objectState.get().data;

    assertEquals("123", readEntity.id);
    assertEquals(5, readEntity.value);
  }

  @Test
  public void testThatStateStoreWritesAndReadsMetadataValue() {
    final Entity1 entity = new Entity1("123", 5);
    interest.until = TestUntil.happenings(2);

    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 1, Metadata.withValue("value")), interest);
    store.read(entity.id, Entity1.class, interest);

    interest.until.completes();

    assertEquals(1, interest.readObjectResultedIn.get());
    assertEquals(1, interest.writeObjectResultedIn.get());
    assertEquals(Result.Success, interest.objectReadResult.get());
    assertEquals(entity, interest.objectState.get().data);
    assertEquals(1, interest.objectState.get().typeVersion);
    assertTrue(interest.objectState.get().hasMetadata());
    assertTrue(interest.objectState.get().metadata.hasValue());
    assertEquals("value", interest.objectState.get().metadata.value);
    assertFalse(interest.objectState.get().metadata.hasOperation());

    final Entity1 readEntity = (Entity1) interest.objectState.get().data;

    assertEquals("123", readEntity.id);
    assertEquals(5, readEntity.value);
  }

  @Test
  public void testThatStateStoreWritesAndReadsMetadataOperation() {
    final Entity1 entity = new Entity1("123", 5);
    interest.until = TestUntil.happenings(2);

    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 1, Metadata.withOperation("op")), interest);
    store.read(entity.id, Entity1.class, interest);

    interest.until.completes();

    assertEquals(1, interest.readObjectResultedIn.get());
    assertEquals(1, interest.writeObjectResultedIn.get());
    assertEquals(Result.Success, interest.objectReadResult.get());
    assertEquals(entity, interest.objectState.get().data);
    assertEquals(1, interest.objectState.get().typeVersion);
    assertTrue(interest.objectState.get().hasMetadata());
    assertFalse(interest.objectState.get().metadata.hasValue());
    assertTrue(interest.objectState.get().metadata.hasOperation());
    assertEquals("op", interest.objectState.get().metadata.operation);

    final Entity1 readEntity = (Entity1) interest.objectState.get().data;

    assertEquals("123", readEntity.id);
    assertEquals(5, readEntity.value);
  }

  @Test
  public void testThatStateStoreWritesAndReadsMetadata() {
    final Entity1 entity = new Entity1("123", 5);
    interest.until = TestUntil.happenings(3);

    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 1, Metadata.withOperation("op")), interest);
    store.read(entity.id, Entity1.class, interest);

    interest.until.completes();

    assertEquals(1, interest.readObjectResultedIn.get());
    assertEquals(1, interest.writeObjectResultedIn.get());
    assertEquals(Result.Success, interest.objectReadResult.get());
    assertEquals(entity, interest.objectState.get().data);
    assertEquals(1, interest.objectState.get().typeVersion);
    assertTrue(interest.objectState.get().hasMetadata());
    assertFalse(interest.objectState.get().metadata.hasValue());
    assertTrue(interest.objectState.get().metadata.hasOperation());
    assertEquals("op", interest.objectState.get().metadata.operation);

    final Entity1 readEntity = (Entity1) interest.objectState.get().data;

    assertEquals("123", readEntity.id);
    assertEquals(5, readEntity.value);
  }

  @Test
  public void testThatConcurrencyViolationsDetected() {
    final Entity1 entity = new Entity1("123", 5);

    interest.until = TestUntil.happenings(4);
    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 1, Metadata.withOperation("op")), interest);
    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 2, Metadata.withOperation("op")), interest);
    interest.until.completes();

    assertEquals(2, interest.objectWriteAccumulatedResults.size());
    assertEquals(Result.Success, interest.objectWriteAccumulatedResults.poll());
    assertEquals(Result.Success, interest.objectWriteAccumulatedResults.poll());

    interest.until = TestUntil.happenings(4);
    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 1, Metadata.withOperation("op")), interest);
    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 2, Metadata.withOperation("op")), interest);
    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 3, Metadata.withOperation("op")), interest);
    interest.until.completes();

    assertEquals(3, interest.objectWriteAccumulatedResults.size());
    assertEquals(Result.ConcurrentyViolation, interest.objectWriteAccumulatedResults.poll());
    assertEquals(Result.ConcurrentyViolation, interest.objectWriteAccumulatedResults.poll());
    assertEquals(Result.Success, interest.objectWriteAccumulatedResults.poll());
  }

  @Test
  public void testThatStateStoreDispatches() {
    interest.until = TestUntil.happenings(3);

    dispatcher.until = TestUntil.happenings(3);

    final Entity1 entity1 = new Entity1("123", 1);
    store.write(new ObjectState<>(entity1.id, Entity1.class, 1, entity1, 1), interest);
    final Entity1 entity2 = new Entity1("234", 2);
    store.write(new ObjectState<>(entity2.id, Entity1.class, 1, entity2, 1), interest);
    final Entity1 entity3 = new Entity1("345", 3);
    store.write(new ObjectState<>(entity3.id, Entity1.class, 1, entity3, 1), interest);

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
    store.write(new ObjectState<>(entity4.id, Entity1.class, 1, entity4, 1), interest);
    final Entity1 entity5 = new Entity1("567", 5);
    store.write(new ObjectState<>(entity5.id, Entity1.class, 1, entity5, 1), interest);
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
    store.write(new ObjectState<>(entity.id, Entity1.class, 1, entity, 1), interest);
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
    assertTrue(interest.objectState.get().isNull());
  }

  @Test
  public void testThatWriteErrorIsReported() {
    interest.until = TestUntil.happenings(1);
    store.write(null, interest);
    interest.until.completes();
    assertEquals(1, interest.errorCauses.size());
    assertEquals("The state is null.", interest.errorCauses.poll().getMessage());
    assertTrue(interest.objectWriteAccumulatedResults.poll().isError());
    assertTrue(interest.objectState.get().isNull());
  }

  @Before
  public void setUp() {
    world = World.startWithDefaults("test-store");

    interest = new MockObjectResultInterest(0);
    dispatcher = new MockObjectDispatcher(0, interest);

    store = world.actorFor(Definition.has(InMemoryObjectStateStoreActor.class, Definition.parameters(dispatcher)), ObjectStateStore.class);

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
