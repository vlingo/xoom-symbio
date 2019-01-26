// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.object;

import java.io.Serializable;

/**
 * A base type for persistent objects.
 */
public abstract class PersistentObject implements Serializable {
  private static final long Unidentified = -1;
  private static final long serialVersionUID = 1L;

  private long persistenceId = Unidentified;

  /**
   * Answer {@code persistentObject} as a {@code PersistentObject}.
   * @param persistentObject the Object
   * @return PersistentObject
   */
  public static PersistentObject from(final Object persistentObject) {
    return (PersistentObject) persistentObject;
  }

  /**
   * Answer my persistenceId.
   * @return long
   */
  public long persistenceId() {
    return persistenceId;
  }

  /**
   * Answer whether or not I am uniquely identified or
   * still awaiting identity assignment.
   * @return boolean
   */
  public boolean isIdentified() {
    return persistenceId != Unidentified;
  }

  /**
   * Construct my default state with {@code persistenceId}.
   * @param persistenceId the long unique identity used for my persistence
   */
  protected PersistentObject(final long persistenceId) {
    this();
    this.persistenceId = persistenceId;
  }

  /**
   * Construct my default state.
   */
  protected PersistentObject() {
  }
}
