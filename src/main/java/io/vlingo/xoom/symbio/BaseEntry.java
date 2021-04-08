// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio;

import java.util.Comparator;

/**
 * The abstract base class of all journal entry types.
 *
 * @param <T> the concrete type of {@code Entry<T>} stored and read, which maybe be String, byte[], or Object
 *
 * @see BinaryEntry ObjectEntry TextEntry NullEntry
 */
public abstract class BaseEntry<T> implements Entry<T> {

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
  private final T entryData;

  /**
   * My version that is the state version with which I am associated.
   */
  private final int entryVersion;

  /**
   * My associated {@code Metadata} if any.
   */
  private final Metadata metadata;

  /**
   * My String type that is the fully-qualified class name of
   * the original entry type.
   */
  private final String type;

  /**
   * My int type version, which may be a semantic version or
   * sequential version of my type.
   */
  private final int typeVersion;

  public BaseEntry(final String id, final Class<?> type, final int typeVersion, final T entryData, final int entryVersion, final Metadata metadata) {
    if (id == null) throw new IllegalArgumentException("Entry id must not be null.");
    this.id = id;
    if (type == null) throw new IllegalArgumentException("Entry type must not be null.");
    this.type = type.getName();
    if (typeVersion <= 0) throw new IllegalArgumentException("Entry typeVersion must be greater than 0.");
    this.typeVersion = typeVersion;
    if (entryData == null) throw new IllegalArgumentException("Entry entryData must not be null.");
    this.entryData = entryData;
    this.entryVersion = entryVersion;
    if (metadata == null) throw new IllegalArgumentException("Entry metadata must not be null.");
    this.metadata = metadata;
  }

  public BaseEntry(final String id, final Class<?> type, final int typeVersion, final T entryData, final Metadata metadata) {
    this(id, type, typeVersion, entryData, DefaultVersion, metadata);
  }

  protected BaseEntry(final String id, final Class<?> type, final int typeVersion, final T entryData) {
    this(id, type, typeVersion, entryData, DefaultVersion, Metadata.nullMetadata());
  }

  public BaseEntry(final Class<?> type, final int typeVersion, final T entryData, final Metadata metadata) {
    this(UnknownId, type, typeVersion, entryData, DefaultVersion, metadata);
  }

  /* @see io.vlingo.xoom.symbio.Entry#id() */
  @Override
  public String id() {
    return id;
  }

  /* @see io.vlingo.xoom.symbio.Entry#entryData() */
  @Override
  public T entryData() {
    return entryData;
  }

  /* @see io.vlingo.xoom.symbio.Entry#entryVersion() */
  @Override
  public int entryVersion() {
    return entryVersion;
  }

  /* @see io.vlingo.xoom.symbio.Entry#metadata() */
  @Override
  public Metadata metadata() {
    return metadata;
  }

  /* @see io.vlingo.xoom.symbio.Entry#type() */
  @Override
  public String typeName() {
    return type;
  }

  /* @see io.vlingo.xoom.symbio.Entry#typeVersion() */
  @Override
  public int typeVersion() {
    return typeVersion;
  }

  public BinaryEntry asBinaryEntry() {
    return (BinaryEntry) this;
  }

  @SuppressWarnings("unchecked")
  public ObjectEntry<T> asObjectEntry() {
    return (ObjectEntry<T>) this;
  }

  public TextEntry asTextEntry() {
    return (TextEntry) this;
  }

  /* @see io.vlingo.xoom.symbio.Entry#hasMetadata() */
  @Override
  public boolean hasMetadata() {
    return !metadata.isEmpty();
  }

  /**
   * Returns true if I am an instance of {@link BinaryEntry}.
   * The default is to answer false.
   *
   * @return true if I am an instance of {@link BinaryEntry}
   */
  public boolean isBinary() {
    return false;
  }

  /* @see io.vlingo.xoom.symbio.Entry#isEmpty() */
  @Override
  public boolean isEmpty() {
    return false;
  }

  /* @see io.vlingo.xoom.symbio.Entry#isNull() */
  @Override
  public boolean isNull() {
    return false;
  }

  /**
   * Returns true if I am an instance of {@link ObjectEntry}.
   * The default is to answer false.
   *
   * @return true if I am an instance of {@link ObjectEntry}
   */
  public boolean isObject() {
    return false;
  }

  /**
   * Returns true if I am an instance of {@link TextEntry}.
   * The default is to answer false.
   *
   * @return true if I am an instance of {@link TextEntry}
   */
  public boolean isText() {
    return false;
  }

  /**
   * @see io.vlingo.xoom.symbio.Entry#typed()
   */
  @Override
  public <C> Class<C> typed() {
    return Entry.typed(type);
  }

  /**
   * Answers the difference between me and other per {@code Comparable<Entry<T>>}.
   *
   * @param other the {@code Entry<T>} to compare to myself
   *
   * @return int
   */
  @Override
  public int compareTo(Entry<T> other) {
    final BaseEntry<T> that = (BaseEntry<T>) other;
    final int dataDiff = compareData(this, that);
    if (dataDiff != 0) return dataDiff;

    return Comparator
      .comparing((BaseEntry<T> s) -> s.id)
      .thenComparing(s -> s.type)
      .thenComparingInt(s -> s.typeVersion)
      .thenComparing(s -> s.metadata)
      .compare(this, that);
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
    return id.equals(((BaseEntry<?>) other).id);
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
            " entryVersion=" + entryVersion +
            " metadata=" + metadata + "]";
  }

  /**
   * FOR INTERNAL USE ONLY.
   * @param id the String to set as my id
   */
  public void __internal__setId(final String id) {
    this.id = id;
  }

  private int compareData(final BaseEntry<T> state1, final BaseEntry<T> state2) {
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
  public static final class BinaryEntry extends BaseEntry<byte[]> {
    public BinaryEntry(final String id, final Class<?> type, final int typeVersion, final byte[] entryData, final int entryVersion, final Metadata metadata) {
      super(id, type, typeVersion, entryData, entryVersion, metadata);
    }

    public BinaryEntry(final String id, final Class<?> type, final int typeVersion, final byte[] entryData, final Metadata metadata) {
      super(id, type, typeVersion, entryData, metadata);
    }

    public BinaryEntry(final String id, final Class<?> type, final int typeVersion, final byte[] entryData) {
      super(id, type, typeVersion, entryData);
    }

    public BinaryEntry(final Class<?> type, final int typeVersion, final byte[] entryData, final int entryVersion, final Metadata metadata) {
      super(UnknownId, type, typeVersion, entryData, entryVersion, metadata);
    }

    public BinaryEntry(final Class<?> type, final int typeVersion, final byte[] entryData, final Metadata metadata) {
      super(UnknownId, type, typeVersion, entryData, metadata);
    }

    public BinaryEntry() {
      super(UnknownId, Object.class, 1, BaseEntry.EmptyBytesData, Metadata.nullMetadata());
    }

    @Override
    public boolean isBinary() {
      return true;
    }

    @Override
    public boolean isEmpty() {
      return entryData().length == 0;
    }

    @Override
    public Entry<byte[]> withId(final String id) {
      return new BinaryEntry(id, typed(), typeVersion(), entryData());
    }
  }

  /**
   * The Object form of {@code Entry<T>}.
   */
  public static final class ObjectEntry<T> extends BaseEntry<Object> {
    public ObjectEntry(final String id, final Class<?> type, final int typeVersion, final String entryData, final int entryVersion, final Metadata metadata) {
      super(id, type, typeVersion, entryData, entryVersion, metadata);
    }

    public ObjectEntry(final String id, final Class<?> type, final int typeVersion, final T entryData, final Metadata metadata) {
      super(id, type, typeVersion, entryData, metadata);
    }

    public ObjectEntry(final String id, final Class<?> type, final int typeVersion, final T entryData, final int dataVersion) {
      super(id, type, typeVersion, entryData);
    }

    public ObjectEntry(final Class<?> type, final int typeVersion, final String entryData, final int entryVersion, final Metadata metadata) {
      super(UnknownId, type, typeVersion, entryData, entryVersion, metadata);
    }

    public ObjectEntry(final Class<?> type, final int typeVersion, final T entryData, final Metadata metadata) {
      super(UnknownId, type, typeVersion, entryData, metadata);
    }

    public ObjectEntry() {
      super(UnknownId, Object.class, 1, BaseEntry.EmptyObjectData, Metadata.nullMetadata());
    }

    @Override
    public boolean isEmpty() {
      return entryData() == BaseEntry.EmptyObjectData;
    }

    @Override
    public boolean isObject() {
      return true;
    }

    @Override
    public Entry<Object> withId(final String id) {
      return new ObjectEntry<>(id, typed(), typeVersion(), entryData(), 1);
    }
  }

  /**
   * The text String form of {@code Entry<T>}.
   */
  public static final class TextEntry extends BaseEntry<String> {
    public TextEntry(final String id, final Class<?> type, final int typeVersion, final String entryData, final int entryVersion, final Metadata metadata) {
      super(id, type, typeVersion, entryData, entryVersion, metadata);
    }

    public TextEntry(final String id, final Class<?> type, final int typeVersion, final String entryData, final Metadata metadata) {
      super(id, type, typeVersion, entryData, metadata);
    }

    public TextEntry(final String id, final Class<?> type, final int typeVersion, final String entryData) {
      super(id, type, typeVersion, entryData);
    }

    public TextEntry(final Class<?> type, final int typeVersion, final String entryData, final int entryVersion, final Metadata metadata) {
      super(UnknownId, type, typeVersion, entryData, entryVersion, metadata);
    }

    public TextEntry(final Class<?> type, final int typeVersion, final String entryData, final Metadata metadata) {
      super(UnknownId, type, typeVersion, entryData, metadata);
    }

    public TextEntry() {
      super(UnknownId, Object.class, 1, BaseEntry.EmptyTextData, Metadata.nullMetadata());
    }

    @Override
    public boolean isEmpty() {
      return entryData().isEmpty();
    }

    @Override
    public boolean isText() {
      return true;
    }

    @Override
    public Entry<String> withId(final String id) {
      return new TextEntry(id, typed(), typeVersion(), entryData());
    }
  }

  /**
   * The Null Object form of {@code Entry<T>}.
   */
  public static final class NullEntry<T> extends BaseEntry<T> {
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

    @Override
    public Entry<T> withId(final String id) {
      return this;
    }
  }
}
