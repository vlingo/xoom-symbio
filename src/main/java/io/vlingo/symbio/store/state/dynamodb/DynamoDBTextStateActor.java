package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.TextStateStore;
import io.vlingo.symbio.store.state.dynamodb.handlers.BatchWriteItemAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.GetItemAsyncHandler;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;

public class DynamoDBTextStateActor implements TextStateStore {
    private final AmazonDynamoDBAsync dynamodb;
    private final CreateTableInterest interest;

    public DynamoDBTextStateActor(AmazonDynamoDBAsync dynamodb, CreateTableInterest interest) {
        this.dynamodb = dynamodb;
        this.interest = interest;

        interest.createTable(dynamodb);
    }

    @Override
    public void read(String id, Class<?> type, ReadResultInterest<String> interest) {
        dynamodb.getItemAsync(readRequestFor(id, type), new GetItemAsyncHandler(interest));
    }

    @Override
    public void write(State<String> state, WriteResultInterest<String> interest) {
        WriteRequest stateForActor = writeRequestFor(state);
        String tableName = tableFor(state.typed());

        BatchWriteItemRequest request = new BatchWriteItemRequest(singletonMap(tableName, singletonList(stateForActor)));
        try {
            dynamodb.batchWriteItemAsync(request, new BatchWriteItemAsyncHandler(state, interest)).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
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
