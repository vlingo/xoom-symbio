// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import java.util.List;

import io.vlingo.actors.Actor;
import io.vlingo.actors.ActorInstantiator;
import io.vlingo.actors.Definition;
import io.vlingo.common.Completes;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.dispatch.Dispatchable;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.JournalReader;
import io.vlingo.symbio.store.journal.StreamReader;

public class InMemoryJournalActor<T,RS extends State<?>> extends Actor implements Journal<T> {
  private final InMemoryJournal<T,RS> journal;

  public InMemoryJournalActor(final Dispatcher<Dispatchable<Entry<T>,RS>> dispatcher) {
    this.journal = new InMemoryJournal<>(dispatcher, stage().world());
  }

  @Override
  public <S,ST> void append(final String streamName, final int streamVersion, final Source<S> source, final AppendResultInterest interest, final Object object) {
    journal.append(streamName, streamVersion, source, interest, object);
  }

  @Override
  public <S, ST> void append(final String streamName, final int streamVersion, final Source<S> source, final Metadata metadata,
          final AppendResultInterest interest, final Object object) {
    journal.append(streamName, streamVersion, source, metadata, interest, object);
  }

  @Override
  public <S,ST> void appendWith(final String streamName, final int streamVersion, final Source<S> source, final ST snapshot, final AppendResultInterest interest, final Object object) {
    journal.appendWith(streamName, streamVersion, source, snapshot, interest, object);
  }

  @Override
  public <S, ST> void appendWith(final String streamName, final int streamVersion, final Source<S> source, final Metadata metadata, final ST snapshot,
          final AppendResultInterest interest, final Object object) {
    journal.appendWith(streamName, streamVersion, source, metadata, snapshot, interest, object);
  }

  @Override
  public <S,ST> void appendAll(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final AppendResultInterest interest, final Object object) {
    journal.appendAll(streamName, fromStreamVersion, sources, interest, object);
  }

  @Override
  public <S, ST> void appendAll(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final Metadata metadata,
          final AppendResultInterest interest, final Object object) {
    journal.appendAll(streamName, fromStreamVersion, sources, metadata, interest, object);
  }

  @Override
  public <S,ST> void appendAllWith(final String streamName, final int fromStreamVersion, final List<Source<S>> sources, final ST snapshot, final AppendResultInterest interest, final Object object) {
    journal.appendAllWith(streamName, fromStreamVersion, sources, snapshot, interest, object);
  }

  @Override
  public <S, ST> void appendAllWith(final String streamName, final int fromStreamVersion, final List<Source<S>> sources,
          final Metadata metadata, final ST snapshot, final AppendResultInterest interest, final Object object) {
    journal.appendAllWith(streamName, fromStreamVersion, sources, metadata, snapshot, interest, object);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <ET extends Entry<?>> Completes<JournalReader<ET>> journalReader(final String name) {
    final JournalReader<ET> inmemory = (JournalReader<ET>) journal.journalReader(name).outcome();
    final JournalReader<ET> actor = childActorFor(JournalReader.class, Definition.has(InMemoryJournalReaderActor.class, new InMemoryJournalReaderInstantiator<>(inmemory)));
    return completes().with(actor);
  }

  @Override
  public Completes<StreamReader<T>> streamReader(final String name) {
    final StreamReader<T> inmemory = journal.streamReader(name).outcome();
    @SuppressWarnings("unchecked")
    final StreamReader<T> actor = childActorFor(StreamReader.class, Definition.has(InMemoryStreamReaderActor.class, new InMemoryStreamReaderInstantiator<>(inmemory)));
    return completes().with(actor);
  }

  @Override
  public void stop() {
    journal.stop();
    super.stop();
  }

  @SuppressWarnings("rawtypes")
  private static class InMemoryJournalReaderInstantiator<T extends Entry<?>> implements ActorInstantiator<InMemoryJournalReaderActor> {
    private static final long serialVersionUID = -4704305821903232245L;

    private final JournalReader<T> inmemory;

    InMemoryJournalReaderInstantiator(final JournalReader<T> inmemory) {
      this.inmemory = inmemory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public InMemoryJournalReaderActor<T> instantiate() {
      return new InMemoryJournalReaderActor((InMemoryJournalReader<T>) inmemory);
    }

    @Override
    public Class<InMemoryJournalReaderActor> type() {
      return InMemoryJournalReaderActor.class;
    }
  }

  @SuppressWarnings("rawtypes")
  private static class InMemoryStreamReaderInstantiator<T extends Entry<?>> implements ActorInstantiator<InMemoryStreamReaderActor> {
    private static final long serialVersionUID = 5878161332125082127L;

    private final StreamReader<?> inmemory;

    InMemoryStreamReaderInstantiator(final StreamReader<?> inmemory) {
      this.inmemory = inmemory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public InMemoryStreamReaderActor<T> instantiate() {
      return new InMemoryStreamReaderActor((InMemoryStreamReader<T>) inmemory);
    }

    @Override
    public Class<InMemoryStreamReaderActor> type() {
      return InMemoryStreamReaderActor.class;
    }
  }
}
