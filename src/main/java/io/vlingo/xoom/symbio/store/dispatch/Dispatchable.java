// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.dispatch;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.State;

/**
 * Defines the data holder for identity and state that has been
 * successfully stored and is then dispatched to registered
 * interests.
 *
 * @param <RS> the concrete {@code State<?>} type of the storage
 * @param <E> the concrete {@code Entry<?>} type of the entries
 */
public class Dispatchable<E extends Entry<?>, RS extends State<?>> {
  /**
   * My String unique identity.
   */
  private final String id;

  /**
   * The moment when I was persistently created.
   */
  private final LocalDateTime createdOn;

  /**
   * My R concrete {@code State<?>} type.
   */
  private final RS state;

  /**
   * My {@code List<Entry<?>>} to dispatch
   */
  private final List<E> entries;

  public Dispatchable(final String id, final LocalDateTime createdOn, final RS state, final List<E> entries) {
    this.id = id;
    this.createdOn = createdOn;
    this.state = state;
    this.entries = entries;
  }

  public String id() {
    return id;
  }

  public LocalDateTime createdOn() {
    return createdOn;
  }

  public Optional<RS> state() {
    return Optional.ofNullable(state);
  }

  public List<E> entries() {
    return entries;
  }

  public boolean hasEntries() {
    return entries != null && !entries.isEmpty();
  }

  @Override
  @SuppressWarnings({ "rawtypes" })
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Dispatchable that = (Dispatchable) o;
    return id.equals(that.id);
  }


  /**
   * Answer the state as an instance of specific type {@code State<S>}.
   * @return {@code State<S>}
   * @param <S> the type of the state, String or byte[]
   */
  @SuppressWarnings("unchecked")
  public <S> State<S> typedState() {
    return (State<S>) state;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
