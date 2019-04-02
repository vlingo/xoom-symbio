// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.DefaultTextEntryAdapter.ObjectSource;
import io.vlingo.symbio.BaseEntry.TextEntry;

public final class DefaultTextEntryAdapter implements EntryAdapter<ObjectSource,TextEntry> {

  @Override
  public ObjectSource fromEntry(final TextEntry entry) {
    try {
      final Class<?> sourceType = Class.forName(entry.type());
      return (ObjectSource) JsonSerialization.deserialized(entry.entryData(), sourceType);
    } catch (Exception e) {
      throw new IllegalStateException("Cannot convert to type: " + entry.type());
    }
  }

  @Override
  public TextEntry toEntry(final ObjectSource source) {
    final String serialization = JsonSerialization.serialized(source);
    return new TextEntry(source.getClass(), 1, serialization, Metadata.nullMetadata());
  }

  @Override
  public TextEntry toEntry(final ObjectSource source, final String id) {
    final String serialization = JsonSerialization.serialized(source);
    return new TextEntry(id, source.getClass(), 1, serialization, Metadata.nullMetadata());
  }

  public static final class ObjectSource extends Source<Object> { }
}
