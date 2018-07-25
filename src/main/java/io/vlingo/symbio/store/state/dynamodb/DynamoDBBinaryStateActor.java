package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.BinaryStateStore;
import io.vlingo.symbio.store.state.dynamodb.adapters.BinaryStateRecordAdapter;
import io.vlingo.symbio.store.state.dynamodb.interests.CreateTableInterest;

public class DynamoDBBinaryStateActor extends DynamoDBStateActor<byte[]> implements BinaryStateStore {
    public DynamoDBBinaryStateActor(Dispatcher dispatcher, AmazonDynamoDBAsync dynamodb, CreateTableInterest createTableInterest) {
        super(dispatcher, dynamodb, createTableInterest, new BinaryStateRecordAdapter(), State.NullState.Binary);

        createTableInterest.createDispatchableTable(dynamodb, DISPATCHABLE_TABLE_NAME);
    }

    @Override
    public void read(String id, Class<?> type, ReadResultInterest<byte[]> interest) {
        doGenericRead(id, type, interest);
    }

    @Override
    public void write(State<byte[]> state, WriteResultInterest<byte[]> interest) {
        doGenericWrite(state, interest);
    }

    @Override
    protected Void doDispatch(Dispatchable<byte[]> dispatchable) {
        dispatcher.dispatch(dispatchable.id, dispatchable.state.asBinaryState());
        return null;
    }
}
