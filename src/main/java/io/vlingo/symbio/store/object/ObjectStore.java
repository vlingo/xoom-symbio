// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.object;

/**
 * An object storage in which {@link StateObject}s are self defined,
 * including their identity. This is often thought of as object-relational
 * mapping, which certainly can be and is supported, but is not limited to such.
 */
public interface ObjectStore extends ObjectStoreReader, ObjectStoreWriter {

  /**
   * Close me.
   */
  void close();

}
