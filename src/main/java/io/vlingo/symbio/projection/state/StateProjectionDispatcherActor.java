// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.projection.state;

import io.vlingo.symbio.projection.AbstractProjectionDispatcherActor;
import io.vlingo.symbio.projection.Projectable;
import io.vlingo.symbio.projection.Projection;
import io.vlingo.symbio.projection.ProjectionControl;
import io.vlingo.symbio.projection.ProjectionDispatcher;
import io.vlingo.symbio.store.state.StateStore.Dispatcher;
import io.vlingo.symbio.store.state.StateStore.DispatcherControl;

public abstract class StateProjectionDispatcherActor extends AbstractProjectionDispatcherActor
    implements Dispatcher, ProjectionDispatcher {

  private DispatcherControl control;
  private final ProjectionControl projectionControl;

  protected StateProjectionDispatcherActor() {
    this.projectionControl = new ProjectionControl() {
      @Override
      public void confirmProjected(String projectionId) {
        if (control != null) {
          control.confirmDispatched(projectionId);
        } else if (requiresDispatchedConfirmation()) {
          logger().log("WARNING: ProjectionDispatcher control is not set; unconfirmed: " + projectionId);
        }
      }
    };
  }

  //=====================================
  // Dispatcher
  //=====================================

  @Override
  public void controlWith(final DispatcherControl control) {
    this.control = control;
  }

  //=====================================
  // internal implementation
  //=====================================

  protected abstract boolean requiresDispatchedConfirmation();

  protected void dispatch(final String dispatchId, final Projectable projectable) {
    for (final Projection projection : projectionsFor(projectable.becauseOf())) {
      projection.projectWith(projectable, projectionControl);
    }
  }
}
