// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.state;

import io.vlingo.common.Completes;
import io.vlingo.reactivestreams.Stream;
import io.vlingo.symbio.store.QueryExpression;
import io.vlingo.symbio.store.state.StateStore.ReadResultInterest;

/**
 * Defines the reader of the {@code StateStore}.
 */
public interface StateStoreReader {
  /**
   * Read the state identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param type the {@code Class<?>} type of the state to read
   * @param interest the ReadResultInterest to which the result is dispatched
   */
  default void read(final String id, final Class<?> type, final ReadResultInterest interest) {
    read(id, type, interest, null);
  }

  /**
   * Read the state identified by {@code id} and dispatch the result to the {@code interest}.
   * @param id the String unique identity of the state to read
   * @param type the {@code Class<?>} type of the state to read
   * @param interest the ReadResultInterest to which the result is dispatched
   * @param object an Object that will be sent to the ReadResultInterest when the read has succeeded or failed
   */
  void read(final String id, final Class<?> type, final ReadResultInterest interest, final Object object);

  /**
   * Answer a new {@code Stream} for flowing all of the instances of the {@code stateType}.
   * Elements are streamed as type {@code StateBundle} to the {@code Sink<StateBundle>}.
   * @param stateType the {@code Class<?>} of the state to read
   * @return {@code Completes<Stream>}
   */
  Completes<Stream> streamAllOf(final Class<?> stateType);

  /**
   * Answer a new {@code Stream} for flowing all instances per {@code query}. Currently
   * the only supported query types are {@code QueryExpression} (no query parameters), and
   * {@code ListQueryExpression} (a {@code List<?>} of {@code Object} parameters).
   * In the future {@code ListQueryExpression} will be supported. Elements are streamed as
   * type {@code StateBundle} to the {@code Sink<StateBundle>}.
   * @param query the QueryExpression used to constrain the Stream
   * @return {@code Completes<Stream>}
   */
  Completes<Stream> streamSomeUsing(final QueryExpression query);
}
