// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.dispatch;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Dispatchable {
  /**
   * My String unique identity.
   */
  private final String id;

  /**
   * The moment when I was persistently created.
   */
  private final LocalDateTime createdOn;

  protected Dispatchable(String id, LocalDateTime createdOn) {
    this.id = id;
    this.createdOn = createdOn;
  }

  public String getId() {
    return id;
  }

  public LocalDateTime createdOn() {
    return createdOn;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Dispatchable that = (Dispatchable) o;
    return id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
