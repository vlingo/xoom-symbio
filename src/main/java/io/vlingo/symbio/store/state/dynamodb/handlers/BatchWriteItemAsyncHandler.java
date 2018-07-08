package io.vlingo.symbio.store.state.dynamodb.handlers;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemRequest;
import com.amazonaws.services.dynamodbv2.model.BatchWriteItemResult;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;

public class BatchWriteItemAsyncHandler implements AsyncHandler<BatchWriteItemRequest, BatchWriteItemResult> {
    private final State<String> state;
    private final StateStore.WriteResultInterest<String> interest;

    public BatchWriteItemAsyncHandler(State<String> state, StateStore.WriteResultInterest<String> interest) {
        this.state = state;
        this.interest = interest;
    }

    @Override
    public void onError(Exception e) {
    }

    @Override
    public void onSuccess(BatchWriteItemRequest request, BatchWriteItemResult batchWriteItemResult) {
        interest.writeResultedIn(StateStore.Result.Success, state.id, state);
    }
}
