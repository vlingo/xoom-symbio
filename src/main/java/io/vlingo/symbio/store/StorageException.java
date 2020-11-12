// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store;

public final class StorageException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public final Result result;

  public StorageException(final Result result, String message, Throwable cause) {
    super(message, cause);

    this.result = result;
  }

  public StorageException(final Result result, String message) {
    super(message);

    this.result = result;
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || !other.getClass().equals(getClass())) {
      return false;
    }
    return this.result == ((StorageException) other).result;
  }

  @Override
  public int hashCode() {
    return 31 * result.hashCode();
  }
}
