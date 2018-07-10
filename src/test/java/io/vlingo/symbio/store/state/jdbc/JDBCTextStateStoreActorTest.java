package io.vlingo.symbio.store.state.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
import io.vlingo.symbio.store.state.jdbc.Configuration.TestConfiguration;

public abstract class JDBCTextStateStoreActorTest {
  protected TestConfiguration configuration;
  protected StorageDelegate delegate;
  protected MockTextDispatcher dispatcher;
  protected String entity1StoreName;
  protected MockResultInterest interest;
  protected TextStateStore store;
  protected World world;

  @Test
  public void testThatStateStoreDispatches() throws Exception {
    interest.until = TestUntil.happenings(6);
    dispatcher.until = TestUntil.happenings(3);

    final Entity1 entity1 = new Entity1("123", 1);
    store.write(new TextState(entity1.id, Entity1.class, 1, JsonSerialization.serialized(entity1), 1), interest);
    final Entity1 entity2 = new Entity1("234", 2);
    store.write(new TextState(entity2.id, Entity1.class, 1, JsonSerialization.serialized(entity2), 1), interest);
    final Entity1 entity3 = new Entity1("345", 3);
    store.write(new TextState(entity3.id, Entity1.class, 1, JsonSerialization.serialized(entity3), 1), interest);

    dispatcher.until.completes();
    interest.until.completes();

    assertEquals(3, dispatcher.dispatched.size());
    assertEquals(3, interest.confirmDispatchedResultedIn.get());
    assertEquals("123", dispatcher.dispatched.get(dispatchId("123")).id);
    assertEquals("234", dispatcher.dispatched.get(dispatchId("234")).id);
    assertEquals("345", dispatcher.dispatched.get(dispatchId("345")).id);

    interest.until = TestUntil.happenings(6);
    dispatcher.until = TestUntil.happenings(2);
    
    dispatcher.processDispatch.set(false);
    final Entity1 entity4 = new Entity1("456", 4);
    store.write(new TextState(entity4.id, Entity1.class, 1, JsonSerialization.serialized(entity4), 1), interest);
    final Entity1 entity5 = new Entity1("567", 5);
    store.write(new TextState(entity5.id, Entity1.class, 1, JsonSerialization.serialized(entity5), 1), interest);
    dispatcher.processDispatch.set(true);
    dispatcher.control.dispatchUnconfirmed();

    dispatcher.until.completes();
    interest.until.completes();

    assertEquals(5, dispatcher.dispatched.size());
    assertEquals(7, interest.confirmDispatchedResultedIn.get());
    assertEquals("456", dispatcher.dispatched.get(dispatchId("456")).id);
    assertEquals("567", dispatcher.dispatched.get(dispatchId("567")).id);
  }

  @Test
  public void testThatReadErrorIsReported() {
    interest.until = TestUntil.happenings(3); // includes write, confirmation, read (not necessarily in that order)
    final Entity1 entity = new Entity1("123", 1);
    store.write(new TextState(entity.id, Entity1.class, 1, JsonSerialization.serialized(entity), 1), interest);
    store.read(null, Entity1.class, interest);
    interest.until.completes();
    assertEquals(1, interest.errorCauses.size());
    assertEquals("The id is null.", interest.errorCauses.poll().getMessage());
    assertTrue(interest.textReadResult.get().isError());
    assertTrue(interest.textState.get().isNull());
    
    interest.until = TestUntil.happenings(1);
    store.read(entity.id, null, interest);  // includes read
    interest.until.completes();
    assertEquals(1, interest.errorCauses.size());
    assertEquals("The type is null.", interest.errorCauses.poll().getMessage());
    assertTrue(interest.textReadResult.get().isError());
    assertTrue(interest.textState.get().isNull());
  }

  @Test
  public void testThatWriteErrorIsReported() {
    interest.until = TestUntil.happenings(1);
    store.write(null, interest);
    interest.until.completes();
    assertEquals(1, interest.errorCauses.size());
    assertEquals("The state is null.", interest.errorCauses.poll().getMessage());
    assertTrue(interest.textWriteAccumulatedResults.poll().isError());
    assertTrue(interest.textState.get().isNull());
  }

  @Before
  public void setUp() throws Exception {
    world = World.start("test-store", true);

    entity1StoreName = Entity1.class.getSimpleName();
    StateTypeStateStoreMap.stateTypeToStoreName(Entity1.class, entity1StoreName);

    configuration = testConfiguration(DataFormat.Text);

    delegate = delegate();

    interest = new MockResultInterest(0);
    dispatcher = new MockTextDispatcher(0, interest);

    store = world.actorFor(
            Definition.has(JDBCTextStateStoreActor.class, Definition.parameters(dispatcher, delegate)),
            TextStateStore.class);
  }

  @After
  public void tearDown() throws Exception {
    if (configuration == null) return;
    configuration.cleanUp();
    delegate.close();
    world.terminate();
  }

  protected abstract StorageDelegate delegate() throws Exception;
  protected abstract TestConfiguration testConfiguration(final DataFormat format) throws Exception;

  private String dispatchId(final String entityId) {
    return entity1StoreName + ":" + entityId;
  }
}
