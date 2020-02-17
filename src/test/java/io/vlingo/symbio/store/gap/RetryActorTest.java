// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.gap;

import io.vlingo.actors.Actor;
import io.vlingo.actors.World;
import io.vlingo.common.Completes;
import io.vlingo.symbio.BaseEntry;
import io.vlingo.symbio.Entry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RetryActorTest {
    private World world;
    private Reader readerActor;

    @Before
    public void setUp() {
        world = World.startWithDefaults("retry-actor-tests");
        readerActor = world.actorFor(Reader.class, RetryReaderActor.class);
    }

    @Test
    public void readTest() {
        Entry<String> entry = readerActor.readOne().await();
        Assert.assertEquals("0", entry.id());

        Entry<String> entry2 = readerActor.readOne().await();
        Assert.assertEquals("1", entry2.id());

        List<Entry<String>> entries = readerActor.readNext(10).await();
        Assert.assertEquals(10, entries.size());

        List<Entry<String>> entries2 = readerActor.readNext(50).await();
        // 4 entries out of 50 didn't get loaded at all
        Assert.assertEquals(46, entries2.size());

        long previousId = -1;
        for (Entry<String> currentEntry : entries2) {
            long currentId = Long.parseLong(currentEntry.id());
            Assert.assertTrue(previousId < currentId);
            previousId = currentId;
        }
    }

    public interface Reader {
        Completes<Entry<String>> readOne();
        Completes<List<Entry<String>>> readNext(int count);
    }

    public static class RetryReaderActor extends Actor implements Reader {
        private GapRetryReader<String> reader = null;
        private int offset = 0;

        GapRetryReader<String> reader() {
            if (reader == null) {
                reader = new GapRetryReader<>(stage(), scheduler());
            }

            return reader;
        }

        private List<Entry<String>> readIds(List<Long> ids) {
            List<Entry<String>> entries = new ArrayList<>();
            if (ids.size() < 3) {
                // Read all requested ids
                for (Long id : ids) {
                    BaseEntry.TextEntry entry = new BaseEntry.TextEntry(Long.toString(id), Object.class, 1, "Entry_" + id);
                    entries.add(entry);
                }
            } else {
                for (int i = 0; i < ids.size(); i++) {
                    if (i % 2 == 0) {
                        // Read every second id
                        Long id = ids.get(i);
                        BaseEntry.TextEntry entry = new BaseEntry.TextEntry(Long.toString(id), Object.class, 1, "Entry_" + id);
                        entries.add(entry);
                    }
                }
            }

            return entries;
        }

        @Override
        public Completes<Entry<String>> readOne() {
            // Simulate failed read of one entry
            final Entry<String> entry = null;
            final List<Long> gapIds = reader().detectGaps(entry, offset, 1);
            GappedEntries<String> gappedEntries = new GappedEntries<>(new ArrayList<>(), gapIds, completesEventually());

            reader().readGaps(gappedEntries, 3, 10L, this::readIds);
            offset++;

            return completes();
        }

        @Override
        public Completes<List<Entry<String>>> readNext(int count) {
            List<Entry<String>> entries = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                // every 3rd entry is loaded successfully
                if (i % 3 == 0) {
                    BaseEntry.TextEntry entry = new BaseEntry.TextEntry(Long.toString(offset + i), Object.class, 1, "Entry_" + offset + i);
                    entries.add(entry);
                }
            }

            final List<Long> gapIds = reader().detectGaps(entries, offset, count);
            GappedEntries<String> gappedEntries = new GappedEntries<>(entries, gapIds, completesEventually());
            offset += count;
            reader().readGaps(gappedEntries, 3, 10L, this::readIds);

            return completes();
        }
    }
}