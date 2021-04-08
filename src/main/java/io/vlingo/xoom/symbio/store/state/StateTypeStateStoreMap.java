// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StateTypeStateStoreMap {
  private static final Map<String,String> stateStoreNames = new ConcurrentHashMap<>();

  public static Collection<String> allStoreNames() {
    return Collections.unmodifiableCollection(stateStoreNames.values());
  }

  public static void stateTypeToStoreName(final Class<?> type, final String storeName) {
    stateStoreNames.putIfAbsent(type.getName(), storeName);
  }

  public static String storeNameFrom(final Class<?> type) {
    return storeNameFrom(type.getName());
  }

  public static String storeNameFrom(final String type) {
    return stateStoreNames.get(type);
  }

  /**
   * USED FOR TEST ONLY
   */
  public static void reset() {
    stateStoreNames.clear();
  }
}
