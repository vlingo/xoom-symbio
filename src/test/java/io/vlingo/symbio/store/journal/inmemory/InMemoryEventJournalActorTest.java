// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.Entry.TextEntry;
import io.vlingo.symbio.EntryAdapter;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.state.SnapshotStateAdapter;

public class InMemoryEventJournalActorTest {
  private Object object = new Object();
  private MockAppendResultInterest interest = new MockAppendResultInterest();
  private MockEventJournalListener<String> listener;
  private Journal<String> journal;
  private World world;

  @Test
  public void testThatJournalAppendsOneEvent() {
    listener.until = TestUntil.happenings(1);
    journal.append("123", 1, new Test1Source(), interest, object);
    listener.until.completes();
    assertEquals(1, listener.entries.size());
    assertEquals("1", listener.entries.get(0).id());
  }

  @Test
  public void testThatJournalAppendsOneEventWithSnapshot() {
    listener.until = TestUntil.happenings(1);
    journal.appendWith("123", 1, new Test1Source(), new SnapshotState(), interest, object);
    listener.until.completes();
    assertEquals(1, listener.entries.size());
    assertEquals("1", listener.entries.get(0).id());
    assertNotNull(listener.snapshot);
  }

  @Test
  public void testThatJournalReaderReadsOneEvent() {
    listener.until = TestUntil.happenings(1);
    journal.append("123", 1, new Test1Source(), interest, object);
    listener.until.completes();
    final TestUntil untilAsserted = TestUntil.happenings(1);
    journal
      .journalReader("test")
      .andThenTo(reader -> reader.readNext())
      .andThenConsume(event -> {
        assertEquals("1", event.id());
        untilAsserted.happened();
      });
    untilAsserted.completes();
  }

  @Test
  public void testThatJournalReaderReadsThreeEvents() {
    listener.until = TestUntil.happenings(1);
    final List<Source<String>> three = Arrays.asList(new Test1Source(), new Test2Source(), new Test1Source());
    journal.appendAll("123", 1, three, interest, object);
    listener.until.completes();
    final TestUntil untilAsserted = TestUntil.happenings(1);
    journal
      .journalReader("test")
      .andThenTo(reader -> reader.readNext(5))
      .andThenConsume(eventStream -> {
        assertEquals(3, eventStream.entries.size());
        final Iterator<Entry<String>> iterator = eventStream.entries.iterator();
        assertEquals("1", iterator.next().id());
        assertEquals("2", iterator.next().id());
        assertEquals("3", iterator.next().id());
        untilAsserted.happened();
      });
    untilAsserted.completes();
  }

  @Test
  public void testThatStreamReaderReadsFiveEventsWithSnapshot() {
    listener.until = TestUntil.happenings(5);
    journal.append("123", 1, new Test1Source(), interest, object);
    journal.append("123", 2, new Test1Source(), interest, object);
    journal.appendWith("123", 3, new Test1Source(), new SnapshotState(), interest, object);
    journal.append("123", 4, new Test1Source(), interest, object);
    journal.append("123", 5, new Test1Source(), interest, object);
    listener.until.completes();
    final TestUntil untilAsserted = TestUntil.happenings(1);
    journal
      .streamReader("test")
      .andThenTo(reader -> reader.streamFor("123"))
      .andThenConsume(eventStream -> {
        assertEquals(3, eventStream.entries.size());
        final Iterator<Entry<String>> iterator = eventStream.entries.iterator();
        assertEquals("3", iterator.next().id());
        assertEquals("4", iterator.next().id());
        assertEquals("5", iterator.next().id());
        assertNotNull(eventStream.snapshot);
        untilAsserted.happened();
      });
    untilAsserted.completes();
  }

  @Test
  public void testThatStreamReaderReadsFromBeyondSnapshot() {
    listener.until = TestUntil.happenings(5);
    journal.append("123", 1, new Test1Source(), interest, object);
    journal.append("123", 2, new Test1Source(), interest, object);
    journal.appendWith("123", 3, new Test1Source(), new SnapshotState(), interest, object);
    journal.append("123", 4, new Test1Source(), interest, object);
    journal.append("123", 5, new Test1Source(), interest, object);
    listener.until.completes();
    final TestUntil untilAsserted = TestUntil.happenings(1);
    journal
      .streamReader("test")
      .andThenTo(reader -> reader.streamFor("123", 4))
      .andThenConsume(eventStream -> {
        assertEquals(2, eventStream.entries.size());
        final Iterator<Entry<String>> iterator = eventStream.entries.iterator();
        assertEquals("4", iterator.next().id());
        assertEquals("5", iterator.next().id());
        assertNull(eventStream.snapshot);
        untilAsserted.happened();
      });
    untilAsserted.completes();
  }

  @Before
  @SuppressWarnings("unchecked")
  public void setUp() {
    world = World.startWithDefaults("test-journal");
    listener = new MockEventJournalListener<>();
    journal = world.actorFor(Journal.class, InMemoryJournalActor.class, listener);
    journal.registerAdapter(Test1Source.class, new Test1SourceAdapter());
    journal.registerAdapter(Test2Source.class, new Test2SourceAdapter());
    journal.registerAdapter(SnapshotState.class, new SnapshotStateAdapter());
  }

  public static final class Test1Source extends Source<String> {
    private final int one = 1;
    public int one() { return one; }
  }

  public static final class Test2Source extends Source<String> {
    private final int two = 2;
    public int two() { return two; }
  }

  private static final class Test1SourceAdapter implements EntryAdapter<Test1Source,TextEntry> {
    @Override
    public Test1Source fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData, Test1Source.class);
    }

    @Override
    public TextEntry toEntry(final Test1Source source) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(Test1Source.class, 1, serialization, Metadata.nullMetadata());
    }
  }

  private static final class Test2SourceAdapter implements EntryAdapter<Test2Source,TextEntry> {
    @Override
    public Test2Source fromEntry(final TextEntry entry) {
      return JsonSerialization.deserialized(entry.entryData, Test2Source.class);
    }

    @Override
    public TextEntry toEntry(final Test2Source source) {
      final String serialization = JsonSerialization.serialized(source);
      return new TextEntry(Test1Source.class, 1, serialization, Metadata.nullMetadata());
    }
  }
}
