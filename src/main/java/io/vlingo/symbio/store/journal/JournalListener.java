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
 * A listener for all appends made to a given {@code Journal<T>} that must be registered
 * upon creation of the given {@code Journal<T>} instance. The registration is therefore
 * implementation dependent.
 *
 * @param <T> the concrete type of {@code Entry<T>} stored and read, which maybe be String, byte[], or Object
 */
public interface JournalListener<T> {
  
  /**
   * NoListener is a {@link JournalListener} that ignores all notifications.
   */
  public static JournalListener<?> NoListener = new NoListener<>();
  
  /**
   * Indicates that entry was just successfully appended.
   * 
   * @param entry the {@code Entry<T>} that was just successfully appended
   */
  void appended(final Entry<T> entry);

  /**
   * Indicates that within the same transaction the entry was just successfully
   * appended and the snapshot alone with it.
   * 
   * @param entry the {@code Entry<T>} that was just successfully appended
   * @param snapshot the current {@code State<T>} of the stream for which the entry was appended
   */
  void appendedWith(final Entry<T> entry, final State<T> snapshot);

  /**
   * Indicates that all entries were just successfully appended.
   * 
   * @param entries the {@code List<Entry<T>>} that were just successfully appended
   */
  void appendedAll(final List<Entry<T>> entries);

  /**
   * Indicates that within the same transaction the entry was just successfully
   * appended and the snapshot alone with it.
   * 
   * @param entries the {@code List<Entry<T>>} that were just successfully appended
   * @param snapshot the current {@code State<T>} of the stream for which the entry was appended
   */
  void appendedAllWith(final List<Entry<T>> entries, final State<T> snapshot);
  
  /**
   * NoListener is a {@link JournalListener} that ignores all notifications.
   */
  static class NoListener<T> implements JournalListener<T> {

    @Override
    public void appended(Entry<T> entry) {}

    @Override
    public void appendedWith(Entry<T> entry, State<T> snapshot) {}

    @Override
    public void appendedAll(List<Entry<T>> entries) {}

    @Override
    public void appendedAllWith(List<Entry<T>> entries, State<T> snapshot) {}
  }
}
