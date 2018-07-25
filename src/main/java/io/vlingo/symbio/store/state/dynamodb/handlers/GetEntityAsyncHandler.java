package io.vlingo.symbio.store.state.dynamodb.handlers;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.GetItemRequest;
import com.amazonaws.services.dynamodbv2.model.GetItemResult;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;

import java.util.Map;
import java.util.function.Function;

public class GetEntityAsyncHandler<T> implements AsyncHandler<GetItemRequest, GetItemResult> {
    private final String id;
    private final StateStore.ReadResultInterest<T> interest;
    private final State<T> nullState;
    private final Function<Map<String, AttributeValue>, State<T>> unmarshaller;

    public GetEntityAsyncHandler(String id, StateStore.ReadResultInterest<T> interest, State<T> nullState, Function<Map<String, AttributeValue>, State<T>> unmarshaller) {
        this.id = id;
        this.interest = interest;
        this.nullState = nullState;
        this.unmarshaller = unmarshaller;
    }

    @Override
    public void onError(Exception e) {
        interest.readResultedIn(StateStore.Result.NoTypeStore, new IllegalStateException(e), id, nullState);
    }

    @Override
    public void onSuccess(GetItemRequest request, GetItemResult getItemResult) {
        Map<String, AttributeValue> item = getItemResult.getItem();
        if (item == null) {
            interest.readResultedIn(StateStore.Result.NotFound, id, nullState);
            return;
        }

        try {
            State<T> state = unmarshaller.apply(item);
            interest.readResultedIn(StateStore.Result.Success, id, state);
        } catch (Exception e) {
            interest.readResultedIn(StateStore.Result.Failure, e, id, nullState);
        }
    }
}
