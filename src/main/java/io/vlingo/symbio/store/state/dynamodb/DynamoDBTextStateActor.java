package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import io.vlingo.actors.Actor;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.TextStateStore;
import io.vlingo.symbio.store.state.dynamodb.adapters.TextStateRecordAdapter;
import io.vlingo.symbio.store.state.dynamodb.handlers.BatchWriteItemAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.ConfirmDispatchableAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.DispatchAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.GetEntityAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.interests.CreateTableInterest;

import java.util.*;

import static java.util.Collections.singletonList;

public class DynamoDBTextStateActor extends DynamoDBStateActor implements TextStateStore {
    private final CreateTableInterest createTableInterest;

    public DynamoDBTextStateActor(Dispatcher dispatcher, AmazonDynamoDBAsync dynamodb, CreateTableInterest createTableInterest) {
        super(dispatcher, dynamodb);
        this.createTableInterest = createTableInterest;

        this.createTableInterest.createDispatchableTable(dynamodb, DISPATCHABLE_TABLE_NAME);
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
                    State<String> savedState = TextStateRecordAdapter.unmarshallState(foundItem);
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
        Map<String, AttributeValue> stateItem = TextStateRecordAdapter.marshallForQuery(id);

        return new GetItemRequest(table, stateItem, true);
    }

    private Map<String, List<WriteRequest>> writeRequestFor(State<String> state, Dispatchable<String> dispatchable) {
        Map<String, List<WriteRequest>> requests = new HashMap<>(2);

        requests.put(tableFor(state.typed()),
                singletonList(new WriteRequest(new PutRequest(TextStateRecordAdapter.marshall(state)))));

        requests.put(DISPATCHABLE_TABLE_NAME,
                singletonList(new WriteRequest(new PutRequest(TextStateRecordAdapter.marshall(dispatchable)))));

        return requests;
    }

}
