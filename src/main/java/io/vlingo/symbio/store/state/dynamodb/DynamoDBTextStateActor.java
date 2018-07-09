package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import io.vlingo.actors.Actor;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.TextStateStore;
import io.vlingo.symbio.store.state.dynamodb.handlers.BatchWriteItemAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.ConfirmDispatchableAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.DispatchAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.GetEntityAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.interests.CreateTableInterest;

import java.util.*;

import static java.util.Collections.singletonList;

public class DynamoDBTextStateActor extends Actor implements TextStateStore, StateStore.DispatcherControl  {
    public static final String DISPATCHABLE_TABLE_NAME = "vlingo_dispatchables";

    private final Dispatcher dispatcher;
    private final AmazonDynamoDBAsync dynamodb;
    private final CreateTableInterest createTableInterest;

    public DynamoDBTextStateActor(Dispatcher dispatcher, AmazonDynamoDBAsync dynamodb, CreateTableInterest createTableInterest) {
        this.dispatcher = dispatcher;
        this.dynamodb = dynamodb;
        this.createTableInterest = createTableInterest;

        this.createTableInterest.createDispatchableTable(dynamodb, DISPATCHABLE_TABLE_NAME);
    }

    @Override
    public void confirmDispatched(String dispatchId, ConfirmDispatchedResultInterest interest) {
        dynamodb.deleteItemAsync(
                new DeleteItemRequest(
                        DISPATCHABLE_TABLE_NAME,
                        StateRecordAdapter.marshallForQuery(dispatchId)),
                new ConfirmDispatchableAsyncHandler(dispatchId, interest)
        );
    }

    @Override
    public void dispatchUnconfirmed() {
        dynamodb.scanAsync(new ScanRequest(DISPATCHABLE_TABLE_NAME).withLimit(100), new DispatchAsyncHandler(dispatcher));
    }

    @Override
    public void read(String id, Class<?> type, ReadResultInterest<String> interest) {
        dynamodb.getItemAsync(readRequestFor(id, type), new GetEntityAsyncHandler(id, interest));
    }

    @Override
    public void write(State<String> state, WriteResultInterest<String> interest) {
        String tableName = tableFor(state.typed());
        createTableInterest.createEntityTable(dynamodb, tableName);

        try {
            Map<String, AttributeValue> foundItem = dynamodb.getItem(readRequestFor(state.id, state.typed())).getItem();
            if (foundItem != null) {
                try {
                    State<String> savedState = StateRecordAdapter.unmarshallState(foundItem);
                    if (savedState.dataVersion > state.dataVersion) {
                        interest.writeResultedIn(Result.ConcurrentyViolation, state.id, savedState);
                        return;
                    }
                } catch (ClassNotFoundException e) {
                    interest.writeResultedIn(Result.Failure, state.id, state);
                    return;
                }
            }
        } catch (Exception e) {
            // in case of error (for now) just try to write the record
        }

        Dispatchable<String> dispatchable = new Dispatchable<>(state.type + ":" + state.id, state);

        Map<String, List<WriteRequest>> transaction = writeRequestFor(state, dispatchable);
        BatchWriteItemRequest request = new BatchWriteItemRequest(transaction);
        dynamodb.batchWriteItemAsync(request, new BatchWriteItemAsyncHandler(state, interest, dispatchable, dispatcher));
    }

    private GetItemRequest readRequestFor(String id, Class<?> type) {
        String table = tableFor(type);
        Map<String, AttributeValue> stateItem = StateRecordAdapter.marshallForQuery(id);

        return new GetItemRequest(table, stateItem, true);
    }

    private Map<String, List<WriteRequest>> writeRequestFor(State<String> state, Dispatchable<String> dispatchable) {
        Map<String, List<WriteRequest>> requests = new HashMap<>(2);

        requests.put(tableFor(state.typed()),
                singletonList(new WriteRequest(new PutRequest(StateRecordAdapter.marshall(state)))));

        requests.put(DISPATCHABLE_TABLE_NAME,
                singletonList(new WriteRequest(new PutRequest(StateRecordAdapter.marshall(dispatchable)))));

        return requests;
    }

    private String tableFor(Class<?> type) {
        String tableName = "vlingo_" + type.getCanonicalName().replace(".", "_");
        StateTypeStateStoreMap.stateTypeToStoreName(type, tableName);
        return tableName;
    }
}
