// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio;

import io.vlingo.xoom.symbio.store.StoredTypes;

import java.util.Collections;
import java.util.List;

/**
 * Entry represents a journal entry
 *
 * @param <T> the concrete type of {@code Entry<T>} stored and read,
 * which may be a String, byte[] or Object
 */
public interface Entry<T> extends Comparable<Entry<T>> {

  /** The default state version number with which I am associated. */
  static final int DefaultVersion = -1;

  /**
   * Answer an empty {@code List<Entry<T>>}.
   * @param <T> the type used in {@code Entry<T>}
   * @return {@code List<Entry<T>>}
   */
  public static <T> List<Entry<T>> none() {
    return Collections.emptyList();
  }

  /**
   * Answers the Class&lt;C&gt; of the given {@code type}.
   * @param type the String representation of the class
   * @param <C> the Class type
   * @return Class of C
   */
  @SuppressWarnings("unchecked")
  public static <C> Class<C> typed(final String type) {
    try {
      return (Class<C>) StoredTypes.forName(type);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot get class for type: " + type);
    }
  }

  /**
   * Answers my id;
   * @return String
   */
  String id();

  /**
   * Answers my entry data
   *
   * @return T my entry data
   */
  T entryData();

  /**
   * Answer my entry version that is the state version with which I am associated.
   * Answers {@code DefaultVersion} if not overridden.
   * @return int
   */
  default int entryVersion() { return DefaultVersion; }

  /**
   * Returns my associated (possibly null) Metadata
   *
   * @return my associated Metadata or null
   */
  Metadata metadata();

  /**
   * Answer the simple name of my {@code type}.
   * @return String
   */
  default String simpleTypeName() {
    final String typeName = typeName();
    final int index = Math.max(typeName.lastIndexOf('.'), typeName.lastIndexOf('$'));
    return typeName.substring(index + 1);
  }

  /**
   * Returns my typeName.
   *
   * @return my type
   */
  String typeName();

  /**
   * Returns my type.
   *
   * @return my type
   */
  default String type() {
    return typeName();
  }

  /**
   * Returns my type version.
   *
   * @return my type version
   */
  int typeVersion();

  /**
   * Answers whether or not I have non-empty Metadata.
   * @return boolean
   */
  boolean hasMetadata();

  /**
   * Answers whether or not I am a completely empty Entry.
   * @return boolean
   */
  boolean isEmpty();

  /**
   * Answers whether or not I am a NullEntry.
   * @return boolean
   */
  boolean isNull();

  /**
   * Answers the Class&lt;C&gt; of my type.
   * @param <C> the Class type
   * @return Class of C
   */
  <C> Class<C> typed();

  /**
   * Answer a copy of myself with the {@code id}.
   * @param id the String identity to assign to my copy
   * @return {@code Entry<T>}
   */
  public abstract Entry<T> withId(final String id);
}
