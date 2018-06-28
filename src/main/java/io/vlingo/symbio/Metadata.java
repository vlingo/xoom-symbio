// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

public class Metadata<T> {
  public final static Object EmptyObject = new Object();
  public final static Metadata<?> Null = new Metadata<>();

  public final T object;
  public final String operation;
  public final String value;

  public static <T> Metadata<T> withObject(final T object) {
    return new Metadata<>(object, "", "");
  }

  public static <T> Metadata<T> withOperation(final String operation) {
    return new Metadata<>(emptyObject(), "", operation);
  }

  public static <T> Metadata<T> withValue(final String value) {
    return new Metadata<>(emptyObject(), value, "");
  }

  public static<T> Metadata<T> with(final String value, final String operation) {
    return new Metadata<>(emptyObject(), value, operation);
  }

  public static<T> Metadata<T> with(final T object, final String value, final String operation) {
    return new Metadata<>(object, value, operation);
  }

  @SuppressWarnings("unchecked")
  private static <T> T emptyObject() {
    return (T) EmptyObject;
  }

  public Metadata(final T object, final String value, final String operation) {
    if (object == null) throw new IllegalArgumentException("Metadata object must not be null.");
    this.object = object;

    if (value == null) throw new IllegalArgumentException("Metadata value must not be null.");
    this.value = value;

    if (operation == null) throw new IllegalArgumentException("Metadata operation must not be null.");
    this.operation = operation;
  }

  public Metadata(final String value, final String operation) {
    this(emptyObject(), value, operation);
  }

  public Metadata() {
    this(emptyObject(), "", "");
  }

  public boolean hasObject() {
    return object != emptyObject();
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
