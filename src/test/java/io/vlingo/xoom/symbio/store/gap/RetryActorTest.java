// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.gap;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.symbio.Entry;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

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
}