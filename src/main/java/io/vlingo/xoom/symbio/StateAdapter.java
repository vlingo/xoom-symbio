// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio;

/**
 * Adapts the native state to the raw {@code State<?>}, and the raw {@code State<?>} to the native state.
 *
 * @param <S> the native type of the state
 * @param <RS> the raw {@code State<?>} of the state
 */
public interface StateAdapter<S,RS extends State<?>> {

  /**
   * Answer the state type's version, as in the number of times
   * the type has been defined and redefined.
   * @return int
   */
  int typeVersion();

  /**
   * Answer the {@code S} native state instance.
   * @param raw the {@code State<?>} instance from which the native state is derived
   * @return S
   */
  S fromRawState(final RS raw);

  /**
   * Answer the {@code ST} native state instance.
   * @param raw the {@code State<?>} instance from which the native state is derived
   * @param stateType the {@code Class<ST>} type to which to convert
   * @param <ST> the state type to which to convert
   * @return {@code ST}
   */
  <ST> ST fromRawState(final RS raw, Class<ST> stateType);

  /**
   * Answer the {@code RS} raw {@code State<?>} instance of the {@code S} instance.
   * @param id the String identity of the state
   * @param state the {@code S} native state instance
   * @param stateVersion the int state version
   * @param metadata the Metadata for this state
   * @return RS
   */
  default RS toRawState(final String id, final S state, final int stateVersion, final Metadata metadata) {
    throw new UnsupportedOperationException("Must override.");
  }

  /**
   * Answer the {@code RS} raw {@code State<?>} instance of the {@code S} instance.
   * @param state the {@code S} native state instance
   * @param stateVersion the int state version
   * @param metadata the Metadata for this state
   * @return RS
   */
  default RS toRawState(final S state, final int stateVersion, final Metadata metadata) {
    return toRawState(State.NoOp, state, stateVersion, metadata);
  }

  /**
   * Answer the {@code RS} raw {@code State<?>} instance of the {@code S} instance
   * using the {@code Metadata.nullMetadata()}.
   * @param state the {@code S} native state instance
   * @param stateVersion the int state version
   * @return RS
   */
  default RS toRawState(final S state, final int stateVersion) {
    return toRawState(state, stateVersion, Metadata.nullMetadata());
  }
}
