// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.gap;

import io.vlingo.actors.CompletesEventually;
import io.vlingo.symbio.Entry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GappedEntries<T> {
    /**
     * Successfully loaded entries up to now.
     */
    private final List<Entry<T>> loadedEntries;

    /**
     * List of ids failed to be loaded (gaps).
     */
    private final List<Long> gapIds;

    /**
     * {@link CompletesEventually} object necessary to asynchronously send the final list of loaded entries.
     */
    private final CompletesEventually eventually;

    public GappedEntries(List<Entry<T>> loadedEntries, List<Long> gapIds, CompletesEventually eventually) {
        this.loadedEntries = loadedEntries;
        this.gapIds = gapIds;
        this.eventually = eventually;
    }

    public List<Entry<T>> getLoadedEntries() {
        return loadedEntries;
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
     * @return
     */
    public int size() {
        return loadedEntries.size() + gapIds.size();
    }

    /**
     * Get first successfully loaded entry.
     * @return
     */
    public Optional<Entry<T>> getFirst() {
        if (loadedEntries.size() > 0) {
            return Optional.of(loadedEntries.get(0));
        } else {
            return Optional.empty();
        }
    }

    public GappedEntries<T> fillupWith(List<Entry<T>> fillups) {
        List<Entry<T>> newLoadedEntries = new ArrayList<>(loadedEntries);
        List<Long> newGapIds = new ArrayList<>(gapIds);
        for (Entry<T> fillup : fillups) {
            Long fillupId = Long.parseLong(fillup.id());
            newGapIds.remove(fillupId);
            newLoadedEntries.add(fillup);
        }

        return new GappedEntries<>(newLoadedEntries, newGapIds, eventually);
    }
}
