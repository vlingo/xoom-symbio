package io.vlingo.symbio.store.state.dynamodb.handlers;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;

import static io.vlingo.symbio.store.state.dynamodb.DynamoDBTextStateActor.DISPATCHABLE_TABLE_NAME;

public class BatchWriteItemAsyncHandler implements AsyncHandler<BatchWriteItemRequest, BatchWriteItemResult> {
    private final State<String> state;
    private final StateStore.WriteResultInterest<String> interest;
    private final StateStore.Dispatchable<String> dispatchable;
    private final StateStore.Dispatcher dispatcher;

    public BatchWriteItemAsyncHandler(State<String> state, StateStore.WriteResultInterest<String> interest, StateStore.Dispatchable<String> dispatchable, StateStore.Dispatcher dispatcher) {
        this.state = state;
        this.interest = interest;
        this.dispatchable = dispatchable;
        this.dispatcher = dispatcher;
    }

    @Override
    public void onError(Exception e) {
        interest.writeResultedIn(StateStore.Result.NoTypeStore, state.id, null);
    }

    @Override
    public void onSuccess(BatchWriteItemRequest request, BatchWriteItemResult batchWriteItemResult) {
        interest.writeResultedIn(StateStore.Result.Success, state.id, state);
        dispatcher.dispatch(dispatchable.id, dispatchable.state.asTextState());
    }
}
