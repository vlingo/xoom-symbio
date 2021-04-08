package io.vlingo.xoom.symbio.store.gap;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.symbio.BaseEntry;
import io.vlingo.xoom.symbio.Entry;

import java.util.ArrayList;
import java.util.List;

public class RetryReaderActor extends Actor implements Reader {
    private GapRetryReader<Entry<String>> reader = null;
    private int offset = 0;

    GapRetryReader<Entry<String>> reader() {
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
        final List<Long> gapIds = reader().detectGaps(entry, offset);
        GappedEntries<Entry<String>> gappedEntries = new GappedEntries<>(new ArrayList<>(), gapIds, completesEventually());

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
        GappedEntries<Entry<String>> gappedEntries = new GappedEntries<>(entries, gapIds, completesEventually());
        offset += count;
        reader().readGaps(gappedEntries, 3, 10L, this::readIds);

        return completes();
    }
}
