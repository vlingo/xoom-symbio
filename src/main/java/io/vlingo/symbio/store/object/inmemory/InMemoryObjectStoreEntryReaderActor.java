package io.vlingo.symbio.store.object.inmemory;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.reactivestreams.Stream;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.store.EntryReaderStream;
import io.vlingo.symbio.store.journal.JournalReader;
import io.vlingo.symbio.store.object.ObjectStoreEntryReader;

public class InMemoryObjectStoreEntryReaderActor extends Actor implements ObjectStoreEntryReader<Entry<String>> {
  private int currentIndex;
  private final EntryAdapterProvider entryAdapterProvider;
  private final List<Entry<String>> entriesView;
  private final String name;

  public InMemoryObjectStoreEntryReaderActor(final List<Entry<String>> entriesView, final String name) {
    this.entriesView = entriesView;
    this.name = name;
    this.entryAdapterProvider = EntryAdapterProvider.instance(stage().world());
    this.currentIndex = 0;
 }

  @Override
  public void close() {

  }

  @Override
  public Completes<String> name() {
    return completes().with(name);
  }

  @Override
  public Completes<Entry<String>> readNext() {
    if (currentIndex < entriesView.size()) {
      return completes().with(entriesView.get(currentIndex++));
    }
    return completes().with(null);
  }

  @Override
  public Completes<Entry<String>> readNext(final String fromId) {
    seekTo(fromId);
    return readNext();
  }

  @Override
  public Completes<List<Entry<String>>> readNext(final int maximumEntries) {
    final List<Entry<String>> entries = new ArrayList<>(maximumEntries);

    for (int count = 0; count < maximumEntries; ++count) {
      if (currentIndex < entriesView.size()) {
        entries.add(entriesView.get(currentIndex++));
      } else {
        break;
      }
    }
    return completes().with(entries);
  }

  @Override
  public Completes<List<Entry<String>>> readNext(final String fromId, final int maximumEntries) {
    seekTo(fromId);
    return readNext(maximumEntries);
  }

  @Override
  public void rewind() {
    this.currentIndex = 0;
  }

  @Override
  public Completes<String> seekTo(final String id) {
    final String currentId;

    switch (id) {
    case Beginning:
      rewind();
      currentId = readCurrentId();
      break;
    case End:
      end();
      currentId = readCurrentId();
      break;
    case Query:
      currentId = readCurrentId();
      break;
    default:
      to(id);
      currentId = readCurrentId();
      break;
    }

    return completes().with(currentId);
  }

  @Override
  public Completes<Long> size() {
    return completes().with((long) entriesView.size());
  }

  @Override
  @SuppressWarnings("unchecked")
  public Completes<Stream> streamAll() {
    return completes().with(new EntryReaderStream<>(stage(), selfAs(JournalReader.class), entryAdapterProvider));
  }

  private void end() {
    currentIndex = entriesView.size() - 1;
  }

  private String readCurrentId() {
    if (currentIndex < entriesView.size()) {
      final String currentId = entriesView.get(currentIndex).id();
      return currentId;
    }
    return "-1";
  }

  private void to(final String id) {
    rewind();
    while (currentIndex < entriesView.size()) {
      final Entry<String> entry = entriesView.get(currentIndex);
      if (entry.id().equals(id)) {
        return;
      }
      ++currentIndex;
    }
  }
}
