package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.World;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.BinaryStateStore;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.dynamodb.adapters.BinaryStateRecordAdapter;
import io.vlingo.symbio.store.state.dynamodb.adapters.RecordAdapter;
import io.vlingo.symbio.store.state.dynamodb.interests.CreateTableInterest;

import java.util.UUID;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class DynamoDBBinaryStateActorTest extends DynamoDBStateActorTest<BinaryStateStore, byte[]> {
    @Override
    protected Protocols stateStoreProtocols(World world, StateStore.Dispatcher dispatcher, AmazonDynamoDBAsync dynamodb, CreateTableInterest interest) {
        return world.actorFor(
                Definition.has(DynamoDBBinaryStateActor.class, Definition.parameters(dispatcher, dynamodb, interest)),
                new Class[]{BinaryStateStore.class, StateStore.DispatcherControl.class}
        );
    }

    @Override
    protected void doWrite(BinaryStateStore actor, State<byte[]> state, StateStore.WriteResultInterest<byte[]> interest) {
        actor.write(state, interest);
    }

    @Override
    protected void doRead(BinaryStateStore actor, String id, Class<?> type, StateStore.ReadResultInterest<byte[]> interest) {
        actor.read(id, type, interest);
    }

    @Override
    protected State<byte[]> nullState() {
        return State.NullState.Binary;
    }

    @Override
    protected State<byte[]> randomState() {
        return new State.BinaryState(
                UUID.randomUUID().toString(),
                Entity1.class,
                1,
                UUID.randomUUID().toString().getBytes(),
                1,
                new Metadata(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        );
    }

    @Override
    protected State<byte[]> newFor(State<byte[]> oldState) {
        return new State.BinaryState(
                oldState.id,
                oldState.typed(),
                oldState.typeVersion,
                oldState.data,
                oldState.dataVersion + 1,
                oldState.metadata
        );
    }

    @Override
    protected void verifyDispatched(StateStore.Dispatcher dispatcher, String id, StateStore.Dispatchable<byte[]> dispatchable) {
        verify(dispatcher).dispatch(dispatchable.id, dispatchable.state.asBinaryState());
    }

    @Override
    protected void verifyDispatched(StateStore.Dispatcher dispatcher, String id, State<byte[]> state) {
        verify(dispatcher, timeout(DEFAULT_TIMEOUT)).dispatch(id, state.asBinaryState());
    }

    @Override
    protected RecordAdapter<byte[]> recordAdapter() {
        return new BinaryStateRecordAdapter();
    }
}