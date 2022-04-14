// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package nativebuild;

import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.state.PartitioningStateStore;
import io.vlingo.xoom.symbio.store.state.StateTypeStateStoreMap;

public final class NativeBuildEntryPoint {
  @SuppressWarnings("unused")
  @CEntryPoint(name = "Java_io_vlingo_xoom_symbionative_Native_start")
  public static int start(@CEntryPoint.IsolateThreadContext long isolateId, CCharPointer name) {
    final String nameString = CTypeConversion.toJavaString(name);
    World world = World.startWithDefaults(nameString);

    StateTypeStateStoreMap.stateTypeToStoreName(Object.class, Object.class.getSimpleName());

    final int times = PartitioningStateStore.MinimumReaders + PartitioningStateStore.MinimumWriters;
    final State.BinaryState emptyState = new State.BinaryState();
    return 0;
  }
}
