package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.TextStateStore;
import io.vlingo.symbio.store.state.dynamodb.adapters.TextStateRecordAdapter;
import io.vlingo.symbio.store.state.dynamodb.interests.CreateTableInterest;

public class DynamoDBTextStateActor extends DynamoDBStateActor<String> implements TextStateStore {
    public DynamoDBTextStateActor(Dispatcher dispatcher, AmazonDynamoDBAsync dynamodb, CreateTableInterest createTableInterest) {
        super(dispatcher, dynamodb, createTableInterest, new TextStateRecordAdapter(), State.NullState.Text);

        createTableInterest.createDispatchableTable(dynamodb, DISPATCHABLE_TABLE_NAME);
    }

    @Override
    public void read(String id, Class<?> type, ReadResultInterest<String> interest) {
        doGenericRead(id, type, interest);
    }

    @Override
    public void write(State<String> state, WriteResultInterest<String> interest) {
        doGenericWrite(state, interest);
    }

    @Override
    protected Void doDispatch(Dispatchable<String> dispatchable) {
        dispatcher.dispatch(dispatchable.id, dispatchable.state.asTextState());
        return null;
    }
}
