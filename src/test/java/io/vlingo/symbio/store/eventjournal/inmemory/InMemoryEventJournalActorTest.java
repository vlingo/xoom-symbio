// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.eventjournal.inmemory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.Definition;
import io.vlingo.actors.World;
import io.vlingo.actors.testkit.TestUntil;
import io.vlingo.symbio.Event;
import io.vlingo.symbio.Event.TextEvent;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.eventjournal.EventJournal;

public class InMemoryEventJournalActorTest {
  private MockEventJournalListener<String> listener;
  private EventJournal<String> journal;
  private World world;

  @Test
  public void testThatJournalAppendsOneEvent() {
    listener.until = TestUntil.happenings(1);
    journal.append("123", 1, new TextEvent());
    listener.until.completes();
    assertEquals(1, listener.events.size());
    assertEquals("1", listener.events.get(0).id());
  }

  @Test
  public void testThatJournalAppendsOneEventWithSnapshot() {
    listener.until = TestUntil.happenings(1);
    journal.appendWith("123", 1, new TextEvent(), new TextState());
    listener.until.completes();
    assertEquals(1, listener.events.size());
    assertEquals("1", listener.events.get(0).id());
    assertNotNull(listener.snapshot);
  }

  @Test
  public void testThatJournalReaderReadsOneEvent() {
    listener.until = TestUntil.happenings(1);
    journal.append("123", 1, new TextEvent());
    listener.until.completes();
    final TestUntil untilAsserted = TestUntil.happenings(1);
    journal
      .eventJournalReader("test")
      .andThenInto(reader -> reader.readNext())
      .andThenConsume(event -> {
        assertEquals("1", event.id());
        untilAsserted.happened();
      });
    untilAsserted.completes();
  }

  @Test
  public void testThatJournalReaderReadsThreeEvents() {
    listener.until = TestUntil.happenings(1);
    final List<Event<String>> three = Arrays.asList(new TextEvent(), new TextEvent(), new TextEvent());
    journal.appendAll("123", 1, three);
    listener.until.completes();
    final TestUntil untilAsserted = TestUntil.happenings(1);
    journal
      .eventJournalReader("test")
      .andThenInto(reader -> reader.readNext(5))
      .andThenConsume(eventStream -> {
        assertEquals(3, eventStream.events.size());
        final Iterator<Event<String>> iterator = eventStream.events.iterator();
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
    journal.append("123", 1, new TextEvent());
    journal.append("123", 2, new TextEvent());
    journal.appendWith("123", 3, new TextEvent(), new TextState("1", String.class, 1, "data", 3));
    journal.append("123", 4, new TextEvent());
    journal.append("123", 5, new TextEvent());
    listener.until.completes();
    final TestUntil untilAsserted = TestUntil.happenings(1);
    journal
      .eventStreamReader("test")
      .andThenInto(reader -> reader.streamFor("123"))
      .andThenConsume(eventStream -> {
        assertEquals(2, eventStream.events.size());
        final Iterator<Event<String>> iterator = eventStream.events.iterator();
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
    journal.append("123", 1, new TextEvent());
    journal.append("123", 2, new TextEvent());
    journal.appendWith("123", 3, new TextEvent(), new TextState("1", String.class, 1, "data", 3));
    journal.append("123", 4, new TextEvent());
    journal.append("123", 5, new TextEvent());
    listener.until.completes();
    final TestUntil untilAsserted = TestUntil.happenings(1);
    journal
      .eventStreamReader("test")
      .andThenInto(reader -> reader.streamFor("123", 4))
      .andThenConsume(eventStream -> {
        assertEquals(2, eventStream.events.size());
        final Iterator<Event<String>> iterator = eventStream.events.iterator();
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
    journal = world.actorFor(Definition.has(InMemoryEventJournalActor.class, Definition.parameters(listener)), EventJournal.class);
  }
}
