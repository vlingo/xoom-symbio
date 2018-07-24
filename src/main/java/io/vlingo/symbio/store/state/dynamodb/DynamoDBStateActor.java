package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.model.*;
import io.vlingo.actors.Actor;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.dynamodb.adapters.RecordAdapter;
import io.vlingo.symbio.store.state.dynamodb.handlers.BatchWriteItemAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.ConfirmDispatchableAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.DispatchAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.handlers.GetEntityAsyncHandler;
import io.vlingo.symbio.store.state.dynamodb.interests.CreateTableInterest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;

public abstract class DynamoDBStateActor<T> extends Actor implements StateStore.DispatcherControl {
    public static final String DISPATCHABLE_TABLE_NAME = "vlingo_dispatchables";
    protected final StateStore.Dispatcher dispatcher;
    protected final AmazonDynamoDBAsync dynamodb;
    protected final CreateTableInterest createTableInterest;
    protected final RecordAdapter<T> recordAdapter;
    protected final State<T> nullState;

    public DynamoDBStateActor(
            StateStore.Dispatcher dispatcher,
            AmazonDynamoDBAsync dynamodb,
            CreateTableInterest createTableInterest,
            RecordAdapter<T> recordAdapter,
            State<T> nullState
    ) {
        this.dispatcher = dispatcher;
        this.dynamodb = dynamodb;
        this.createTableInterest = createTableInterest;
        this.recordAdapter = recordAdapter;
        this.nullState = nullState;
    }

    @Override
    public void confirmDispatched(String dispatchId, StateStore.ConfirmDispatchedResultInterest interest) {
        dynamodb.deleteItemAsync(
                new DeleteItemRequest(
                        DISPATCHABLE_TABLE_NAME,
                        recordAdapter.marshallForQuery(dispatchId)),
                new ConfirmDispatchableAsyncHandler(dispatchId, interest)
        );
    }

    @Override
    public void dispatchUnconfirmed() {
        dynamodb.scanAsync(new ScanRequest(DISPATCHABLE_TABLE_NAME).withLimit(100), new DispatchAsyncHandler<>(recordAdapter::unmarshallDispatchable, this::doDispatch));
    }

    protected abstract Void doDispatch(StateStore.Dispatchable<T> dispatchable);

    protected final String tableFor(Class<?> type) {
        String tableName = "vlingo_" + type.getCanonicalName().replace(".", "_");
        StateTypeStateStoreMap.stateTypeToStoreName(type, tableName);
        return tableName;
    }

    protected final void doGenericRead(String id, Class<?> type, StateStore.ReadResultInterest<T> interest) {
        dynamodb.getItemAsync(readRequestFor(id, type), new GetEntityAsyncHandler<T>(id, interest, nullState, recordAdapter::unmarshallState));
    }

    protected final void doGenericWrite(State<T> state, StateStore.WriteResultInterest<T> interest) {
        String tableName = tableFor(state.typed());
        createTableInterest.createEntityTable(dynamodb, tableName);

        try {
            Map<String, AttributeValue> foundItem = dynamodb.getItem(readRequestFor(state.id, state.typed())).getItem();
            if (foundItem != null) {
                try {
                    State<T> savedState = recordAdapter.unmarshallState(foundItem);
                    if (savedState.dataVersion > state.dataVersion) {
                        interest.writeResultedIn(StateStore.Result.ConcurrentyViolation, state.id, savedState);
                        return;
                    }
                } catch (Exception e) {
                    interest.writeResultedIn(StateStore.Result.Failure, state.id, state);
                    return;
                }
            }
        } catch (Exception e) {
            // in case of error (for now) just try to write the record
        }

        StateStore.Dispatchable<T> dispatchable = new StateStore.Dispatchable<>(state.type + ":" + state.id, state);

        Map<String, List<WriteRequest>> transaction = writeRequestFor(state, dispatchable);
        BatchWriteItemRequest request = new BatchWriteItemRequest(transaction);
        dynamodb.batchWriteItemAsync(request, new BatchWriteItemAsyncHandler<>(state, interest, dispatchable, dispatcher, nullState, this::doDispatch));
    }

    protected GetItemRequest readRequestFor(String id, Class<?> type) {
        String table = tableFor(type);
        Map<String, AttributeValue> stateItem = recordAdapter.marshallForQuery(id);

        return new GetItemRequest(table, stateItem, true);
    }

    protected Map<String, List<WriteRequest>> writeRequestFor(State<T> state, StateStore.Dispatchable<T> dispatchable) {
        Map<String, List<WriteRequest>> requests = new HashMap<>(2);

        requests.put(tableFor(state.typed()),
                singletonList(new WriteRequest(new PutRequest(recordAdapter.marshallState(state)))));

        requests.put(DISPATCHABLE_TABLE_NAME,
                singletonList(new WriteRequest(new PutRequest(recordAdapter.marshallDispatchable(dispatchable)))));

        return requests;
    }

}
