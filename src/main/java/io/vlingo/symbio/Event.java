// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import java.util.Comparator;

/**
 * The abstract base class of all journal event types.
 *
 * @param <T> the concrete type of Event&lt;T&gt; stored and read, which maybe be String, byte[], or Object
 * 
 * @see BinaryEvent ObjectEvent TextEvent NullEvent
 */
public abstract class Event<T> implements Comparable<Event<T>> {
  private static final byte[] EmptyBytesData = new byte[0];
  private static final Object EmptyObjectData = new Object() { @Override public String toString() { return "(empty)"; } };
  private static final String EmptyTextData = "";
  private static final String UnknownId = "";

  /**
   * My String id that is unique within the EventJournal&lt;T&gt; where persisted,
   * and is (generally) assigned by the journal.
   */
  public final String id;

  /**
   * My data representation of the event, generally serialized as
   * String, byte[], or Object.
   */
  public final T eventData;

  /**
   * My associated Metadata if any.
   */
  public final Metadata metadata;

  /**
   * My String type that is the fully-qualified class name of
   * the original event type.
   */
  public final String type;

  /**
   * My int type version, which may be a semantic version or
   * sequential version of my type.
   */
  public final int typeVersion;

  public Event(final String id, final Class<?> type, final int typeVersion, final T eventData, final Metadata metadata) {
    if (id == null) throw new IllegalArgumentException("Event id must not be null.");
    this.id = id;
    if (type == null) throw new IllegalArgumentException("Event type must not be null.");
    this.type = type.getName();
    if (typeVersion <= 0) throw new IllegalArgumentException("Event typeVersion must be greater than 0.");
    this.typeVersion = typeVersion;
    if (eventData == null) throw new IllegalArgumentException("Event eventData must not be null.");
    this.eventData = eventData;
    if (metadata == null) throw new IllegalArgumentException("Event metadata must not be null.");
    this.metadata = metadata;
  }

  protected Event(final String id, final Class<?> type, final int typeVersion, final T eventData) {
    this(id, type, typeVersion, eventData, Metadata.nullMetadata());
  }

  public Event(final Class<?> type, final int typeVersion, final T eventData, final Metadata metadata) {
    this(UnknownId, type, typeVersion, eventData, metadata);
  }

  /**
   * Answers myself as a BinaryEvent.
   * @return BinaryEvent
   */
  public BinaryEvent asBinaryEvent() {
    return (BinaryEvent) this;
  }

  /**
   * Answers myself as an ObjectEvent.
   * @return ObjectEvent
   */
  @SuppressWarnings("unchecked")
  public ObjectEvent<T> asObjectEvent() {
    return (ObjectEvent<T>) this;
  }

  /**
   * Answers myself as a TextEvent.
   * @return TextEvent
   */
  public TextEvent asTextEvent() {
    return (TextEvent) this;
  }

  /**
   * Answers whether or not I have non-empty Metadata.
   * @return boolean
   */
  public boolean hasMetadata() {
    return !metadata.isEmpty();
  }

  /**
   * Answers whether or not I am a BinaryEvent.
   * @return boolean
   */
  public boolean isBinary() {
    return false;
  }

  /**
   * Answers whether or not I am a completely empty Event.
   * @return boolean
   */
  public boolean isEmpty() {
    return false;
  }

  /**
   * Answers whether or not I am a NullEvent.
   * @return boolean
   */
  public boolean isNull() {
    return false;
  }

  /**
   * Answers whether or not I am an ObjectEvent.
   * @return boolean
   */
  public boolean isObject() {
    return false;
  }

  /**
   * Answers whether or not I am a TextEvent.
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
   * Answers the difference between me and other per Comparable&lt;Event&lt;T&gt;&gt;.
   * 
   * @param other the Event&lt;T&gt; to compare to myself
   * 
   * @return int
   */
  @Override
  public int compareTo(final Event<T> other) {
    final int dataDiff = compareData(this, other);
    if (dataDiff != 0) return dataDiff;

    return Comparator
      .comparing((Event<T> s) -> s.id)
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
    return id.equals(((Event<?>) other).id);
  }

  /**
   * Answers myself as a String.
   * @return String
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() +
            "[id=" + id + " type=" + type + " typeVersion=" + typeVersion +
            " eventData=" + (isText() || isObject() ? eventData.toString() : "(binary)") +
            " metadata=" + metadata + "]";
  }

  private int compareData(final Event<T> state1, final Event<T> state2) {
    if (state1.isText() && state2.isText()) {
      return ((String) state1.eventData).compareTo((String) state2.eventData);
    } else if (state1.isBinary() && state2.isBinary()) {
      final byte[] data1 = (byte[]) state1.eventData;
      final byte[] data2 = (byte[]) state2.eventData;
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
   * The byte[] form of Event&lt;T&gt;.
   */
  public static final class BinaryEvent extends Event<byte[]> {
    public BinaryEvent(final String id, final Class<?> type, final int typeVersion, final byte[] eventData, final Metadata metadata) {
      super(id, type, typeVersion, eventData, metadata);
    }

    public BinaryEvent(final String id, final Class<?> type, final int typeVersion, final byte[] eventData) {
      super(id, type, typeVersion, eventData);
    }

    public BinaryEvent() {
      super(UnknownId, Object.class, 1, Event.EmptyBytesData, Metadata.nullMetadata());
    }

    @Override
    public boolean isBinary() {
      return true;
    }

    @Override
    public boolean isEmpty() {
      return eventData.length == 0;
    }
  }

  /**
   * The Object form of Event&lt;T&gt;.
   */
  public static final class ObjectEvent<T> extends Event<Object> {
    public ObjectEvent(final String id, final Class<?> type, final int typeVersion, final T eventData, final Metadata metadata) {
      super(id, type, typeVersion, eventData, metadata);
    }

    public ObjectEvent(String id, Class<?> type, int typeVersion, T eventData, int dataVersion) {
      super(id, type, typeVersion, eventData);
    }

    @SuppressWarnings("unchecked")
    public ObjectEvent() {
      super(UnknownId, Object.class, 1, (T) Event.EmptyObjectData, Metadata.nullMetadata());
    }

    @Override
    public boolean isEmpty() {
      return eventData == Event.EmptyObjectData;
    }

    @Override
    public boolean isObject() {
      return true;
    }
  }

  /**
   * The text String form of Event&lt;T&gt;.
   */
  public static final class TextEvent extends Event<String> {
    public TextEvent(final String id, final Class<?> type, final int typeVersion, final String eventData, final Metadata metadata) {
      super(id, type, typeVersion, eventData, metadata);
    }

    public TextEvent(final String id, final Class<?> type, final int typeVersion, final String eventData) {
      super(id, type, typeVersion, eventData);
    }

    public TextEvent() {
      super(UnknownId, Object.class, 1, Event.EmptyTextData, Metadata.nullMetadata());
    }

    @Override
    public boolean isEmpty() {
      return eventData.isEmpty();
    }

    @Override
    public boolean isText() {
      return true;
    }
  }

  /**
   * The Null Object form of Event&lt;T&gt;.
   */
  public static final class NullEvent<T> extends Event<T> {
    public static NullEvent<byte[]> Binary = new NullEvent<>(EmptyBytesData);
    public static NullEvent<Object> Object = new NullEvent<>(EmptyObjectData);
    public static NullEvent<String> Text = new NullEvent<>(EmptyTextData);

    private NullEvent(final T eventData) {
      super(UnknownId, Object.class, 1, eventData, Metadata.nullMetadata());
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
