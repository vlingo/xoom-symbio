// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import io.vlingo.symbio.BaseEntry.BinaryEntry;
import io.vlingo.symbio.BaseEntry.ObjectEntry;
import io.vlingo.symbio.BaseEntry.TextEntry;
/**
 * Entry represents a journal entry
 *
 * @param <T> the concrete type of {@code Entry<T>} stored and read,
 * which may be a String, byte[] or Object
 */
public interface Entry<T> extends Comparable<Entry<T>> {

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
   * Returns my associated (possibly null) Metadata
   *
   * @return my associated Metadata or null
   */
  Metadata metadata();

  /**
   * Returns my type.
   *
   * @return my type
   */
  String type();

  /**
   * Returns my type version.
   *
   * @return my type version
   */
  int typeVersion();

  /**
   * Answers myself as a BinaryEntry.
   * @return BinaryEntry
   */
  BinaryEntry asBinaryEntry();

  /**
   * Answers myself as an ObjectEntry.
   * @return ObjectEntry
   */
  ObjectEntry<T> asObjectEntry();

  /**
   * Answers myself as a TextEntry.
   * @return TextEntry
   */
  TextEntry asTextEntry();

  /**
   * Answers whether or not I have non-empty Metadata.
   * @return boolean
   */
  boolean hasMetadata();

  /**
   * Answers whether or not I am a BinaryEntry.
   * @return boolean
   */
  boolean isBinary();

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
   * Answers whether or not I am an ObjectEntry.
   * @return boolean
   */
  boolean isObject();

  /**
   * Answers whether or not I am a TextEntry.
   * @return boolean
   */
  boolean isText();

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
