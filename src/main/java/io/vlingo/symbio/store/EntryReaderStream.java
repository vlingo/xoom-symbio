// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.symbio.store;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.vlingo.actors.Stage;
import io.vlingo.reactivestreams.PublisherConfiguration;
import io.vlingo.reactivestreams.Sink;
import io.vlingo.reactivestreams.Source;
import io.vlingo.reactivestreams.Stream;
import io.vlingo.reactivestreams.StreamPublisher;
import io.vlingo.reactivestreams.StreamSubscriber;
import io.vlingo.reactivestreams.Streams;
import io.vlingo.symbio.Entry;
import io.vlingo.symbio.EntryAdapterProvider;

public class EntryReaderStream<T extends Entry<?>> implements Stream {
  private final EntryAdapterProvider entryAdapterProvider;
  private long flowElementsRate;
  private final EntryReader<T> entryReader;
  private Source<T> entryReaderSource;
  private Publisher<T> publisher;
  private final Stage stage;
  private EntryStreamSubscriber<T> subscriber;

  public EntryReaderStream(final Stage stage, final EntryReader<T> entryReader, final EntryAdapterProvider entryAdapterProvider) {
    this.stage = stage;
    this.entryReader = entryReader;
    this.entryAdapterProvider = entryAdapterProvider;
  }

  @Override
  public void request(final long flowElementsRate) {
    this.flowElementsRate = flowElementsRate;

    subscriber.subscriptionHook.request(this.flowElementsRate);
  }

  @Override
  public <S> void flowInto(final Sink<S> sink) {
    flowInto(sink, DefaultFlowRate, DefaultProbeInterval);
  }

  @Override
  public <S> void flowInto(final Sink<S> sink, final long flowElementsRate) {
    flowInto(sink, flowElementsRate, DefaultProbeInterval);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <S> void flowInto(final Sink<S> sink, final long flowElementsRate, final int probeInterval) {
    this.flowElementsRate = flowElementsRate;

    final PublisherConfiguration configuration =
            PublisherConfiguration.with(
                    probeInterval,
                    Streams.DefaultMaxThrottle,
                    Streams.DefaultBufferSize,
                    Streams.OverflowPolicy.DropCurrent);

    entryReaderSource = stage.actorFor(Source.class, EntryReaderSource.class, entryReader, entryAdapterProvider, flowElementsRate);

    publisher = stage.actorFor(Publisher.class, StreamPublisher.class, entryReaderSource, configuration);

    final Subscriber<T> subscriber = stage.actorFor(Subscriber.class, EntryStreamSubscriber.class, sink, flowElementsRate);

    publisher.subscribe(subscriber);
  }

  @Override
  public void stop() {
    subscriber.subscriptionHook.cancel();
  }

  public static class EntryStreamSubscriber<T> extends StreamSubscriber<T> {
    Subscription subscriptionHook;

    public EntryStreamSubscriber(final Sink<T> sink, final long requestThreshold) {
      super(sink, requestThreshold);
    }

    @Override
    public void onComplete() {
      super.onComplete();
    }

    @Override
    public void onSubscribe(final Subscription subscription) {
      this.subscriptionHook = subscription;

      super.onSubscribe(subscription);
    }
  }
}
