// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

public class Metadata {
  public final static Metadata Null = new Metadata();

  public final String operation;
  public final String value;

  public static Metadata withOperation(final String operation) {
    return new Metadata("", operation);
  }

  public static Metadata withValue(final String value) {
    return new Metadata(value, "");
  }

  public static Metadata with(final String value, final String operation) {
    return new Metadata(value, operation);
  }

  public Metadata(final String value, final String operation) {
    if (value == null) throw new IllegalArgumentException("Metadata value must not be null.");
    this.value = value;
    if (operation == null) throw new IllegalArgumentException("Metadata operation must not be null.");
    this.operation = operation;
  }

  public Metadata() {
    this("", "");
  }

  public boolean hasOperation() {
    return !operation.isEmpty();
  }

  public boolean hasValue() {
    return !value.isEmpty();
  }

  public boolean isEmpty() {
    return !hasOperation() && !hasValue();
  }
}
