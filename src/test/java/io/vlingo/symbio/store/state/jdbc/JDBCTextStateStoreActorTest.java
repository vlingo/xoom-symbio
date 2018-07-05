package io.vlingo.symbio.store.state.jdbc;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.MockResultInterest;
import io.vlingo.symbio.store.state.MockTextDispatcher;
import io.vlingo.symbio.store.state.StateStore.DataFormat;
import io.vlingo.symbio.store.state.StateStore.StorageDelegate;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.TextStateStore;
import io.vlingo.symbio.store.state.jdbc.hsqldb.HSQLDBStorageDelegate;

public abstract class JDBCTextStateStoreActorTest {
  protected StorageDelegate delegate;
  protected MockTextDispatcher dispatcher;
  protected String entity1StoreName;
  protected TextStateStore store;
  protected World world;

  @Test
  public void testThatStateStoreDispatches() {
    final MockResultInterest interest1 = new MockResultInterest(3);
    dispatcher.until = TestUntil.happenings(3);

    final Entity1 entity1 = new Entity1("123", 1);
    store.write(new TextState(entity1.id, Entity1.class, 1, JsonSerialization.serialized(entity1), 1), interest1);
    final Entity1 entity2 = new Entity1("234", 2);
    store.write(new TextState(entity2.id, Entity1.class, 1, JsonSerialization.serialized(entity2), 1), interest1);
    final Entity1 entity3 = new Entity1("345", 3);
    store.write(new TextState(entity3.id, Entity1.class, 1, JsonSerialization.serialized(entity3), 1), interest1);

    dispatcher.until.completes();
    interest1.until.completes();

    assertEquals(3, dispatcher.dispatched.size());
    assertEquals("123", dispatcher.dispatched.get(dispatchId("123")).id);
    assertEquals("234", dispatcher.dispatched.get(dispatchId("234")).id);
    assertEquals("345", dispatcher.dispatched.get(dispatchId("345")).id);

    dispatcher.until = TestUntil.happenings(2);
    final MockResultInterest interest2 = new MockResultInterest(2);

    dispatcher.processDispatch.set(false);
    final Entity1 entity4 = new Entity1("456", 4);
    store.write(new TextState(entity4.id, Entity1.class, 1, JsonSerialization.serialized(entity4), 1), interest2);
    final Entity1 entity5 = new Entity1("567", 5);
    store.write(new TextState(entity5.id, Entity1.class, 1, JsonSerialization.serialized(entity5), 1), interest2);
    dispatcher.processDispatch.set(true);
    dispatcher.control.dispatchUnconfirmed();

    dispatcher.until.completes();
    interest2.until.completes();

    assertEquals(5, dispatcher.dispatched.size());
    assertEquals("456", dispatcher.dispatched.get(dispatchId("456")).id);
    assertEquals("567", dispatcher.dispatched.get(dispatchId("567")).id);
  }

  @Before
  public void setUp() {
    world = World.start("test-store", true);

    entity1StoreName = Entity1.class.getSimpleName();
    StateTypeStateStoreMap.stateTypeToStoreName(Entity1.class, entity1StoreName);

    dispatcher = new MockTextDispatcher();

    delegate = delegate();

    store = world.actorFor(
            Definition.has(JDBCTextStateStoreActor.class, Definition.parameters(dispatcher, delegate)),
            TextStateStore.class);
  }

  @After
  public void tearDown() throws Exception {
    delegate.dropAll();
    delegate.close();
    world.terminate();
  }

  protected abstract StorageDelegate delegate();

  private String dispatchId(final String entityId) {
    return entity1StoreName + ":" + entityId;
  }
}
