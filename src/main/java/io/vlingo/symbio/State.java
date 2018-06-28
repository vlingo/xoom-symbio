// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

public abstract class State<T> {
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
    this(id, type, typeVersion, data, dataVersion, Metadata.Null);
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
  public int hashCode() {
    return id.hashCode();
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

  public static final class BinaryState extends State<byte[]> {
    public BinaryState(final String id, final Class<?> type, final int typeVersion, final byte[] data, final int dataVersion, final Metadata metadata) {
      super(id, type, typeVersion, data, dataVersion, metadata);
    }

    public BinaryState(final String id, final Class<?> type, final int typeVersion, final byte[] data, final int dataVersion) {
      super(id, type, typeVersion, data, dataVersion);
    }

    public BinaryState() {
      super(NoOp, Object.class, 1, State.EmptyBytesData, 1, Metadata.Null);
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
      super(NoOp, Object.class, 1, State.EmptyTextData, 1, Metadata.Null);
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
      super(NoOp, Object.class, 1, State.EmptyObjectData, 1, Metadata.Null);
    }

    public boolean isNull() {
      return true;
    }
  }
}
