// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.symbio.store;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.reactivestreams.PublisherConfiguration;
import io.vlingo.xoom.reactivestreams.Sink;
import io.vlingo.xoom.reactivestreams.Source;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.reactivestreams.StreamPublisher;
import io.vlingo.xoom.reactivestreams.StreamSubscriber;
import io.vlingo.xoom.reactivestreams.Streams;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.EntryAdapterProvider;

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
