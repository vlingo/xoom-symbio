// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.symbio.store.object;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.vlingo.symbio.Source;
/**
 * StateSources records the mapping between {@link StateObject} and the
 * {@link io.vlingo.symbio.Source sources}  of its creation or mutation.
 */
public class StateSources<T extends StateObject, E> {

  private T stateObject;
  private List<Source<E>> sources;

  public StateSources(final T stateObject) {
    this(stateObject, Source.none());
  }

  public StateSources(final T stateObject, final List<Source<E>> sources) {
    super();
    if (stateObject == null) throw new IllegalArgumentException("stateObject is required");
    this.stateObject = stateObject;
    if (sources == null) throw new IllegalArgumentException("sources is required");
    this.sources = sources;
  }

  public static <T extends StateObject, E> StateSources<T,E> of(T stateObject) {
    return new StateSources<T,E>(stateObject, Source.none());
  }

  public static <T extends StateObject, E> StateSources<T,E> of(T stateObject, Source<E> source) {
    return new StateSources<T,E>(stateObject, Collections.singletonList(source));
  }

  public static <T extends StateObject, E> StateSources<T,E> of(T stateObject, List<Source<E>> sources) {
    return new StateSources<T,E>(stateObject, sources);
  }

  public T stateObject() {
    return stateObject;
  }

  public List<Source<E>> sources() {
    return Collections.unmodifiableList(sources);
  }

  @Override
  @SuppressWarnings("rawtypes")
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StateSources that = (StateSources) o;
    return stateObject.equals(that.stateObject) &&
      sources.equals(that.sources);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stateObject, sources);
  }

  @Override
  public String toString() {
    return "StateSources{" +
      "stateObject=" + stateObject +
      ", sources=" + sources +
      '}';
  }
}
