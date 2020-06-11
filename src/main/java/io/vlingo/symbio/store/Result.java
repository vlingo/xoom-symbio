// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store;

public enum Result {
  ConcurrencyViolation {
    @Override public boolean isConcurrencyViolation() { return true; }
  },
  Error {
    @Override public boolean isError() { return true; }
  },
  Failure {
    @Override public boolean isFailure() { return true; }
  },
  NotAllFound {
    @Override public boolean isNotAllFound() { return true; }
  },
  NotFound {
    @Override public boolean isNotFound() { return true; }
  },
  NoTypeStore {
    @Override public boolean isNoTypeStore() { return true; }
  },
  Success {
    @Override public boolean isSuccess() { return true; }
  };

  public boolean isConcurrencyViolation() { return false; }
  public boolean isError() { return false; }
  public boolean isFailure() { return false; }
  public boolean isNotAllFound() { return false; }
  public boolean isNotFound() { return false; }
  public boolean isNoTypeStore() { return false; }
  public boolean isSuccess() { return false; }
}
