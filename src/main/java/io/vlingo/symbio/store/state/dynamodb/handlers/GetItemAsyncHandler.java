package io.vlingo.symbio.store.state.dynamodb.handlers;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.dynamodb.StateRecordAdapter;

import java.util.Map;

public class GetItemAsyncHandler implements AsyncHandler<GetItemRequest, GetItemResult> {
    private final StateStore.ReadResultInterest<String> interest;

    public GetItemAsyncHandler(StateStore.ReadResultInterest<String> interest) {
        this.interest = interest;
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onSuccess(GetItemRequest request, GetItemResult getItemResult) {
        Map<String, AttributeValue> item = getItemResult.getItem();
        try {
            State<String> state = StateRecordAdapter.unmarshall(item);
            interest.readResultedIn(StateStore.Result.Success, state.id, state);
        } catch (ClassNotFoundException e) {
            interest.readResultedIn(StateStore.Result.Failure,
                    StateRecordAdapter.unmarshallForId(item),
                    (State) new State.NullState()
            );
        }
    }
}
