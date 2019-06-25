// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store.journal;

import io.vlingo.actors.Actor;
import io.vlingo.actors.DeadLetter;
import io.vlingo.actors.LocalMessage;
import io.vlingo.actors.Mailbox;
import io.vlingo.common.Outcome;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.Source;
import io.vlingo.symbio.store.Result;
import io.vlingo.symbio.store.StorageException;
import io.vlingo.symbio.store.journal.Journal.AppendResultInterest;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class JournalAppendResultInterest__Proxy implements io.vlingo.symbio.store.journal.Journal.AppendResultInterest {

  private static final String appendResultedInRepresentation1 = "appendResultedIn(io.vlingo.common.Outcome<io.vlingo.symbio.store.StorageException, io.vlingo.symbio.store.Result>, java.lang.String, int, io.vlingo.symbio.Source<S>, java.util.Optional<ST>, java.lang.Object)";
  private static final String appendResultedInRepresentation2 = "appendResultedIn(io.vlingo.common.Outcome<io.vlingo.symbio.store.StorageException, io.vlingo.symbio.store.Result>, java.lang.String, int, io.vlingo.symbio.Source<S>, io.vlingo.symbio.Metadata,  java.util.Optional<ST>, java.lang.Object)";
  private static final String appendAllResultedInRepresentation1 = "appendAllResultedIn(io.vlingo.common.Outcome<io.vlingo.symbio.store.StorageException, io.vlingo.symbio.store.Result>, java.lang.String, int, java.util.List<io.vlingo.symbio.Source<S>>, java.util.Optional<ST>, java.lang.Object)";
  private static final String appendAllResultedInRepresentation2 = "appendAllResultedIn(io.vlingo.common.Outcome<io.vlingo.symbio.store.StorageException, io.vlingo.symbio.store.Result>, java.lang.String, int, java.util.List<io.vlingo.symbio.Source<S>>, io.vlingo.symbio.Metadata, java.util.Optional<ST>, java.lang.Object)";

  private final Actor actor;
  private final Mailbox mailbox;

  public JournalAppendResultInterest__Proxy(final Actor actor, final Mailbox mailbox){
    this.actor = actor;
    this.mailbox = mailbox;
  }

  public <S,ST>void appendResultedIn(io.vlingo.common.Outcome<io.vlingo.symbio.store.StorageException, io.vlingo.symbio.store.Result> arg0, java.lang.String arg1, int arg2, io.vlingo.symbio.Source<S> arg3, java.util.Optional<ST> arg4, java.lang.Object arg5) {
    final java.util.function.Consumer<AppendResultInterest> consumer = (actor) -> actor.appendResultedIn(arg0, arg1, arg2, arg3, arg4, arg5);
    send(JournalAppendResultInterest__Proxy.appendResultedInRepresentation1, consumer);
  }

  @Override
  public <S, ST> void appendResultedIn(Outcome<StorageException, Result> outcome, String streamName, int streamVersion,
          Source<S> source, Metadata metadata, Optional<ST> snapshot, Object object) {
    final java.util.function.Consumer<AppendResultInterest> consumer = (actor) -> actor.appendResultedIn(outcome, streamName, streamVersion, source, metadata, snapshot, object);
    send(JournalAppendResultInterest__Proxy.appendResultedInRepresentation2, consumer);
  }

  public <S,ST>void appendAllResultedIn(io.vlingo.common.Outcome<io.vlingo.symbio.store.StorageException, io.vlingo.symbio.store.Result> arg0, java.lang.String arg1, int arg2, java.util.List<io.vlingo.symbio.Source<S>> arg3, java.util.Optional<ST> arg4, java.lang.Object arg5) {
    final java.util.function.Consumer<AppendResultInterest> consumer = (actor) -> actor.appendAllResultedIn(arg0, arg1, arg2, arg3, arg4, arg5);
    send(JournalAppendResultInterest__Proxy.appendAllResultedInRepresentation1, consumer);
  }

  @Override
  public <S, ST> void appendAllResultedIn(Outcome<StorageException, Result> outcome, String streamName,
          int streamVersion, List<Source<S>> sources, Metadata metadata, Optional<ST> snapshot, Object object) {
    final java.util.function.Consumer<AppendResultInterest> consumer = (actor) -> actor.appendAllResultedIn(outcome, streamName, streamVersion, sources, metadata, snapshot, object);
    send(JournalAppendResultInterest__Proxy.appendAllResultedInRepresentation2, consumer);
  }

  private void send(String representation, Consumer<AppendResultInterest> consumer) {
    if (!actor.isStopped()) {
      if (mailbox.isPreallocated()) {
        mailbox.send(actor, AppendResultInterest.class, consumer, null, representation);
      } else {
        mailbox.send(new LocalMessage<>(actor, AppendResultInterest.class, consumer, representation));
      }
    } else {
      actor.deadLetters().failedDelivery(new DeadLetter(actor, representation));
    }
  }

}
