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
import java.util.UUID;
import java.util.function.BiConsumer;

import io.vlingo.actors.World;
import io.vlingo.symbio.DefaultTextEntryAdapter.ObjectSource;
import io.vlingo.symbio.Entry.TextEntry;

public class EntryAdapterProvider {
  static final String INTERNAL_NAME = UUID.randomUUID().toString();

  private final Map<Class<?>,EntryAdapter<?,?>> adapters;
  private final Map<String,EntryAdapter<?,?>> namedAdapters;
  private final EntryAdapter<ObjectSource,TextEntry> defaultTextEntryAdapter;

  /**
   * Answer the {@code EntryAdapterProvider} held by the {@code world}.
   * @param world the World where the EntryAdapterProvider is held
   * @return EntryAdapterProvider
   */
  public static EntryAdapterProvider instance(final World world) {
    return world.resolveDynamic(INTERNAL_NAME, EntryAdapterProvider.class);
  }

  public EntryAdapterProvider(final World world) {
    this();
    world.registerDynamic(INTERNAL_NAME, this);
  }

  public EntryAdapterProvider() {
    this.adapters = new HashMap<>();
    this.namedAdapters = new HashMap<>();
    this.defaultTextEntryAdapter = new DefaultTextEntryAdapter();
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
    if (adapter != null) {
      return adapter.toEntry(source);
    }
    return (E) defaultTextEntryAdapter.toEntry((ObjectSource) source);
  }

  public <S extends Source<?>,E extends Entry<?>> List<S> asSources(final List<E> entries) {
    final List<S> sources = new ArrayList<>(entries.size());
    for (final E entry : entries) {
      sources.add(asSource(entry));
    }
    return sources;
  }

  @SuppressWarnings("unchecked")
  public <S extends Source<?>,E extends Entry<?>> S asSource(final E entry) {
    EntryAdapter<S,E> adapter = namedAdapter(entry);
    if (adapter != null) {
      return adapter.fromEntry(entry);
    }
    return (S) defaultTextEntryAdapter.fromEntry((TextEntry) entry);
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
