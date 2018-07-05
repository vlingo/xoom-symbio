// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.postgres;

import io.vlingo.symbio.store.state.StateStore.DataFormat;

public class Configuration {
  final DataFormat format;
  final String url;
  final String username;
  final String password;
  final boolean useSSL;
  final String originatorId;
  final boolean createTables;

  public Configuration(
          final DataFormat format,
          final String url,
          final String username,
          final String password,
          final boolean useSSL,
          final String originatorId,
          final boolean createTables) {

    this.format = format;
    this.url = url;
    this.username = username;
    this.password = password;
    this.useSSL = useSSL;
    this.originatorId = originatorId;
    this.createTables = createTables;
  }
}
