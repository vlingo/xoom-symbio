// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

public interface StateAdapter<S,RS extends State<?>> {
  default boolean isBinary() { return false; }
  default boolean isObject() { return false; }
  default boolean isText() { return false; }
  int typeVersion();
  S fromRawState(final RS raw);
  RS toRawState(final S state, final int stateVersion, final Metadata metadata);

  default RS toRawState(final S state, final int stateVersion) {
    return toRawState(state, stateVersion, Metadata.nullMetadata());
  }
}
