package io.vlingo.symbio.store.state.dynamodb.adapters;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;

import java.util.Map;

public interface RecordAdapter<T> {
    Map<String, AttributeValue> marshallState(State<T> state);

    Map<String, AttributeValue> marshallDispatchable(StateStore.Dispatchable<T> dispatchable);

    Map<String, AttributeValue> marshallForQuery(String id);

    State<T> unmarshallState(Map<String, AttributeValue> record);

    StateStore.Dispatchable<T> unmarshallDispatchable(Map<String, AttributeValue> item);
}
