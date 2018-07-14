package io.vlingo.symbio.store.state.dynamodb.handlers;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.dynamodb.adapters.TextStateRecordAdapter;

import java.util.Map;

public class GetEntityAsyncHandler implements AsyncHandler<GetItemRequest, GetItemResult> {
    private static final State<String> NO_STATE = State.NullState.Text;
    private final String id;
    private final StateStore.ReadResultInterest<String> interest;

    public GetEntityAsyncHandler(String id, StateStore.ReadResultInterest<String> interest) {
        this.id = id;
        this.interest = interest;
    }

    @Override
    public void onError(Exception e) {
        interest.readResultedIn(StateStore.Result.NoTypeStore, new IllegalStateException(e), id, NO_STATE);
    }

    @Override
    public void onSuccess(GetItemRequest request, GetItemResult getItemResult) {
        Map<String, AttributeValue> item = getItemResult.getItem();
        if (item == null) {
            interest.readResultedIn(StateStore.Result.NotFound, id, NO_STATE);
            return;
        }

        try {
            State<String> state = TextStateRecordAdapter.unmarshallState(item);
            interest.readResultedIn(StateStore.Result.Success, id, state);
        } catch (ClassNotFoundException e) {
            interest.readResultedIn(StateStore.Result.Failure, e, id, NO_STATE);
        }
    }
}
