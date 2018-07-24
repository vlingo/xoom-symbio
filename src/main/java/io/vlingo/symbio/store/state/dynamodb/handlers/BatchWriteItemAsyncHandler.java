package io.vlingo.symbio.store.state.dynamodb.handlers;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;

import java.util.function.Function;

public class BatchWriteItemAsyncHandler<T> implements AsyncHandler<BatchWriteItemRequest, BatchWriteItemResult> {
    private final State<T> state;
    private final StateStore.WriteResultInterest<T> interest;
    private final StateStore.Dispatchable<T> dispatchable;
    private final StateStore.Dispatcher dispatcher;
    private final State<T> nullState;
    private final Function<StateStore.Dispatchable<T>, Void> dispatchState;

    public BatchWriteItemAsyncHandler(State<T> state, StateStore.WriteResultInterest<T> interest, StateStore.Dispatchable<T> dispatchable, StateStore.Dispatcher dispatcher, State<T> nullState, Function<StateStore.Dispatchable<T>, Void> dispatchState) {
        this.state = state;
        this.interest = interest;
        this.dispatchable = dispatchable;
        this.dispatcher = dispatcher;
        this.nullState = nullState;
        this.dispatchState = dispatchState;
    }

    @Override
    public void onError(Exception e) {
        interest.writeResultedIn(StateStore.Result.NoTypeStore, new IllegalStateException(e), state.id, nullState);
    }

    @Override
    public void onSuccess(BatchWriteItemRequest request, BatchWriteItemResult batchWriteItemResult) {
        interest.writeResultedIn(StateStore.Result.Success, state.id, state);
        dispatchState.apply(dispatchable);
    }
}
