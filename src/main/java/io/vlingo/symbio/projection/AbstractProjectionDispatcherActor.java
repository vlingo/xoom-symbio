// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.projection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vlingo.actors.Actor;

public class AbstractProjectionDispatcherActor extends Actor implements ProjectionDispatcher {
  private final static List<Projection> EmptyProjections = new ArrayList<>(0);

  private final Map<String, List<Projection>> mappedProjections;

  protected AbstractProjectionDispatcherActor() {
    this.mappedProjections = new HashMap<>();
  }

  //=====================================
  // ProjectionDispatcher
  //=====================================

  @Override
  public void projectTo(final Projection projection, final String becauseOf) {
    List<Projection> projections = mappedProjections.get(becauseOf);

    if (projections == null) {
      projections = new ArrayList<>(1);
      mappedProjections.put(becauseOf, projections);
    }

    projections.add(projection);
  }

  //=====================================
  // internal implementation
  //=====================================

  protected boolean hasProjectionsFor(final String becauseOf) {
    return !projectionsFor(becauseOf).isEmpty();
  }

  protected List<Projection> projectionsFor(final String becauseOf) {
    return mappedProjections.getOrDefault(becauseOf, EmptyProjections);
  }
}
