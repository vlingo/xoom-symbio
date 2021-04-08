// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Failure;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.common.Success;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.symbio.Metadata;
import io.vlingo.xoom.symbio.store.QueryExpression;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.state.StateStore.ReadResultInterest;
import io.vlingo.xoom.symbio.store.state.StateStore.TypedStateBundle;

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
   * Read the states identified by {@code id} within {@code bundles} and dispatch the result to the {@code interest}.
   * @param bundles the {@code Collection<TypedStateBundle>} defining the states to read
   * @param interest the ReadResultInterest to which the result is dispatched
   * @param object an Object that will be sent to the ReadResultInterest when the read has succeeded or failed
   */
  void readAll(final Collection<TypedStateBundle> bundles, final ReadResultInterest interest, final Object object);

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

  /**
   * Collects results of multiple reads using {@code StateStoreReader#readAll()}.
   */
  static class ReadAllResultCollector implements ReadResultInterest {
    private final List<TypedStateBundle> readBundles;
    private final AtomicBoolean success;
    private final AtomicReference<Outcome<StorageException, Result>> readOutcome;

    public ReadAllResultCollector() {
      this.readBundles = new ArrayList<>();
      this.success = new AtomicBoolean(true);
      this.readOutcome = new AtomicReference<>(Success.of(Result.Success));
    }

    /**
     * Prepares results collectors.
     */
    public void prepare() {
      readBundles.clear();
      success.set(true);
      readOutcome.set(Success.of(Result.Success));
    }

    /**
     * Single result collector.
     */
    @Override
    public <S> void readResultedIn(
            final Outcome<StorageException, Result> outcome,
            final String id,
            final S state,
            final int stateVersion,
            final Metadata metadata,
            final Object object) {
      outcome.andThen(result -> {
        readBundles.add(new TypedStateBundle(id, state, stateVersion, metadata));
        return result;
      })
      .otherwise(cause -> {
        readOutcome.set(outcome);
        success.set(false);
        return cause.result;
      });
    }

    /**
     * Answer my {@code List<TypedStateBundle>}.
     * @return {@code List<TypedStateBundle>}
     */
    public List<TypedStateBundle> readResultBundles() {
      return readBundles;
    }

    /**
     * Answer my {@code Outcome<StorageException, Result>}.
     * @param expectedReads the int number of excepted reads
     * @return {@code Outcome<StorageException, Result>}
     */
    public Outcome<StorageException, Result> readResultOutcome(final int expectedReads) {
      if (isFailure()) {
        if (!readBundles.isEmpty() && readBundles.size() < expectedReads) {
          return Failure.of(new StorageException(Result.NotAllFound, "Not all states were found."));
        }
      }

      return readOutcome.get();
    }

    /**
     * Answer whether or not my reads were a complete success.
     * @return boolean
     */
    public boolean isSuccess() {
      return success.get();
    }

    /**
     * Answer whether or not my reads were a complete or partial failure.
     * @return boolean
     */
    public boolean isFailure() {
      return !isSuccess();
    }
  }
}
