// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.state.inmemory;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.StateAdapterProvider;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.Entity1.Entity1StateAdapter;
import io.vlingo.symbio.store.state.MockStateStoreDispatcher;
import io.vlingo.symbio.store.state.MockStateStoreResultInterest;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * RedispatchControlTest
 */
public class InMemoryStateStoreRedispatchControlTest {

  private final static String StoreName = Entity1.class.getSimpleName();

  private MockStateStoreDispatcher dispatcher;
  private MockStateStoreResultInterest interest;
  private StateStore store;
  private World world;

  @Test
  public void testRedispatch() {
    final AccessSafely accessDispatcher = dispatcher.afterCompleting(3);

    final Entity1 entity = new Entity1("123", 5);

    accessDispatcher.writeUsing("processDispatch", false);
    store.write(entity.id, entity, 1, interest);

    try {
      Thread.sleep(3000);
    }
    catch (InterruptedException ex) {
      //ignored
    }

    accessDispatcher.writeUsing("processDispatch", true);

    int dispatchedStateCount = accessDispatcher.readFrom("dispatchedStateCount");
    assertTrue("dispatchedStateCount", dispatchedStateCount == 1);

    int dispatchAttemptCount = accessDispatcher.readFrom("dispatchAttemptCount");
    assertTrue("dispatchAttemptCount", dispatchAttemptCount > 1);
  }

  @Before
  public void setUp() {
    world = World.startWithDefaults("test-store");

    interest = new MockStateStoreResultInterest();
    dispatcher = new MockStateStoreDispatcher(interest);

    final StateAdapterProvider stateAdapterProvider = new StateAdapterProvider(world);
    new EntryAdapterProvider(world);

    stateAdapterProvider.registerAdapter(Entity1.class, new Entity1StateAdapter());
    // NOTE: No adapter registered for Entity2.class because it will use the default

    StateTypeStateStoreMap.stateTypeToStoreName(Entity1.class, StoreName);

    store = world.actorFor(StateStore.class, InMemoryStateStoreActor.class, dispatcher);
  }

  @After
  public void tearDown() {
    world.terminate();
  }
}
