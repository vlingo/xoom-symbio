// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public class EntryAdapterProvider {
  private final Map<Class<?>,EntryAdapter<?,?>> adapters;
  private final Map<String,EntryAdapter<?,?>> namedAdapters;

  public EntryAdapterProvider() {
    this.adapters = new HashMap<>();
    this.namedAdapters = new HashMap<>();
  }

  public <S extends Source<?>,E extends Entry<?>> void registerAdapter(final Class<S> sourceType, final EntryAdapter<S,E> adapter) {
    adapters.put(sourceType, adapter);
    namedAdapters.put(sourceType.getName(), adapter);
  }

  public <S extends Source<?>,E extends Entry<?>> void registerAdapter(final Class<S> sourceType, final EntryAdapter<S,E> adapter, final BiConsumer<Class<S>,EntryAdapter<S,E>> consumer) {
    adapters.put(sourceType, adapter);
    namedAdapters.put(sourceType.getName(), adapter);
    consumer.accept(sourceType, adapter);
  }

  public <S extends Source<?>,E extends Entry<?>> List<E> asEntries(final List<S> sources) {
    final List<E> entries = new ArrayList<>(sources.size());
    for (final S source : sources) {
      entries.add(asEntry(source));
    }
    return entries;
  }

  @SuppressWarnings("unchecked")
  public <S extends Source<?>,E extends Entry<?>> E asEntry(final S source) {
    final EntryAdapter<S,E>  adapter = (EntryAdapter<S,E>) adapter((Class<S>) source.getClass());
    return adapter.toEntry(source);
  }

  public <S extends Source<?>,E extends Entry<?>> List<S> asSources(final List<E> entries) {
    final List<S> sources = new ArrayList<>(entries.size());
    for (final E entry : entries) {
      sources.add(asSource(entry));
    }
    return sources;
  }

  public <S extends Source<?>,E extends Entry<?>> S asSource(final E entry) {
    EntryAdapter<S,E> adapter = namedAdapter(entry);
    return (S) adapter.fromEntry(entry);
  }

  @SuppressWarnings("unchecked")
  private <S extends Source<?>,E extends Entry<?>> EntryAdapter<S,E> adapter(final Class<?> sourceType) {
    final EntryAdapter<S,E> adapter = (EntryAdapter<S,E>) adapters.get(sourceType);
    if (adapter != null) {
      return adapter;
    }
    throw new IllegalStateException("Adapter not registrered for: " + sourceType.getName());
  }

  @SuppressWarnings("unchecked")
  private <S extends Source<?>,E extends Entry<?>> EntryAdapter<S,E> namedAdapter(final E entry) {
    final EntryAdapter<S,E> adapter = (EntryAdapter<S,E>) namedAdapters.get(entry.type);
    if (adapter != null) {
      return adapter;
    }
    throw new IllegalStateException("Adapter not registrered for: " + entry.type);
  }
}
