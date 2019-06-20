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
import java.util.Optional;

public class JournalDispatchable<T, RS extends State<?>> extends Dispatchable {

  /**
   * The {@code Entry<T>} that was just successfully appended.
   */
  private final Entry<T> entry;

  /**
   * Current {@code State<T>} of the stream, for which the entry was appended.
   */
  private final RS snapshot;

  public JournalDispatchable(String streamName, int streamVersion, Entry<T> entry, RS snapshot) {
    super(getDispatchId(streamName, streamVersion, entry), LocalDateTime.now());
    this.entry = entry;
    this.snapshot = snapshot;
  }

  public JournalDispatchable(String id) {
    super(id, null);
    this.entry = null;
    this.snapshot = null;
  }

  private static <T> String getDispatchId(String streamName, int streamVersion, Entry<T> entry) {
    return streamName + ":" + streamVersion + ":" + entry.id();
  }

  public Entry<T> getEntry() {
    return entry;
  }

  public Optional<RS> getSnapshot() {
    return Optional.ofNullable(snapshot);
  }
}
