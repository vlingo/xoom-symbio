// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.object;

/**
 * Holder and provider of {@code StateObject} type mappers.
 * The {@code persistMapper} and {@code queryMapper} are received
 * and held as {@code Object} instances, but the underlying type
 * is implementation specific (e.g. JPA, Jdbi, Hibernate, etc.).
 * The specific types are cast when retrieving by means of the
 * methods {@code persistMapper()} and {@code queryMapper()}.
 */
public class StateObjectMapper {
  private final Object persistMapper;
  private final Object queryMapper;
  private final Class<?> type;

  /**
   * Answer a new {@code StateObjectMapper} with {@code type}, {@code persistMapper}, and {@code queryMapper}.
   * @param type the {@code Class<?>} type of the persistent object to be mapped
   * @param persistMapper the Object mapper of persistence information
   * @param queryMapper the Object mapper of query information
   * @return StateObjectMapper
   */
  public static StateObjectMapper with(final Class<?> type, final Object persistMapper, final Object queryMapper) {
    return new StateObjectMapper(type, persistMapper, queryMapper);
  }

  /**
   * Construct my state with {@code type}, {@code persistMapper}, and {@code queryMapper}.
   * @param type the {@code Class<?>} type of the persistent object to be mapped
   * @param persistMapper the Object mapper of persistence information
   * @param queryMapper the Object mapper of query information
   */
  public StateObjectMapper(final Class<?> type, final Object persistMapper, final Object queryMapper) {
    this.type = type;
    this.persistMapper = persistMapper;
    this.queryMapper = queryMapper;
  }

  /**
   * Answer my {@code persistMapper} as an {@code M}.
   * @param <M> the type to which my {@code persistMapper} is cast.
   * @return M
   */
  @SuppressWarnings("unchecked")
  public <M> M persistMapper() {
    return (M) persistMapper;
  }

  /**
   * Answer my {@code queryMapper} as an {@code M}.
   * @param <M> the type to which my {@code queryMapper} is cast.
   * @return M
   */
  @SuppressWarnings("unchecked")
  public <M> M queryMapper() {
    return (M) queryMapper;
  }

  /**
   * Answer my {@code type}.
   * @return {@code Class<?>}
   */
  public Class<?> type() {
    return type;
  }
}
