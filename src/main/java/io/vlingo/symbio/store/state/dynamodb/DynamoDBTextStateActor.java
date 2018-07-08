package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.TextStateStore;
import io.vlingo.symbio.store.state.dynamodb.handlers.BatchWriteItemAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.GetItemAsyncHandler;

import java.util.*;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

public class DynamoDBTextStateActor implements TextStateStore {
    private final AmazonDynamoDBAsync dynamodb;
    private final CreateTableInterest createTableInterest;

    public DynamoDBTextStateActor(AmazonDynamoDBAsync dynamodb, CreateTableInterest createTableInterest) {
        this.dynamodb = dynamodb;
        this.createTableInterest = createTableInterest;
    }

    @Override
    public void read(String id, Class<?> type, ReadResultInterest<String> interest) {
        dynamodb.getItemAsync(readRequestFor(id, type), new GetItemAsyncHandler(id, interest));
    }

    @Override
    public void write(State<String> state, WriteResultInterest<String> interest) {
        WriteRequest stateForActor = writeRequestFor(state);
        String tableName = tableFor(state.typed());

        createTableInterest.createTable(dynamodb, tableName);

        try {
            Map<String, AttributeValue> foundItem = dynamodb.getItem(readRequestFor(state.id, state.typed())).getItem();
            if (foundItem != null) {
                try {
                    State<String> savedState = StateRecordAdapter.unmarshall(foundItem);
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

        BatchWriteItemRequest request = new BatchWriteItemRequest(singletonMap(tableName, singletonList(stateForActor)));
        dynamodb.batchWriteItemAsync(request, new BatchWriteItemAsyncHandler(state, interest));
    }

    private GetItemRequest readRequestFor(String id, Class<?> type) {
        String table = tableFor(type);
        Map<String, AttributeValue> stateItem = StateRecordAdapter.marshallForQuery(id);

        return new GetItemRequest(table, stateItem, true);
    }

    private WriteRequest writeRequestFor(State<String> state) {
        Map<String, AttributeValue> stateItem = StateRecordAdapter.marshall(state);
        return new WriteRequest(new PutRequest(stateItem));
    }

    private String tableFor(Class<?> type) {
        return "vlingo_" + type.getCanonicalName().replace(".", "_");
    }
}
