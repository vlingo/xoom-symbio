package io.vlingo.xoom.symbio.implnative;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.store.state.PartitioningStateStore;
import io.vlingo.xoom.symbio.store.state.StateTypeStateStoreMap;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;

public final class NativeImpl {
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
