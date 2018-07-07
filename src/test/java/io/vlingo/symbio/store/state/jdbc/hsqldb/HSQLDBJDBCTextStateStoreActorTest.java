// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.hsqldb;

import io.vlingo.symbio.store.state.StateStore.DataFormat;
import io.vlingo.symbio.store.state.StateStore.StorageDelegate;
import io.vlingo.symbio.store.state.jdbc.Configuration.TestConfiguration;
import io.vlingo.symbio.store.state.jdbc.JDBCTextStateStoreActorTest;

public class HSQLDBJDBCTextStateStoreActorTest extends JDBCTextStateStoreActorTest {

  @Override
  protected StorageDelegate delegate() throws Exception {
    return new HSQLDBStorageDelegate(configuration, world.defaultLogger());
  }

  @Override
  protected TestConfiguration testConfiguration(final DataFormat format) throws Exception {
    return HSQLDBConfigurationProvider.testConfiguration(format);
  }
}
