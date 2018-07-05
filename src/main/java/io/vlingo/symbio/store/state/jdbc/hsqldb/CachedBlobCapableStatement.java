// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc.hsqldb;

import java.sql.Blob;
import java.sql.PreparedStatement;

import io.vlingo.symbio.store.state.jdbc.CachedStatement;

class CachedBlobCapableStatement extends CachedStatement {
  final Blob blob;

  CachedBlobCapableStatement(final PreparedStatement preparedStatement, final Blob blob) {
    super(preparedStatement);
    this.blob = blob;
  }
}
