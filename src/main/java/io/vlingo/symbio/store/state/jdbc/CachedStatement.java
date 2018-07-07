// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state.jdbc;

import java.sql.PreparedStatement;

public class CachedStatement<T> {
  public final T data;
  public final PreparedStatement preparedStatement;

  public CachedStatement(final PreparedStatement preparedStatement, final T data) {
    this.preparedStatement = preparedStatement;
    this.data = data;
  }
}
