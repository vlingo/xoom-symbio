// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.projection.state;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import io.vlingo.actors.Actor;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.projection.MockProjection;
import io.vlingo.symbio.projection.Projectable;
import io.vlingo.symbio.projection.Projection;
import io.vlingo.symbio.projection.ProjectionControl;
import io.vlingo.symbio.projection.ProjectionDispatcher;
import io.vlingo.symbio.projection.ProjectionDispatcherTest;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.Entity2;
import io.vlingo.symbio.store.state.MockResultInterest;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateStore.Dispatcher;
import io.vlingo.symbio.store.state.TextStateStore;
import io.vlingo.symbio.store.state.TextStateStore.TextDispatcher;

public class TextStateProjectionDispatcherTest extends ProjectionDispatcherTest {

  @Test
  public void testThatTextStateDataProjects() {
    final TextStateStore store = store();

    final MockResultInterest interest = new MockResultInterest(0);

    final MockProjection projection = new MockProjection();

    projectionDispatcher.projectTo(projection, "op1");
    projectionDispatcher.projectTo(projection, "op2");

    final TextState state1 =
            new TextState("123-1", Entity1.class, 1, "test-state-1", 1, Metadata.with("value1", "op1"));
    final TextState state2 =
            new TextState("123-2", Entity2.class, 1, "test-state-2", 1, Metadata.with("value2", "op2"));

    projection.until = TestUntil.happenings(2);

    store.write(state1, interest);
    store.write(state2, interest);

    projection.until.completes();

    assertEquals(2, projection.projectedDataIds.size());
    assertEquals("123-1", projection.projectedDataIds.get(0));
    assertEquals("123-2", projection.projectedDataIds.get(1));
  }

  @Test
  public void testThatProjectionsPipeline() {
    final TextStateStore store = store();

    final FilterOutcome filterOutcome = new FilterOutcome(3);

    ProjectionDispatcher filter1 =
            FilterProjectionDispatcherActor.filterFor(world, projectionDispatcher, "op-1", filterOutcome);

    ProjectionDispatcher filter2 =
            FilterProjectionDispatcherActor.filterFor(world, filter1, "op-1", filterOutcome);

    FilterProjectionDispatcherActor.filterFor(world, filter2, "op-1", filterOutcome);

    final TextState state1 =
            new TextState("123-1", Entity1.class, 1, "test-state-1", 1, Metadata.with("value1", "op-1"));

    store.write(state1, new MockResultInterest(0));

    filterOutcome.until.completes();

    assertEquals(3, filterOutcome.filterCount.get());
  }

  @Override
  protected Class<? extends Dispatcher> dispatcherInterfaceClass() {
    return TextDispatcher.class;
  }

  @Override
  protected Class<? extends Actor> projectionDispatcherClass() {
    return TextStateProjectionDispatcherActor.class;
  }

  @Override
  protected Class<? extends StateStore> stateStoreInterfaceClass() {
    return TextStateStore.class;
  }

  public static class FilterProjectionDispatcherActor extends StateProjectionDispatcherActor
      implements Projection, ProjectionDispatcher {

    private final FilterOutcome outcome;
    
    public static ProjectionDispatcher filterFor(
            final World world,
            final ProjectionDispatcher projectionDispatcher,
            final String becauseOf,
            final FilterOutcome filterOutcome) {

      final Protocols projectionProtocols =
              world.actorFor(
                      Definition.has(FilterProjectionDispatcherActor.class, Definition.parameters(filterOutcome)),
                      new Class<?>[] { ProjectionDispatcher.class, Projection.class });

      final Protocols.Two<ProjectionDispatcher, Projection> projectionFilter = Protocols.two(projectionProtocols);

      projectionDispatcher.projectTo(projectionFilter.p2(), becauseOf);

      return projectionFilter.p1();
    }

    public FilterProjectionDispatcherActor(final FilterOutcome outcome) {
      this.outcome = outcome;
    }

    @Override
    public void projectWith(final Projectable projectable, final ProjectionControl control) {
      outcome.filterCount.incrementAndGet();
      control.confirmProjected(projectable.projectionId());
      dispatch(projectable.projectionId(), projectable);
      outcome.until.happened();
    }

    @Override
    protected boolean requiresDispatchedConfirmation() {
      return false;
    }
  }

  private static final class FilterOutcome {
    public final AtomicInteger filterCount;
    public final TestUntil until;

    FilterOutcome(final int testUntilHappenings) {
      this.filterCount = new AtomicInteger(0);
      this.until = TestUntil.happenings(testUntilHappenings);
    }
  }
}
