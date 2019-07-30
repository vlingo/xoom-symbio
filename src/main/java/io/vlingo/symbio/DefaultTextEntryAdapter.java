// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.BaseEntry.TextEntry;

@SuppressWarnings("rawtypes")
public final class DefaultTextEntryAdapter implements EntryAdapter {

  @Override
  public Source fromEntry(final Entry entry) {
    try {
      final Class<?> sourceType = Class.forName(entry.typeName());
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
}
