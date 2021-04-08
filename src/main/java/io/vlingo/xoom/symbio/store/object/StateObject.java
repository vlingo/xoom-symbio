// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.object;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
/**
 * A base type for persistent object states.
 */
public abstract class StateObject implements Serializable {
  /** May be used by subclasses to indicate they have not yet been persisted. */
  protected static final long Unidentified = -1;
  private static final long InitialVersion = 0L;
  private static final long serialVersionUID = 1L;

  /** My surrogate (non-business) identity used by the database. */
  private long persistenceId = Unidentified;

  /**
   * My persistent version, indicating how many state
   * mutations I have suffered over my lifetime. The default
   * value is {@link #InitialVersion}.
   */
  private long version = InitialVersion;

  /**
   * Answer {@code stateObject} as a {@code stateObject}.
   * @param stateObject the Object
   * @return stateObject
   */
  public static StateObject from(final Object stateObject) {
    return (StateObject) stateObject;
  }

  /**
   * Answer the value of the {@code Unidentified} id.
   * @return long
   */
  public static long unidentified() {
    return Unidentified;
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
   * Answers my persistence version, which can be used
   * to implement optimistic concurrency conflict detection.
   *
   * @return int
   */
  public long version() {
    return version;
  }

  /**
   * Increments my {@link version}. This method is necessary for
   * application-managed optimistic concurrency control, but should
   * not be used when the persistence mechanism (e.g., JPA) manages
   * this attribute on behalf of the application.
   */
  public void incrementVersion() {
    version++;
  }

  /**
   * Answer a {@code List<Object>} that may be used as query parameters.
   * @return {@code List<Object>}
   */
  public List<Object> queryList() {
    return Collections.emptyList();
  }

  /**
   * Answer a {@code Map<String,Object>} that may be used as query parameters.
   * @return {@code Map<String,Object>}
   */
  public Map<String,Object> queryMap() {
    return Collections.emptyMap();
  }

  /**
   * FOR INTERNAL USE ONLY.
   * @param persistenceId the long to set as my persistenceId
   */
  public void __internal__setPersistenceId(final long persistenceId) {
    this.persistenceId = persistenceId;
  }

  /**
   * Construct my default state with {@code persistenceId} and {@link version}
   * @param persistenceId the long unique identity used for my persistence
   * @param version the persistent version
   */
  protected StateObject(final long persistenceId, final long version) {
    this();
    this.persistenceId = persistenceId;
    this.version = version;
  }

  /**
   * Construct my default state with {@code persistenceId}.
   * @param persistenceId the long unique identity used for my persistence
   */
  protected StateObject(final long persistenceId) {
    this();
    this.persistenceId = persistenceId;
  }

  /**
   * Construct my default state.
   */
  protected StateObject() {
  }

  /* @see java.lang.Object#hashCode() */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (persistenceId ^ (persistenceId >>> 32));
    return result;
  }

  /* @see java.lang.Object#equals(java.lang.Object) */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StateObject other = (StateObject) obj;
    if (persistenceId != other.persistenceId)
      return false;
    return true;
  }
}
