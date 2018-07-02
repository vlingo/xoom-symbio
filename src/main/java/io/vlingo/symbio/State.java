// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import java.util.Comparator;

public abstract class State<T> implements Comparable<State<T>> {
  private final static byte[] EmptyBytesData = new byte[0];
  private final static Object EmptyObjectData = new Object();
  private final static String EmptyTextData = "";
  private final static String NoOp = "";

  public final String id;
  public final T data;
  public final int dataVersion;
  public final Metadata metadata;
  public final String type;
  public final int typeVersion;

  protected State(final String id, final Class<?> type, final int typeVersion, final T data, final int dataVersion, final Metadata metadata) {
    if (id == null) throw new IllegalArgumentException("State id must not be null.");
    this.id = id;
    if (type == null) throw new IllegalArgumentException("State type must not be null.");
    this.type = type.getName();
    if (typeVersion <= 0) throw new IllegalArgumentException("State typeVersion must be greater than 0.");
    this.typeVersion = typeVersion;
    if (data == null) throw new IllegalArgumentException("State data must not be null.");
    this.data = data;
    if (dataVersion <= 0) throw new IllegalArgumentException("State dataVersion must be greater than 0.");
    this.dataVersion = dataVersion;
    if (metadata == null) throw new IllegalArgumentException("State metadata must not be null.");
    this.metadata = metadata;
  }

  protected State(final String id, final Class<?> type, final int typeVersion, final T data, final int dataVersion) {
    this(id, type, typeVersion, data, dataVersion, Metadata.nullMetadata());
  }

  public BinaryState asBinaryState() {
    return (BinaryState) this;
  }

  public TextState asTextState() {
    return (TextState) this;
  }

  public boolean hasMetadata() {
    return !metadata.isEmpty();
  }

  public boolean isBinary() {
    return false;
  }

  public boolean isEmpty() {
    return false;
  }

  public boolean isNull() {
    return false;
  }

  public boolean isText() {
    return false;
  }

  @SuppressWarnings("unchecked")
  public <C> Class<C> typed() {
    try {
      return (Class<C>) Class.forName(type);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot get class for type: " + type);
    }
  }

  @Override
  public int compareTo(final State<T> other) {
    final int dataDiff = compareData(this, other);
    if (dataDiff != 0) return dataDiff;

    return Comparator
      .comparing((State<T> s) -> s.id)
      .thenComparing(s -> s.type)
      .thenComparingInt(s -> s.typeVersion)
      .thenComparingInt(s -> s.dataVersion)
      .thenComparing(s -> s.metadata)
      .compare(this, other);
  }

  @Override
  public int hashCode() {
    return 31 * id.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != this.getClass()) {
      return false;
    }
    return id.equals(((State<?>) other).id);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() +
            "[id=" + id + " type=" + type + " typeVersion=" + typeVersion +
            " data=" + (isText() ? data.toString() : "(binary)") + " dataVersion=" + dataVersion +
            " metadata=" + metadata + "]";
  }

  private int compareData(final State<T> state1, final State<T> state2) {
    if (state1.isText() && state2.isText()) {
      return ((String) state1.data).compareTo((String) state2.data);
    } else if (state1.isBinary() && state2.isBinary()) {
      final byte[] data1 = (byte[]) state1.data;
      final byte[] data2 = (byte[]) state2.data;
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

  public static final class BinaryState extends State<byte[]> {
    public BinaryState(final String id, final Class<?> type, final int typeVersion, final byte[] data, final int dataVersion, final Metadata metadata) {
      super(id, type, typeVersion, data, dataVersion, metadata);
    }

    public BinaryState(final String id, final Class<?> type, final int typeVersion, final byte[] data, final int dataVersion) {
      super(id, type, typeVersion, data, dataVersion);
    }

    public BinaryState() {
      super(NoOp, Object.class, 1, State.EmptyBytesData, 1, Metadata.nullMetadata());
    }

    @Override
    public boolean isBinary() {
      return true;
    }

    @Override
    public boolean isEmpty() {
      return data.length == 0;
    }
  }

  public static final class TextState extends State<String> {
    public TextState(final String id, final Class<?> type, final int typeVersion, final String data, final int dataVersion, final Metadata metadata) {
      super(id, type, typeVersion, data, dataVersion, metadata);
    }

    public TextState(final String id, final Class<?> type, final int typeVersion, final String data, final int dataVersion) {
      super(id, type, typeVersion, data, dataVersion);
    }

    public TextState() {
      super(NoOp, Object.class, 1, State.EmptyTextData, 1, Metadata.nullMetadata());
    }

    @Override
    public boolean isEmpty() {
      return data.isEmpty();
    }

    @Override
    public boolean isText() {
      return true;
    }
  }

  public static final class NullState extends State<Object> {
    public NullState() {
      super(NoOp, Object.class, 1, State.EmptyObjectData, 1, Metadata.nullMetadata());
    }

    public boolean isNull() {
      return true;
    }
  }
}
