// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio;

import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class Metadata implements Comparable<Metadata> {
  @Deprecated
  public final static Object EmptyObject = new Object() { @Override public String toString() { return "(empty)"; } };

  // Object is deprecated and will be removed in future versions. Use the Map of properties instead.
  @Deprecated
  public final Object object;
  public final Map<String, String> properties;
  public final String operation;
  public final String value;

  public static Metadata nullMetadata() {
    return new Metadata(Collections.emptyMap(), "", "");
  }

  @Deprecated
  public static Metadata withObject(final Object object) {
    return new Metadata(object, "", "");
  }

  public static Metadata withProperties(final Map<String, String> properties) {
    return new Metadata(properties, "", "");
  }

  public static Metadata withOperation(final String operation) {
    return new Metadata(EmptyObject, "", operation);
  }

  public static Metadata withValue(final String value) {
    return new Metadata(EmptyObject, value, "");
  }

  public static Metadata with(final String value, final String operation) {
    return new Metadata(value, operation);
  }

  public static Metadata with(final Map<String, String> properties, final String value, final String operation) {
    return new Metadata(properties, value, operation);
  }

  public static Metadata with(final Map<String, String> properties, final String value, final Class<?> operationType) {
    return with(properties, value, operationType, true);
  }

  public static Metadata with(final Map<String, String> properties, final String value, final Class<?> operationType, final boolean compact) {
    final String operation = compact ? operationType.getSimpleName() : operationType.getName();
    return new Metadata(properties, value, operation);
  }

  @Deprecated
  public static Metadata with(final Object object, final String value, final String operation) {
    return new Metadata(object, value, operation);
  }

  @Deprecated
  public static Metadata with(final Object object, final String value, final Class<?> operationType) {
    return with(object, value, operationType, true);
  }

  @Deprecated
  public static Metadata with(final Object object, final String value, final Class<?> operationType, final boolean compact) {
    final String operation = compact ? operationType.getSimpleName() : operationType.getName();
    return new Metadata(object, value, operation);
  }

  @Deprecated
  public Metadata(final Object object, final String value, final String operation) {
    if (object == null) this.object = EmptyObject; else this.object = object;

    if (value == null) this.value = ""; else this.value = value;

    if (operation == null) this.operation = ""; else this.operation = operation;

    this.properties = Collections.emptyMap();
  }

  public Metadata(final Map<String, String> properties, final String value, final String operation) {
    this.object = EmptyObject;

    if (properties == null) this.properties = Collections.emptyMap(); else this.properties = properties;

    if (value == null) this.value = ""; else this.value = value;

    if (operation == null) this.operation = ""; else this.operation = operation;
  }

  public Metadata(final String value, final String operation) {
    this(Collections.emptyMap(), value, operation);
  }

  public Metadata() {
    this(Collections.emptyMap(), "", "");
  }

  @Deprecated
  public boolean hasObject() {
    return object != EmptyObject;
  }

  public boolean hasProperties() {
    return !properties.isEmpty();
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

  @Deprecated
  public Object object() {
    return object;
  }

  @Deprecated
  public Optional<Object> optionalObject() {
    return hasObject() ? Optional.of(object) : Optional.empty();
  }

  public String operation() {
    return operation;
  }

  public String value() {
    return value;
  }

  @Deprecated
  @SuppressWarnings("unchecked")
  public <T> T typedObject() {
    return (T) object;
  }

  @Override
  public int compareTo(final Metadata other) {
    if (!this.object.equals(other.object)) return 1;
    if (!this.properties.equals(other.properties)) return 1;
    return Comparator
            .comparing((Metadata m) -> m.value)
            .thenComparing(m -> m.operation)
            .compare(this, other);
  }

  @Override
  public int hashCode() {
    return 31 * value.hashCode() + operation.hashCode() + properties.hashCode() + object.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != this.getClass()) {
      return false;
    }

    final Metadata otherMetadata = (Metadata) other;

    return value.equals(otherMetadata.value) &&
            operation.equals(otherMetadata.operation) &&
            properties.equals(otherMetadata.properties) &&
            object.equals(otherMetadata.object);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() +
            "[value=" + value + " operation=" + operation + " properties=" + properties + " object=" + object + "]";
  }
}
