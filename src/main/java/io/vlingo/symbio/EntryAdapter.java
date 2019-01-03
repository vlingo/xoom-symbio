// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio;

/**
 * Adapts the native {@code Source<?>} state to the raw {@code Entry<?>},
 * and the raw {@code Entry<?>} to the native {@code Source<?>}.
 *
 * @param <S> the native {@code Source<?>}
 * @param <E> the raw {@code Entry<?>}
 */
public interface EntryAdapter<S extends Source<?>,E extends Entry<?>> {
  /**
   * Answer the {@code S Source<?>} native state from the {@code E Entry<?>} state.
   * @param entry the {@code E Entry<?>} to adapt from
   * @return S
   */
  S fromEntry(final E entry);

  /**
   * Answer the {@code E Entry<?>} state from the {@code S Source<?>} native state.
   * @param source the {@code S Source<?>} native state
   * @return E
   */
  E toEntry(final S source);
}
