// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store;

/**
 * Indicates the format of the stored data, either Binary or Text.
 */
public enum DataFormat {
  /**
   * The binary format indicator.
   */
  Binary {
    @Override public boolean isBinary() { return true; }
  },
  /**
   * The native format indicator.
   */
  Native {
    @Override public boolean isNative() { return true; }
  },
  /**
   * The text format indicator.
   */
  Text {
    @Override public boolean isText() { return true; }
  };

  /**
   * Answer whether or not this is a Binary indicator.
   * @return boolean
   */
  public boolean isBinary() { return false; }

  /**
   * Answer whether or not this is a Native indicator.
   * @return boolean
   */
  public boolean isNative() { return false; }

  /**
   * Answer whether or not this is a Text indicator.
   * @return boolean
   */
  public boolean isText() { return false; }
};
