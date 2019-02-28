// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.object;
/**
 * VersionedPersistentObject is a {@link PersistentObject} that
 * tracks its persistent {@link #version} in order to support
 * optimistic concurrency conflict detection.
 */
public class VersionedPersistentObject extends PersistentObject {

  private static final long serialVersionUID = -3030023638306588079L;
  public static final int INITIAL_VERSION = 0;
  
  /**
   * My persistent version, indicating how many state
   * mutations I have suffered over my lifetime. The default
   * value is {@link #INITIAL_VERSION}.
   */
  private int version = INITIAL_VERSION;
  
  /**
   * Answers {@code object} as a {@link VersionedPersistentObject}
   * 
   * @param object the object to cast
   * @return {@link VersionedPersistentObject}
   */
  public static VersionedPersistentObject from(Object object) {
    return (VersionedPersistentObject) object;
  }

  /**
   * Construct my default state.
   */
  protected VersionedPersistentObject() {
    super();
  }

  /**
   * Construct my default state with {@code persistenceId}
   * and {@code version}.
   * 
   * @param persistenceId the persistent identifier
   * @param version the persistent version
   */
  protected VersionedPersistentObject(final long persistenceId, final int version) {
    super(persistenceId);
    this.version = version;
  }

  /**
   * Answers my persistence version, which can be used
   * to implement optimistic concurrency conflict detection.
   * 
   * @return int
   */
  public int version() {
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
}
