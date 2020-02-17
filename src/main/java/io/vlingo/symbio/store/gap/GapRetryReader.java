// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.gap;

import io.vlingo.actors.Actor;
import io.vlingo.actors.CompletesEventually;
import io.vlingo.actors.Stage;
import io.vlingo.common.Scheduled;
import io.vlingo.common.Scheduler;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.store.EntryReader;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Detection and fill up (gap prevention) functionality related to {@link EntryReader}
 * @param <T> <T> Generic type applied to {@link Entry<T>}
 */
public class GapRetryReader<T> {
    private final Scheduled<RetryGappedEntries<T>> actor;
    private final Scheduler scheduler;

    public GapRetryReader(Stage stage, Scheduler scheduler) {
        this.actor = stage.actorFor(Scheduled.class, GapsFillUpActor.class);
        this.scheduler = scheduler;
    }

    private Set<Long> collectIds(List<Entry<T>> entries) {
        if (entries == null) {
            return new HashSet<>();
        } else {
            return entries.stream()
                    .map(e -> Long.parseLong(e.id()))
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Single entry variant method of {@link #detectGaps(List, long, long)}.
     * @param entry
     * @param startIndex This index refers to {@link Entry#id()}
     * @param count
     * @return
     */
    public List<Long> detectGaps(Entry<T> entry, long startIndex, long count) {
        List<Entry<T>> entries = entry == null ? new ArrayList<>() : Collections.singletonList(entry);
        return detectGaps(entries, startIndex, count);
    }

    /**
     * Detect gaps in entries list.
     * @param entries
     * @param startIndex This index refers to {@link Entry#id()}
     * @param count
     * @return Empty list if no gaps have been detected.
     */
    public List<Long> detectGaps(List<Entry<T>> entries, long startIndex, long count) {
        Set<Long> allIds = collectIds(entries);
        List<Long> gapIds = new ArrayList<>();

        for (long index = 0; index < count; index++) {
            if (!allIds.contains(startIndex + index)) {
                gapIds.add(startIndex + index);
            }
        }

        return gapIds;
    }

    public void readGaps(GappedEntries<T> gappedEntries, int retries, long retryInterval, Function<List<Long>, List<Entry<T>>> gappedReader) {
        RetryGappedEntries<T> entries = new RetryGappedEntries<>(gappedEntries, 1, retries, retryInterval, gappedReader);
        scheduler.scheduleOnce(actor, entries, 0L, retryInterval);
    }

    static class RetryGappedEntries<T> {
        private final GappedEntries<T> gappedEntries;
        private final int currentRetry;
        private final int retries;
        private final long retryInterval;
        private final Function<List<Long>, List<Entry<T>>> gappedReader;

        RetryGappedEntries(GappedEntries<T> gappedEntries, int currentRetry, int retries, long retryInterval, Function<List<Long>, List<Entry<T>>> gappedReader) {
            this.gappedEntries = gappedEntries;
            this.currentRetry = currentRetry;
            this.retries = retries;
            this.retryInterval = retryInterval;
            this.gappedReader = gappedReader;
        }

        boolean moreRetries() {
            return currentRetry < retries;
        }

        RetryGappedEntries<T> nextRetry(GappedEntries<T> nextGappedEntries) {
            return new RetryGappedEntries<>(nextGappedEntries, currentRetry + 1, retries, retryInterval, gappedReader);
        }
    }

    public static class GapsFillUpActor<T> extends Actor implements Scheduled<RetryGappedEntries<T>> {
        @Override
        public void intervalSignal(Scheduled<RetryGappedEntries<T>> scheduled, RetryGappedEntries<T> data) {
            Function<List<Long>, List<Entry<T>>> gappedReader = data.gappedReader;
            List<Entry<T>> fillups = gappedReader.apply(data.gappedEntries.getGapIds());
            GappedEntries<T> nextGappedEntries = data.gappedEntries.fillupWith(fillups);

            if (!nextGappedEntries.containGaps() || !data.moreRetries()) {
                CompletesEventually eventually = data.gappedEntries.getEventually();
                if (nextGappedEntries.size() == 1) {
                    // Only one entry has to be returned.
                    // {@link EntryReader<T>} - read one Entry<T> method.
                    eventually.with(nextGappedEntries.getFirst().orElse(null));
                } else {
                    // {@link EntryReader<T>} - read a list of Entry<T>
                    eventually.with(nextGappedEntries.getSortedLoadedEntries());
                }
            } else {
                RetryGappedEntries<T> nextData = data.nextRetry(nextGappedEntries);
                scheduler().scheduleOnce(scheduled, nextData, 0L, data.retryInterval);
            }
        }
    }
}