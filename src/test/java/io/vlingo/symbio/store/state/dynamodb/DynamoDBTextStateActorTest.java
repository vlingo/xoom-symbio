package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.World;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.TextStateStore;
import io.vlingo.symbio.store.state.dynamodb.interests.CreateTableInterest;

import java.util.UUID;

public class DynamoDBTextStateActorTest extends DynamoDBStateActorTest<TextStateStore, String> {
    @Override
    protected Protocols stateStoreProtocols(World world, StateStore.Dispatcher dispatcher, AmazonDynamoDBAsync dynamodb, CreateTableInterest interest) {
        return world.actorFor(
                Definition.has(DynamoDBTextStateActor.class, Definition.parameters(dispatcher, dynamodb, interest)),
                new Class[]{TextStateStore.class, StateStore.DispatcherControl.class}
        );
    }

    @Override
    protected void doWrite(TextStateStore actor, State<String> state, StateStore.WriteResultInterest<String> interest) {
        actor.write(state, interest);
    }

    @Override
    protected void doRead(TextStateStore actor, String id, Class<?> type, StateStore.ReadResultInterest<String> interest) {
        actor.read(id, type, interest);
    }

    @Override
    protected State<String> nullState() {
        return State.NullState.Text;
    }

    @Override
    protected State<String> randomState() {
        return new State.TextState(
                UUID.randomUUID().toString(),
                Entity1.class,
                1,
                UUID.randomUUID().toString(),
                1,
                new Metadata(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        );
    }

    @Override
    protected State<String> newFor(State<String> oldState) {
        return new State.TextState(
                oldState.id,
                oldState.typed(),
                oldState.typeVersion,
                oldState.data,
                oldState.dataVersion + 1,
                oldState.metadata
        );
    }
}