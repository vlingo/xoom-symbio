// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
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
 * <p>
 * Note that the {@code id} provided herein is the identity assigned by the storage mechanism.
 * This may be a sequence number or an alphanumeric value. The important thing to note is
 * that this is provided by storage but may subsequently be used to represent time or order.
 *
 * @param <S> the native {@code Source<?>}
 * @param <E> the raw {@code Entry<?>}
 */
public interface EntryAdapter<S extends Source<?>,E extends Entry<?>> {
  /**
   * Answer the {@code ST} native state from the {@code E Entry<?>} state.
   * @param entry the {@code E Entry<?>} to adapt from
   * @param <ST> the {@code Source<?>} specified by the client
   * @return ST
   */
  @SuppressWarnings("unchecked")
  default <ST extends Source<?>> ST anyTypeFromEntry(final E entry) {
    final S source = fromEntry(entry);
    return (ST) source;
  }

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
  default E toEntry(final S source){
     return this.toEntry(source, Metadata.nullMetadata());
  }

  /**
   * Answer the {@code E Entry<?>} state from the {@code S Source<?>} native state.
   * @param source the {@code S Source<?>} native state
   * @param metadata the Metadata for this entry
   * @return E
   */
  E toEntry(final S source, final Metadata metadata);

  /**
   * Answer the {@code E Entry<?>} state from the {@code S Source<?>} native state.
   * @param source the {@code S Source<?>} native state
   * @param version the int state version with which source is associated
   * @param metadata the Metadata for this entry
   * @return E
   */
  default E toEntry(final S source, final int version, final Metadata metadata) {
    return toEntry(source, version, "", metadata);
  }

  /**
   * Answer the {@code E Entry<?>} state with its {@code id} from the {@code S Source<?>} native state.
   * @param source the {@code S Source<?>} native state
   * @param id the String unique identity to assign to the Entry
   * @return E
   */
  default E toEntry(final S source, final String id){
    return this.toEntry(source, id, Metadata.nullMetadata());
  }

  /**
   * Answer the {@code E Entry<?>} state with its {@code id} from the {@code S Source<?>} native state.
   * @param source the {@code S Source<?>} native state
   * @param id the String unique identity to assign to the Entry
   * @param metadata the Metadata for this entry
   * @return E
   */
  default E toEntry(final S source, final String id, final Metadata metadata) {
    return toEntry(source, Entry.DefaultVersion, id, metadata);
  }

  /**
   * Answer the {@code E Entry<?>} state with its {@code id} from the {@code S Source<?>} native state.
   * @param source the {@code S Source<?>} native state
   * @param version the int state version with which source is associated
   * @param id the String unique identity to assign to the Entry
   * @param metadata the Metadata for this entry
   * @return E
   */
  E toEntry(final S source, final int version, final String id, final Metadata metadata);
}
