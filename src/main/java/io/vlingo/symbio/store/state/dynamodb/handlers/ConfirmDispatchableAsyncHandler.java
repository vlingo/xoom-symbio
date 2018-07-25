package io.vlingo.symbio.store.state.dynamodb.handlers;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.DeleteItemResult;
import io.vlingo.symbio.store.state.StateStore;

public class ConfirmDispatchableAsyncHandler implements AsyncHandler<DeleteItemRequest, DeleteItemResult> {
    private final String dispatchId;
    private final StateStore.ConfirmDispatchedResultInterest interest;

    public ConfirmDispatchableAsyncHandler(String dispatchId, StateStore.ConfirmDispatchedResultInterest interest) {
        this.dispatchId = dispatchId;
        this.interest = interest;
    }

    @Override
    public void onError(Exception e) {
        interest.confirmDispatchedResultedIn(StateStore.Result.Failure, dispatchId);
    }

    @Override
    public void onSuccess(DeleteItemRequest request, DeleteItemResult deleteItemResult) {
        interest.confirmDispatchedResultedIn(StateStore.Result.Success, dispatchId);
    }
}
