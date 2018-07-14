package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.DeleteItemRequest;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import io.vlingo.actors.Actor;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.dynamodb.adapters.TextStateRecordAdapter;
import io.vlingo.symbio.store.state.dynamodb.handlers.ConfirmDispatchableAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.DispatchAsyncHandler;

public class DynamoDBStateActor extends Actor implements StateStore.DispatcherControl {
    public static final String DISPATCHABLE_TABLE_NAME = "vlingo_dispatchables";
    protected final StateStore.Dispatcher dispatcher;
    protected final AmazonDynamoDBAsync dynamodb;

    public DynamoDBStateActor(StateStore.Dispatcher dispatcher, AmazonDynamoDBAsync dynamodb) {
        this.dispatcher = dispatcher;
        this.dynamodb = dynamodb;
    }

    @Override
    public void confirmDispatched(String dispatchId, StateStore.ConfirmDispatchedResultInterest interest) {
        dynamodb.deleteItemAsync(
                new DeleteItemRequest(
                        DISPATCHABLE_TABLE_NAME,
                        TextStateRecordAdapter.marshallForQuery(dispatchId)),
                new ConfirmDispatchableAsyncHandler(dispatchId, interest)
        );
    }

    @Override
    public void dispatchUnconfirmed() {
        dynamodb.scanAsync(new ScanRequest(DISPATCHABLE_TABLE_NAME).withLimit(100), new DispatchAsyncHandler(dispatcher));
    }

    protected String tableFor(Class<?> type) {
        String tableName = "vlingo_" + type.getCanonicalName().replace(".", "_");
        StateTypeStateStoreMap.stateTypeToStoreName(type, tableName);
        return tableName;
    }
}
