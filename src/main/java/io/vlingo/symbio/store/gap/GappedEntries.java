// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.gap;

import io.vlingo.actors.CompletesEventually;
import io.vlingo.symbio.Entry;

import java.util.*;

/**
 * This class models entries which contain gaps.
 *
 * @param <T> Subtype of {@code Entry}
 */
public class GappedEntries<T extends Entry<?>> {
    /**
     * Successfully loaded entries up to now.
     */
    private final List<T> loadedEntries;

    /**
     * List of ids failed to be loaded (gaps).
     */
    private final List<Long> gapIds;

    /**
     * {@link CompletesEventually} object necessary to asynchronously send the final list of loaded entries.
     */
    private final CompletesEventually eventually;

    public GappedEntries(List<T> loadedEntries, List<Long> gapIds, CompletesEventually eventually) {
        this.loadedEntries = loadedEntries;
        this.gapIds = gapIds;
        this.eventually = eventually;
    }

    public List<T> getLoadedEntries() {
        return loadedEntries;
    }

    private int compare(T e1, T e2) {
        long id1 = Long.parseLong(e1.id());
        long id2 = Long.parseLong(e2.id());

        return Long.compare(id1, id2);
    }

    /**
     * Get loaded entries
     *
     * @return Sorted entries.
     */
    public List<T> getSortedLoadedEntries() {
        SortedSet<T> sorted = new TreeSet<>(this::compare);
        sorted.addAll(loadedEntries);

        return new ArrayList<>(sorted);
    }

    public List<Long> getGapIds() {
        return gapIds;
    }

    public CompletesEventually getEventually() {
        return eventually;
    }

    public boolean containGaps() {
        return !gapIds.isEmpty();
    }

    /**
     * Combined size of loaded and gapped entries.
     *
     * @return Sum of loaded and gapped entries.
     */
    public int size() {
        return loadedEntries.size() + gapIds.size();
    }

    /**
     * Get first successfully loaded entry.
     *
     * @return an {@link Optional} describing the first entry.
     */
    public Optional<T> getFirst() {
        if (loadedEntries.size() > 0) {
            return Optional.of(loadedEntries.get(0));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Builds up a new <code>GappedEntries</code> obtained by filling up current instance with fill ups parameter.
     *
     * @param fillups Parameter used to fill up current instance.
     * @return New, filled up <code>GappedEntries</code>.
     */
    public GappedEntries<T> fillupWith(List<T> fillups) {
        List<T> newLoadedEntries = new ArrayList<>(loadedEntries);
        List<Long> newGapIds = new ArrayList<>(gapIds);
        for (T fillup : fillups) {
            Long fillupId = Long.parseLong(fillup.id());
            newGapIds.remove(fillupId);
            newLoadedEntries.add(fillup);
        }

        return new GappedEntries<>(newLoadedEntries, newGapIds, eventually);
    }
}
