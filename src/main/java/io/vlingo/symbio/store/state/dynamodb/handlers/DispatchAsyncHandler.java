package io.vlingo.symbio.store.state.dynamodb.handlers;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.dynamodb.StateRecordAdapter;

import java.util.List;
import java.util.Map;

public class DispatchAsyncHandler implements AsyncHandler<ScanRequest, ScanResult> {
    private final StateStore.Dispatcher dispatcher;

    public DispatchAsyncHandler(StateStore.Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void onError(Exception e) {

    }

    @Override
    public void onSuccess(ScanRequest request, ScanResult scanResult) {
        List<Map<String, AttributeValue>> items = scanResult.getItems();
        for (Map<String, AttributeValue> item : items) {
            StateStore.Dispatchable<String> dispatchable = StateRecordAdapter.unmarshallDispatchable(item);
            dispatcher.dispatch(dispatchable.id, dispatchable.state.asTextState());
        }
    }
}
