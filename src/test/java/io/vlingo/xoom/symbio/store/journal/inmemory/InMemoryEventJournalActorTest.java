// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.journal.inmemory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.AccessSafely;
import io.vlingo.xoom.common.serialization.JsonSerialization;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.reactivestreams.sink.ConsumerSink;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.symbio.BaseEntry.TextEntry;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.EntryAdapter;
import io.vlingo.xoom.symbio.EntryAdapterProvider;
import io.vlingo.xoom.symbio.EntryBundle;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.StateAdapterProvider;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.MockConfirmDispatchedResultInterest;
import io.vlingo.xoom.symbio.store.dispatch.MockDispatcher;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.MockAppendResultInterest.JournalData;
import io.vlingo.xoom.symbio.store.state.SnapshotStateAdapter;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class InMemoryEventJournalActorTest {
  private Object object = new Object();
  private MockAppendResultInterest interest = new MockAppendResultInterest<String, SnapshotState>();
  private Journal<String> journal;
  private World world;
  private MockDispatcher<String, SnapshotState> dispatcher;

  @Test
  public void testThatJournalAppendsOneEvent() {
    dispatcher.afterCompleting(1);
    interest.afterCompleting(1);

    final Test1Source source = new Test1Source();
    final String streamName = "123";
    final int streamVersion = 1;
    journal.append(streamName, streamVersion, source, interest, object);

    assertEquals(1, interest.getReceivedAppendsSize());

    final List<JournalData<String, SnapshotState>> entries = interest.getEntries();
    final JournalData<String, SnapshotState> journalData = entries.get(0);
    assertNotNull(journalData);
    Assert.assertEquals(streamName, journalData.streamName);
    Assert.assertEquals(streamVersion, journalData.streamVersion);
    Assert.assertEquals(Result.Success, journalData.result);
    Assert.assertFalse(journalData.snapshot.isPresent());

    final List<Source<String>> sourceList = journalData.sources;
    Assert.assertEquals(1, sourceList.size());
    Assert.assertEquals(source, sourceList.get(0));

    assertEquals(1, dispatcher.dispatchedCount());
    final Dispatchable<Entry<?>, ?> dispatched = dispatcher.getDispatched().get(0);

    Assert.assertNotNull(dispatched.createdOn());
    Assert.assertFalse(dispatched.state().isPresent());
    Assert.assertNotNull(dispatched.id());
    final Collection<Entry<?>> dispatchedEntries = dispatched.entries();
    Assert.assertEquals(1, dispatchedEntries.size());
  }

  @Test
  public void testThatJournalAppendsOneEventWithSnapshot() {
    dispatcher.afterCompleting(1);
    interest.afterCompleting(1);

    final Test1Source source = new Test1Source();
    final String streamName = "123";
    final int streamVersion = 1;

    journal.appendWith(streamName, streamVersion, new Test1Source(), new SnapshotState(), interest, object);

    final List<JournalData<String, SnapshotState>> entries = interest.getEntries();
    final JournalData<String, SnapshotState> journalData = entries.get(0);
    assertNotNull(journalData);
    Assert.assertEquals(streamName, journalData.streamName);
    Assert.assertEquals(streamVersion, journalData.streamVersion);
    Assert.assertEquals(Result.Success, journalData.result);
    Assert.assertTrue(journalData.snapshot.isPresent());

    final List<Source<String>> sourceList = journalData.sources;
    Assert.assertEquals(1, sourceList.size());
    Assert.assertEquals(source, sourceList.get(0));

    assertEquals(1, dispatcher.dispatchedCount());
    final Dispatchable<Entry<?>, ?> dispatched = dispatcher.getDispatched().get(0);

    Assert.assertNotNull(dispatched.createdOn());
    Assert.assertTrue(dispatched.state().isPresent());
    Assert.assertNotNull(dispatched.id());
    final Collection<Entry<?>> dispatchedEntries = dispatched.entries();
    Assert.assertEquals(1, dispatchedEntries.size());
  }

  @Test
  public void testThatJournalReaderReadsOneEvent() {
    interest.afterCompleting(1);
    dispatcher.afterCompleting(1);

    final Test1Source source = new Test1Source();
    final String streamName = "123";
    final int streamVersion = 1;
    journal.append(streamName, streamVersion, source, interest, object);

    final AccessSafely accessResults = new TestResults().afterCompleting(1);
    journal.journalReader("test").andThenTo(reader -> reader.readNext()).andThenConsume(event -> {
      accessResults.writeUsing("addAll", Collections.singletonList(event));
    });

    assertNotNull(accessResults.readFrom("entry", 0));
    assertEquals("1", accessResults.readFrom("entryId", 0));
  }

  @Test
  public void testThatJournalReaderReadsThreeEvents() {
    interest.afterCompleting(1);
    dispatcher.afterCompleting(1);

    final List<Source<String>> three = Arrays.asList(new Test1Source(), new Test2Source(), new Test1Source());
    journal.appendAll("123", 1, three, interest, object);

    final AccessSafely accessResults = new TestResults().afterCompleting(1);
    journal.journalReader("test").andThenTo(reader -> reader.readNext(5)).andThenConsume(entries -> {
      accessResults.writeUsing("addAll", entries);
    });

    assertEquals(3, (int) accessResults.readFrom("size"));
    assertEquals("1", accessResults.readFrom("entryId", 0));
    assertEquals("2", accessResults.readFrom("entryId", 1));
    assertEquals("3", accessResults.readFrom("entryId", 2));
  }

  @Test
  public void testThatStreamReaderReadsFiveEventsWithSnapshot() {
    dispatcher.afterCompleting(5);
    interest.afterCompleting(5);
    journal.append("123", 1, new Test1Source(), interest, object);
    journal.append("123", 2, new Test1Source(), interest, object);
    journal.appendWith("123", 3, new Test1Source(), new SnapshotState(), interest, object);
    journal.append("123", 4, new Test1Source(), interest, object);
    journal.append("123", 5, new Test1Source(), interest, object);

    final AccessSafely accessResults = new TestResults().afterCompleting(1);
    journal.streamReader("test").andThenTo(reader -> reader.streamFor("123")).andThenConsume(eventStream -> {
      accessResults.writeUsing("addAll", eventStream.entries);
    });

    assertEquals(3, (int) accessResults.readFrom("size"));
    assertEquals("3", accessResults.readFrom("entryId", 0));
    assertEquals("4", accessResults.readFrom("entryId", 1));
    assertEquals("5", accessResults.readFrom("entryId", 2));
  }

  @Test
  public void testThatStreamReaderReadsFromBeyondSnapshot() {
    dispatcher.afterCompleting(5);
    interest.afterCompleting(5);
    journal.append("123", 1, new Test1Source(), interest, object);
    journal.append("123", 2, new Test1Source(), interest, object);
    journal.appendWith("123", 3, new Test1Source(), new SnapshotState(), interest, object);
    journal.append("123", 4, new Test1Source(), interest, object);
    journal.append("123", 5, new Test1Source(), interest, object);

    final AccessSafely accessResults = new TestResults().afterCompleting(1);
    journal.streamReader("test").andThenTo(reader -> reader.streamFor("123", 4)).andThenConsume(eventStream -> {
      accessResults.writeUsing("addAll", eventStream.entries);
      assertNull(eventStream.snapshot);
    });

    assertEquals(2, (int) accessResults.readFrom("size"));
    assertEquals("4", accessResults.readFrom("entryId", 0));
    assertEquals("5", accessResults.readFrom("entryId", 1));
  }

  private ConsumerSink<EntryBundle> sink;

  private AtomicInteger totalSources = new AtomicInteger(0);

  @Test
  public void testThatJournalReaderStreams() {
    final int limit = 1000;

    dispatcher.afterCompleting(0);
    interest.afterCompleting(0);

    for (int count = 0; count < limit; ++count) {
      journal.append("123-" + count, 1, new Test1Source(count), interest, object);
    }

    final AccessSafely access = AccessSafely.afterCompleting(limit);

    access.writingWith("sourcesCounter", (state) -> { totalSources.incrementAndGet(); });
    access.readingWith("sourcesCount", () -> totalSources.get());

    final Stream all = journal.journalReader("test").andThenTo(reader -> reader.streamAll()).await();

    final Consumer<EntryBundle> bundles = (bundle) -> access.writeUsing("sourcesCounter", 1);

    sink = new ConsumerSink<>(bundles);

    all.flowInto(sink, 100);

    final int sourcesCount = access.readFromExpecting("sourcesCount", limit);

    Assert.assertEquals(limit, totalSources.get());
    Assert.assertEquals(totalSources.get(), sourcesCount);
  }

  @Before
  public void setUp() {
    world = World.startWithDefaults("test-journal");
    this.dispatcher = new MockDispatcher<>(new MockConfirmDispatchedResultInterest());

    journal = Journal.using(world.stage(), InMemoryJournalActor.class, Arrays.asList(this.dispatcher));
    EntryAdapterProvider.instance(world).registerAdapter(Test1Source.class, new Test1SourceAdapter());
    EntryAdapterProvider.instance(world).registerAdapter(Test2Source.class, new Test2SourceAdapter());
    StateAdapterProvider.instance(world).registerAdapter(SnapshotState.class, new SnapshotStateAdapter());
  }

  @After
  public void tearDown() {
    world.terminate();
  }

  public static final class Test1Source extends Source<String> {
    private final int value;

    public Test1Source() {
      this(1);
    }

    public Test1Source(final int value) {
      this.value = value;
    }

    public int one() {
      return value;
    }
  }

  public static final class Test2Source extends Source<String> {
    private final int two = 2;

    public int two() {
      return two;
    }
  }

  private static final class Test1SourceAdapter implements EntryAdapter<Test1Source, TextEntry> {
    @Override
    public Test1Source fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), Test1Source.class);
    }

    @Override
    public TextEntry toEntry(Test1Source source, Metadata metadata) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(Test1Source.class, 1, serialization, metadata);
    }

    @Override
    public TextEntry toEntry(Test1Source source, String id, Metadata metadata) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, Test1Source.class, 1, serialization, metadata);
    }

    @Override
    public TextEntry toEntry(final Test1Source source, final int version, final String id, final Metadata metadata) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, Test1Source.class, 1, serialization, version, metadata);
    }
  }

  private static final class Test2SourceAdapter implements EntryAdapter<Test2Source, TextEntry> {
    @Override
    public Test2Source fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData(), Test2Source.class);
    }

    @Override
    public TextEntry toEntry(Test2Source source, Metadata metadata) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(Test2Source.class, 1, serialization, metadata);
    }

    @Override
    public TextEntry toEntry(Test2Source source, String id, Metadata metadata) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, Test2Source.class, 1, serialization, metadata);
    }

    @Override
    public TextEntry toEntry(final Test2Source source, final int version, final String id, final Metadata metadata) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(id, Test2Source.class, 1, serialization, version, metadata);
    }
  }

  private static final class TestResults {
    AccessSafely access;
    public final List<BaseEntry<String>> entries = new ArrayList<>();

    public AccessSafely afterCompleting(final int times) {
      access = AccessSafely.afterCompleting(times)
              .writingWith("addAll", (values) -> this.entries.addAll((Collection<BaseEntry<String>>) values))
              .readingWith("entry", (index) -> this.entries.get((int) index))
              .readingWith("entryId", (index) -> this.entries.get((int) index).id())
              .readingWith("size", () -> this.entries.size());

      return access;
    }
  }
}
