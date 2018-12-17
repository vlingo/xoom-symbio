// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

public interface StateAdapter<S,R> {
  S fromRaw(final R raw, final int stateVersion, final int typeVersion);
  R toRaw(final S state, final int stateVersion, final int typeVersion);
}
