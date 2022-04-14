// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state;

import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.state.StateStore.WriteResultInterest;

import java.util.List;

/**
 * Defines the writer of the {@code StateStore}.
 */
public interface StateStoreWriter {
  /**
   * Write the {@code state} identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param <S> the concrete type of the state
   */
  default <S> void write(final String id, final S state, final int stateVersion, final WriteResultInterest interest) {
    write(id, state, stateVersion, Source.none(), Metadata.nullMetadata(), interest, null);
  }

  /**
   * Write the {@code state} identified by {@code id} along with appending {@code sources}
   * and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param sources the {@code List<Source<C>>} to append
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param <S> the concrete type of the state
   * @param <C> the concrete type of the sources
   */
  default <S,C> void write(final String id, final S state, final int stateVersion, final List<Source<C>> sources, final WriteResultInterest interest) {
    write(id, state, stateVersion, sources, Metadata.nullMetadata(), interest, null);
  }

  /**
   * Write the {@code state} identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param metadata the Metadata for the state
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param <S> the concrete type of the state
   */
  default <S> void write(final String id, final S state, final int stateVersion, final Metadata metadata, final WriteResultInterest interest) {
    write(id, state, stateVersion, Source.none(), metadata, interest, null);
  }

  /**
   * Write the {@code state} identified by {@code id} along with appending {@code sources}
   * and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param sources the {@code List<Source<C>>} to append
   * @param metadata the Metadata for the state
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param <S> the concrete type of the state
   * @param <C> the concrete type of the sources
   */
  default <S,C> void write(final String id, final S state, final int stateVersion, final List<Source<C>> sources, final Metadata metadata, final WriteResultInterest interest) {
    write(id, state, stateVersion, sources, metadata, interest, null);
  }

  /**
   * Write the {@code state} identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param object an Object that will be sent to the WriteResultInterest when the write has succeeded or failed
   * @param <S> the concrete type of the state
   */
  default <S> void write(final String id, final S state, final int stateVersion, final WriteResultInterest interest, final Object object) {
    write(id, state, stateVersion, Source.none(), Metadata.nullMetadata(), interest, object);
  }

  /**
   * Write the {@code state} identified by {@code id} along with appending {@code sources}
   * and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param sources the {@code List<Source<C>>} to append
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param object an Object that will be sent to the WriteResultInterest when the write has succeeded or failed
   * @param <S> the concrete type of the state
   * @param <C> the concrete type of the sources
   */
  default <S,C> void write(final String id, final S state, final int stateVersion, final List<Source<C>> sources, final WriteResultInterest interest, final Object object) {
    write(id, state, stateVersion, sources, Metadata.nullMetadata(), interest, object);
  }

  /**
   * Write the {@code state} identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param metadata the Metadata for the state
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param object an Object that will be sent to the WriteResultInterest when the write has succeeded or failed
   * @param <S> the concrete type of the state
   */
  default <S> void write(final String id, final S state, final int stateVersion, final Metadata metadata, final WriteResultInterest interest, final Object object) {
    write(id, state, stateVersion, Source.none(), metadata, interest, object);
  }

  /**
   * Write the {@code state} identified by {@code id} along with appending {@code sources}
   * and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param state the S typed state instance
   * @param stateVersion the int version of the state
   * @param sources the {@code List<Source<C>>} to append
   * @param metadata the Metadata for the state
   * @param interest the WriteResultInterest to which the result is dispatched
   * @param object an Object that will be sent to the WriteResultInterest when the write has succeeded or failed
   * @param <S> the concrete type of the state
   * @param <C> the concrete type of the sources
   */
  <S,C> void write(final String id, final S state, final int stateVersion, final List<Source<C>> sources, final Metadata metadata, final WriteResultInterest interest, final Object object);
}
