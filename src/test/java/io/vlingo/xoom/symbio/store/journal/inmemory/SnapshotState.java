// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.journal.inmemory;

import io.vlingo.xoom.symbio.State;

public class SnapshotState extends State<String> {
  public SnapshotState() {
    super("1", String.class, 1, "data", 1, null);
  }
}
