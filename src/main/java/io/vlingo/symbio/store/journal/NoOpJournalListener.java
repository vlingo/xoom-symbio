// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal;

import java.util.List;

import io.vlingo.symbio.Entry;
import io.vlingo.symbio.State;

/**
 * All dispatching to this `JournalListener` are ignored.
 */
public class NoOpJournalListener implements JournalListener<Object> {
  /**
   * @see io.vlingo.symbio.store.journal.JournalListener#appended(io.vlingo.symbio.Entry)
   */
  @Override
  public void appended(final Entry<Object> entry) { }

  /**
   * @see io.vlingo.symbio.store.journal.JournalListener#appendedWith(io.vlingo.symbio.Entry, io.vlingo.symbio.State)
   */
  @Override
  public void appendedWith(final Entry<Object> entry, final State<Object> snapshot) { }

  /**
   * @see io.vlingo.symbio.store.journal.JournalListener#appendedAll(java.util.List)
   */
  @Override
  public void appendedAll(final List<Entry<Object>> entries) { }

  /**
   * @see io.vlingo.symbio.store.journal.JournalListener#appendedAllWith(java.util.List, io.vlingo.symbio.State)
   */
  @Override
  public void appendedAllWith(final List<Entry<Object>> entries, final State<Object> snapshot) { }
}
