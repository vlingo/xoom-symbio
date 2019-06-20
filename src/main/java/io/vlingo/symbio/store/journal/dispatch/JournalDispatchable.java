// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.dispatch;

import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.dispatch.Dispatchable;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class JournalDispatchable<T, RS extends State<?>> extends Dispatchable {

  /**
   * The Collection of {@code Entry<T>} that was just successfully appended.
   */
  private final Collection<Entry<T>> entries;

  /**
   * Current {@code State<T>} of the stream, for which the entries was appended.
   */
  private final RS snapshot;

  public JournalDispatchable(String streamName, int streamVersion, Collection<Entry<T>> entries, RS snapshot) {
    super(getDispatchId(streamName, streamVersion, entries), LocalDateTime.now());
    this.entries = entries;
    this.snapshot = snapshot;
  }

  private static <T> String getDispatchId(String streamName, int streamVersion, Collection<Entry<T>> entries) {
    return streamName + ":" + streamVersion + ":"
            + entries.stream().map(Entry::id).collect(Collectors.joining(":"));
  }

  public Collection<Entry<T>> getEntries() {
    return entries;
  }

  public Optional<RS> getSnapshot() {
    return Optional.ofNullable(snapshot);
  }
}
