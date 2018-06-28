// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import io.vlingo.symbio.State;

public interface StateStore {
  public enum Result {
    ConcurrentyViolation {
      @Override public boolean isConcurrentyViolation() { return true; }
    },
    Failure {
      @Override public boolean isFailure() { return true; }
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

    public boolean isConcurrentyViolation() { return false; }
    public boolean isFailure() { return false; }
    public boolean isNotFound() { return false; }
    public boolean isNoTypeStore() { return false; }
    public boolean isSuccess() { return false; }
  }

  public static interface DispatcherControl {
    void confirmDispatched(final String dispatchId);
    void dispatchUnconfirmed();
  }

  public static interface Dispatcher {
    void controlWith(final DispatcherControl control);
  }

  public static interface ResultInterest<T> {
    void readResultedIn(final Result result, final String id, final State<T> state);
    void writeResultedIn(final Result result, final String id, final State<T> state);
  }
}
