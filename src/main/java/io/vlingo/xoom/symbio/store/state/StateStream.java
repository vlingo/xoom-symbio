package io.vlingo.xoom.symbio.store.state;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.reactivestreams.Elements;
import io.vlingo.xoom.reactivestreams.PublisherConfiguration;
import io.vlingo.xoom.reactivestreams.Sink;
import io.vlingo.xoom.reactivestreams.Source;
import io.vlingo.xoom.reactivestreams.Stream;
import io.vlingo.xoom.reactivestreams.StreamPublisher;
import io.vlingo.xoom.reactivestreams.StreamSubscriber;
import io.vlingo.xoom.reactivestreams.Streams;
import io.vlingo.xoom.symbio.State;
import io.vlingo.xoom.symbio.StateAdapterProvider;
import io.vlingo.xoom.symbio.StateBundle;

public class StateStream<RS extends State<?>> implements Stream {
  private long flowElementsRate;
  private Publisher<RS> publisher;
  private final Stage stage;
  private final Map<String, RS> states;
  private final StateAdapterProvider stateAdapterProvider;
  private StateStreamSubscriber<RS> subscriber;

  public StateStream(final Stage stage, final Map<String, RS> states, final StateAdapterProvider stateAdapterProvider) {
    this.stage = stage;
    this.stateAdapterProvider = stateAdapterProvider;
    this.states = states;
  }

  @Override
  public void request(long flowElementsRate) {
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

    publisher = stage.actorFor(Publisher.class, StreamPublisher.class, new StateSource<>(states, stateAdapterProvider, flowElementsRate), configuration);

    final Subscriber<RS> subscriber =
            stage.actorFor(
                    Subscriber.class,
                    StateStreamSubscriber.class,
                    sink,
                    flowElementsRate,
                    this);

    publisher.subscribe(subscriber);
  }

  @Override
  public void stop() {
    subscriber.subscriptionHook.cancel();
  }

  private static final class StateSource<RS extends State<?>> implements Source<RS> {
    private final long flowElementsRate;
    private final Iterator<String> iterator;
    private final Map<String, RS> states;
    private final StateAdapterProvider stateAdapterProvider;

    public StateSource(final Map<String, RS> states, final StateAdapterProvider stateAdapterProvider, final long flowElementsRate) {
      this.states = states;
      this.iterator = states.keySet().iterator();
      this.stateAdapterProvider = stateAdapterProvider;
      this.flowElementsRate = flowElementsRate;
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Completes<Elements<RS>> next() {
      if (iterator.hasNext()) {
        int count = 0;
        final List<StateBundle> next = new ArrayList<>();
        while (iterator.hasNext() && count++ < flowElementsRate) {
          final String id = iterator.next();
          final State<?> state = states.get(id);
          final Object object = stateAdapterProvider.fromRaw(state);
          next.add(new StateBundle(state, object));
        }
        final Elements elements = Elements.of(arrayFrom(next));
        return Completes.withSuccess(elements);
      }
      return Completes.withSuccess(Elements.terminated());
    }

    @Override
    public Completes<Elements<RS>> next(final int maximumElements) {
      return next();
    }

    @Override
    public Completes<Elements<RS>> next(final long index) {
      return next();
    }

    @Override
    public Completes<Elements<RS>> next(final long index, final int maximumElements) {
      return next();
    }

    @Override
    public Completes<Boolean> isSlow() {
      return Completes.withSuccess(false);
    }

    private StateBundle[] arrayFrom(final List<StateBundle> states) {
      return states.toArray(new StateBundle[states.size()]);
    }
  }

  public static class StateStreamSubscriber<RS extends State<?>> extends StreamSubscriber<RS> {
    Subscription subscriptionHook;

    public StateStreamSubscriber(
            final Sink<RS> sink,
            final long requestThreshold,
            final StateStream<RS> stateStream) {

      super(sink, requestThreshold);

      stateStream.subscriber = this;
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
