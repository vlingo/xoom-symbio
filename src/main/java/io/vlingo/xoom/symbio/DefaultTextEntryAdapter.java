// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio;

import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.symbio.BaseEntry.TextEntry;
import io.vlingo.xoom.symbio.store.StoredTypes;

@SuppressWarnings("rawtypes")
public final class DefaultTextEntryAdapter implements EntryAdapter {

  @Override
  public Source fromEntry(final Entry entry) {
    try {
      final Class<?> sourceType = StoredTypes.forName(entry.typeName());
      final Object bland = JsonSerialization.deserialized((String) entry.entryData(), sourceType);
      return (Source) bland;
    } catch (Exception e) {
      throw new IllegalStateException("Cannot convert to type: " + entry.typeName());
    }
  }

  @Override
  public Entry<?> toEntry(final Source source, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(source);
    return new TextEntry(source.getClass(), 1, serialization, metadata);
  }

  @Override
  public Entry<?> toEntry(final Source source, final String id, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(source);
    return new TextEntry(id, source.getClass(), 1, serialization, metadata);
  }

  @Override
  public Entry toEntry(final Source source, final int version, final String id, final Metadata metadata) {
    final String serialization = JsonSerialization.serialized(source);
    return new TextEntry(id, source.getClass(), 1, serialization, version, metadata);
  }
}
