// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.hsqldb;

import io.vlingo.symbio.store.state.StateStore.DataFormat;
import io.vlingo.symbio.store.state.StateStore.StorageDelegate;
import io.vlingo.symbio.store.state.jdbc.JDBCTextStateStoreActorTest;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class HSQLDBJDBCTextStateStoreActorTest extends JDBCTextStateStoreActorTest {
  @Override
  protected StorageDelegate delegate() {
    return new HSQLDBStorageDelegate(DataFormat.Text, world.defaultLogger());
  }
}
