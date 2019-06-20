// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal.inmemory;

import io.vlingo.actors.testkit.AccessSafely;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.journal.Journal.AppendResultInterest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class MockAppendResultInterest<T, ST> implements AppendResultInterest {
  private AccessSafely access;
  private final List<JournalData<T, ST>> entries = new ArrayList<>();

  @Override
  public <S, ST> void appendResultedIn(Outcome<StorageException, Result> outcome, String streamName, int streamVersion,
          Source<S> source, Optional<ST> snapshot, Object object) {
    outcome.andThen(result -> {
      access.writeUsing("appendResultedIn",
              new JournalData<>(streamName, streamVersion, null, result, Collections.singletonList(source), snapshot));
      return result;
    }).otherwise(cause -> {
      access.writeUsing("appendResultedIn",
              new JournalData<>(streamName, streamVersion, cause, null, Collections.singletonList(source), snapshot));
      return cause.result;
    });
  }

  @Override
  public <S, ST> void appendAllResultedIn(Outcome<StorageException, Result> outcome, String streamName,
          int streamVersion, List<Source<S>> sources, Optional<ST> snapshot, Object object) {
    outcome.andThen(result -> {
      access.writeUsing("appendResultedIn",
              new JournalData<>(streamName, streamVersion, null, result, sources, snapshot));
      return result;
    }).otherwise(cause -> {
      access.writeUsing("appendResultedIn",
              new JournalData<>(streamName, streamVersion, cause, null, sources, snapshot));
      return cause.result;
    });
  }

  public AccessSafely afterCompleting(final int times) {
    access = AccessSafely.afterCompleting(times)
            .writingWith("appendResultedIn", (Consumer<JournalData<T, ST>>) this.entries::add)
            .readingWith("appendResultedIn", () -> this.entries)
            .readingWith("size", entries::size);

    return access;
  }

  int getReceivedAppendsSize(){
    return (int) access.readFrom("size");
  }

  List<JournalData<T,ST>> getEntries(){
    return access.readFrom("appendResultedIn");
  }

  public static class JournalData<T, ST> {
    public final String streamName;
    public final int streamVersion;
    public final List<Source<T>> sources;
    public final Optional<ST> snapshot;

    public final Exception errorCauses;
    public final Result result;

    public JournalData(String streamName, int streamVersion, Exception errorCauses, Result result,
            List<Source<T>> sources, Optional<ST> snapshot) {
      this.streamName = streamName;
      this.streamVersion = streamVersion;
      this.errorCauses = errorCauses;
      this.result = result;
      this.sources = sources;
      this.snapshot = snapshot;
    }
  }

}
