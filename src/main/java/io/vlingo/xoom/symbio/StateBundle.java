// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio;

public class StateBundle {
  public final State<?> state;
  public final Object object;

  public StateBundle(final State<?> state, final Object object) {
    this.state = state;
    this.object = object;
  }

  public StateBundle(final State<?> state) {
    this.state = state;
    this.object = null;
  }

  @SuppressWarnings("unchecked")
  public <S extends State<?>> S typedState() {
    return (S) state;
  }

  @SuppressWarnings("unchecked")
  public <O extends Object> O typedObject() {
    return (O) object;
  }
}
