// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import java.util.Comparator;

/**
 * The abstract base class of all journal entry types.
 *
 * @param <T> the concrete type of {@code Entry<T>} stored and read, which maybe be String, byte[], or Object
 *
 * @see BinaryEntry ObjectEntry TextEntry NullEntry
 */
public abstract class Entry<T> implements Comparable<Entry<T>> {
  private static final byte[] EmptyBytesData = new byte[0];
  private static final Object EmptyObjectData = new Object() { @Override public String toString() { return "(empty)"; } };
  private static final String EmptyTextData = "";
  private static final String UnknownId = "";

  /**
   * My {@code String} id that is unique within the {@code Journal<T>} where persisted,
   * and is (generally) assigned by the journal.
   */
  private String id;

  /**
   * My data representation of the entry, generally serialized as
   * String, byte[], or Object.
   */
  public final T entryData;

  /**
   * My associated {@code Metadata} if any.
   */
  public final Metadata metadata;

  /**
   * My String type that is the fully-qualified class name of
   * the original entry type.
   */
  public final String type;

  /**
   * My int type version, which may be a semantic version or
   * sequential version of my type.
   */
  public final int typeVersion;

  public Entry(final String id, final Class<?> type, final int typeVersion, final T entryData, final Metadata metadata) {
    if (id == null) throw new IllegalArgumentException("Entry id must not be null.");
    this.id = id;
    if (type == null) throw new IllegalArgumentException("Entry type must not be null.");
    this.type = type.getName();
    if (typeVersion <= 0) throw new IllegalArgumentException("Entry typeVersion must be greater than 0.");
    this.typeVersion = typeVersion;
    if (entryData == null) throw new IllegalArgumentException("Entry entryData must not be null.");
    this.entryData = entryData;
    if (metadata == null) throw new IllegalArgumentException("Entry metadata must not be null.");
    this.metadata = metadata;
  }

  protected Entry(final String id, final Class<?> type, final int typeVersion, final T entryData) {
    this(id, type, typeVersion, entryData, Metadata.nullMetadata());
  }

  public Entry(final Class<?> type, final int typeVersion, final T entryData, final Metadata metadata) {
    this(UnknownId, type, typeVersion, entryData, metadata);
  }

  /**
   * Answers my id;
   * @return String
   */
  public String id() {
    return id;
  }

  /**
   * Answers myself as a BinaryEntry.
   * @return BinaryEntry
   */
  public BinaryEntry asBinaryEntry() {
    return (BinaryEntry) this;
  }

  /**
   * Answers myself as an ObjectEntry.
   * @return ObjectEntry
   */
  @SuppressWarnings("unchecked")
  public ObjectEntry<T> asObjectEntry() {
    return (ObjectEntry<T>) this;
  }

  /**
   * Answers myself as a TextEntry.
   * @return TextEntry
   */
  public TextEntry asTextEntry() {
    return (TextEntry) this;
  }

  /**
   * Answers whether or not I have non-empty Metadata.
   * @return boolean
   */
  public boolean hasMetadata() {
    return !metadata.isEmpty();
  }

  /**
   * Answers whether or not I am a BinaryEntry.
   * @return boolean
   */
  public boolean isBinary() {
    return false;
  }

  /**
   * Answers whether or not I am a completely empty Entry.
   * @return boolean
   */
  public boolean isEmpty() {
    return false;
  }

  /**
   * Answers whether or not I am a NullEntry.
   * @return boolean
   */
  public boolean isNull() {
    return false;
  }

  /**
   * Answers whether or not I am an ObjectEntry.
   * @return boolean
   */
  public boolean isObject() {
    return false;
  }

  /**
   * Answers whether or not I am a TextEntry.
   * @return boolean
   */
  public boolean isText() {
    return false;
  }

  /**
   * Answers the Class&lt;C&gt; of my type.
   * @param <C> the Class type
   * @return Class of C
   */
  @SuppressWarnings("unchecked")
  public <C> Class<C> typed() {
    try {
      return (Class<C>) Class.forName(type);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot get class for type: " + type);
    }
  }

  /**
   * Answers the difference between me and other per {@code Comparable<Entry<T>>}.
   *
   * @param other the {@code Entry<T>} to compare to myself
   *
   * @return int
   */
  @Override
  public int compareTo(final Entry<T> other) {
    final int dataDiff = compareData(this, other);
    if (dataDiff != 0) return dataDiff;

    return Comparator
      .comparing((Entry<T> s) -> s.id)
      .thenComparing(s -> s.type)
      .thenComparingInt(s -> s.typeVersion)
      .thenComparing(s -> s.metadata)
      .compare(this, other);
  }

  /**
   * Answers my hash code.
   * @return int
   */
  @Override
  public int hashCode() {
    return 31 * id.hashCode();
  }

  /**
   * Answers whether or not I am equal to other.
   * @param other the Object to compare to my equality
   * @return boolean
   */
  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != this.getClass()) {
      return false;
    }
    return id.equals(((Entry<?>) other).id);
  }

  /**
   * Answers myself as a String.
   * @return String
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() +
            "[id=" + id + " type=" + type + " typeVersion=" + typeVersion +
            " entryData=" + (isText() || isObject() ? entryData.toString() : "(binary)") +
            " metadata=" + metadata + "]";
  }

  public void __internal__setId(final String id) {
    this.id = id;
  }

  private int compareData(final Entry<T> state1, final Entry<T> state2) {
    if (state1.isText() && state2.isText()) {
      return ((String) state1.entryData).compareTo((String) state2.entryData);
    } else if (state1.isBinary() && state2.isBinary()) {
      final byte[] data1 = (byte[]) state1.entryData;
      final byte[] data2 = (byte[]) state2.entryData;
      if (data1.length == data2.length) {
        for (int idx = 0; idx < data1.length; ++idx) {
          if (data1[idx] != data2[idx]) {
            return 1;
          }
        }
        return 0;
      }
      return 1;
    }
    return 1;
  }

  /**
   * The byte[] form of {@code Entry<T>}.
   */
  public static final class BinaryEntry extends Entry<byte[]> {
    public BinaryEntry(final String id, final Class<?> type, final int typeVersion, final byte[] entryData, final Metadata metadata) {
      super(id, type, typeVersion, entryData, metadata);
    }

    public BinaryEntry(final String id, final Class<?> type, final int typeVersion, final byte[] entryData) {
      super(id, type, typeVersion, entryData);
    }

    public BinaryEntry(final Class<?> type, final int typeVersion, final byte[] entryData, final Metadata metadata) {
      super(UnknownId, type, typeVersion, entryData, metadata);
    }

    public BinaryEntry() {
      super(UnknownId, Object.class, 1, Entry.EmptyBytesData, Metadata.nullMetadata());
    }

    @Override
    public boolean isBinary() {
      return true;
    }

    @Override
    public boolean isEmpty() {
      return entryData.length == 0;
    }
  }

  /**
   * The Object form of {@code Entry<T>}.
   */
  public static final class ObjectEntry<T> extends Entry<Object> {
    public ObjectEntry(final String id, final Class<?> type, final int typeVersion, final T entryData, final Metadata metadata) {
      super(id, type, typeVersion, entryData, metadata);
    }

    public ObjectEntry(String id, Class<?> type, int typeVersion, T entryData, int dataVersion) {
      super(id, type, typeVersion, entryData);
    }

    public ObjectEntry(final Class<?> type, final int typeVersion, final T entryData, final Metadata metadata) {
      super(UnknownId, type, typeVersion, entryData, metadata);
    }

    @SuppressWarnings("unchecked")
    public ObjectEntry() {
      super(UnknownId, Object.class, 1, Entry.EmptyObjectData, Metadata.nullMetadata());
    }

    @Override
    public boolean isEmpty() {
      return entryData == Entry.EmptyObjectData;
    }

    @Override
    public boolean isObject() {
      return true;
    }
  }

  /**
   * The text String form of {@code Entry<T>}.
   */
  public static final class TextEntry extends Entry<String> {
    public TextEntry(final String id, final Class<?> type, final int typeVersion, final String entryData, final Metadata metadata) {
      super(id, type, typeVersion, entryData, metadata);
    }

    public TextEntry(final String id, final Class<?> type, final int typeVersion, final String entryData) {
      super(id, type, typeVersion, entryData);
    }

    public TextEntry(final Class<?> type, final int typeVersion, final String entryData, final Metadata metadata) {
      super(UnknownId, type, typeVersion, entryData, metadata);
    }

    public TextEntry() {
      super(UnknownId, Object.class, 1, Entry.EmptyTextData, Metadata.nullMetadata());
    }

    @Override
    public boolean isEmpty() {
      return entryData.isEmpty();
    }

    @Override
    public boolean isText() {
      return true;
    }
  }

  /**
   * The Null Object form of {@code Entry<T>}.
   */
  public static final class NullEntry<T> extends Entry<T> {
    public static NullEntry<byte[]> Binary = new NullEntry<>(EmptyBytesData);
    public static NullEntry<Object> Object = new NullEntry<>(EmptyObjectData);
    public static NullEntry<String> Text = new NullEntry<>(EmptyTextData);

    private NullEntry(final T entryData) {
      super(UnknownId, Object.class, 1, entryData, Metadata.nullMetadata());
    }

    @Override
    public boolean isEmpty() {
      return true;
    }

    @Override
    public boolean isNull() {
      return true;
    }
  }
}
