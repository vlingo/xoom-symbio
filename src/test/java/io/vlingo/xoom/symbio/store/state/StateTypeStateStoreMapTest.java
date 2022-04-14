// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

public class StateTypeStateStoreMapTest {

  @Test
  public void testExistingMappings() {
    StateTypeStateStoreMap.stateTypeToStoreName(Entity1.class, Entity1.class.getSimpleName());

    assertEquals(Entity1.class.getSimpleName(), StateTypeStateStoreMap.storeNameFrom(Entity1.class));
    assertEquals(Entity1.class.getSimpleName(), StateTypeStateStoreMap.storeNameFrom(Entity1.class.getName()));

    assertNull(StateTypeStateStoreMap.storeNameFrom(Entity2.class));
    assertNull(StateTypeStateStoreMap.storeNameFrom(Entity2.class.getName()));

    StateTypeStateStoreMap.stateTypeToStoreName(Entity2.class, Entity2.class.getSimpleName());

    assertEquals(Entity2.class.getSimpleName(), StateTypeStateStoreMap.storeNameFrom(Entity2.class));
    assertEquals(Entity2.class.getSimpleName(), StateTypeStateStoreMap.storeNameFrom(Entity2.class.getName()));

    assertEquals(Entity1.class.getSimpleName(), StateTypeStateStoreMap.storeNameFrom(Entity1.class));
    assertEquals(Entity1.class.getSimpleName(), StateTypeStateStoreMap.storeNameFrom(Entity1.class.getName()));
  }

  @Test
  public void testNonExistingMappings() {
    StateTypeStateStoreMap.stateTypeToStoreName(Entity1.class, Entity1.class.getSimpleName());
    StateTypeStateStoreMap.stateTypeToStoreName(Entity2.class, Entity2.class.getSimpleName());

    assertNull(StateTypeStateStoreMap.storeNameFrom("123"));
    assertNull(StateTypeStateStoreMap.storeNameFrom(String.class));
  }

  @Before
  public void setUp() {
    StateTypeStateStoreMap.reset();
  }

  private static class Entity1 { }
  private static class Entity2 { }
}
