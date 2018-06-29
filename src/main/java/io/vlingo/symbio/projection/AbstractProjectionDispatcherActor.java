// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.projection;

import java.util.List;

import io.vlingo.actors.Actor;

public class AbstractProjectionDispatcherActor extends Actor implements ProjectionDispatcher {
  private final MatchableProjections matchableProjections;

  protected AbstractProjectionDispatcherActor() {
    this.matchableProjections = new MatchableProjections();
  }

  //=====================================
  // ProjectionDispatcher
  //=====================================

  @Override
  public void projectTo(final Projection projection, final String whenMatchingCause) {
    matchableProjections.mayDispatchTo(projection, whenMatchingCause);
  }

  //=====================================
  // internal implementation
  //=====================================

  protected boolean hasProjectionsFor(final String actualCause) {
    return !projectionsFor(actualCause).isEmpty();
  }

  protected List<Projection> projectionsFor(final String actualCause) {
    return matchableProjections.matchProjections(actualCause);
  }
}
