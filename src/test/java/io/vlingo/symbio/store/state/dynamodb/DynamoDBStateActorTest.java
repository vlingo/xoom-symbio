package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsyncClient;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import com.amazonaws.services.dynamodbv2.model.*;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.World;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.StateTypeStateStoreMap;
import io.vlingo.symbio.store.state.dynamodb.adapters.RecordAdapter;
import io.vlingo.symbio.store.state.dynamodb.interests.CreateTableInterest;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public abstract class DynamoDBStateActorTest<T extends StateStore, K> {
    protected static final int DEFAULT_TIMEOUT = 6000;
    private static final String DYNAMODB_HOST = "http://localhost:8000";
    private static final String DYNAMODB_REGION = "eu-west-1";
    private static final AWSStaticCredentialsProvider DYNAMODB_CREDENTIALS = new AWSStaticCredentialsProvider(new BasicAWSCredentials("1", "2"));
    private static final AwsClientBuilder.EndpointConfiguration DYNAMODB_ENDPOINT_CONFIGURATION = new AwsClientBuilder.EndpointConfiguration(DYNAMODB_HOST, DYNAMODB_REGION);
    private static final String TABLE_NAME = "vlingo_io_vlingo_symbio_store_state_Entity1";
    private static final String DISPATCHABLE_TABLE_NAME = "vlingo_dispatchables";
    private static DynamoDBProxyServer dynamodbServer;

    private World world;
    private AmazonDynamoDBAsync dynamodb;
    private CreateTableInterest createTableInterest;
    private T stateStore;
    private StateStore.DispatcherControl dispatcherControl;
    private StateStore.WriteResultInterest<K> writeResultInterest;
    private StateStore.ReadResultInterest<K> readResultInterest;
    private StateStore.Dispatcher dispatcher;
    private StateStore.ConfirmDispatchedResultInterest confirmDispatchedResultInterest;

    @BeforeClass
    public static void setUpDynamoDB() throws Exception {
        System.setProperty("sqlite4java.library.path", "native-libs");
        final String[] localArgs = {"-inMemory"};

        dynamodbServer = ServerRunner.createServerFromCommandLineArgs(localArgs);
        dynamodbServer.start();
    }

    @AfterClass
    public static void tearDownDynamoDb() throws Exception {
        dynamodbServer.stop();
        StateTypeStateStoreMap.reset();
    }

    protected abstract Protocols stateStoreProtocols(World world, StateStore.Dispatcher dispatcher, AmazonDynamoDBAsync dynamodb, CreateTableInterest interest);

    protected abstract void doWrite(T actor, State<K> state, StateStore.WriteResultInterest<K> interest);

    protected abstract void doRead(T actor, String id, Class<?> type, StateStore.ReadResultInterest<K> interest);

    protected abstract State<K> nullState();

    protected abstract State<K> randomState();

    protected abstract State<K> newFor(State<K> oldState);

    protected abstract void verifyDispatched(StateStore.Dispatcher dispatcher, String id, StateStore.Dispatchable<K> dispatchable);

    protected abstract void verifyDispatched(StateStore.Dispatcher dispatcher, String id, State<K> state);

    protected abstract RecordAdapter<K> recordAdapter();

    @Before
    public void setUp() {
        createTable(TABLE_NAME);
        createTable(DISPATCHABLE_TABLE_NAME);

        world = World.start(UUID.randomUUID().toString(), true);

        dynamodb = AmazonDynamoDBAsyncClient.asyncBuilder()
                .withCredentials(DYNAMODB_CREDENTIALS)
                .withEndpointConfiguration(DYNAMODB_ENDPOINT_CONFIGURATION)
                .build();

        createTableInterest = mock(CreateTableInterest.class);
        writeResultInterest = mock(StateStore.WriteResultInterest.class);
        readResultInterest = mock(StateStore.ReadResultInterest.class);
        confirmDispatchedResultInterest = mock(StateStore.ConfirmDispatchedResultInterest.class);

        dispatcher = mock(StateStore.Dispatcher.class);

        Protocols protocols = stateStoreProtocols(world, dispatcher, dynamodb, createTableInterest);

        stateStore = protocols.get(0);
        dispatcherControl = protocols.get(1);
    }

    @After
    public void tearDown() {
        dropTable(TABLE_NAME);
        dropTable(DISPATCHABLE_TABLE_NAME);
    }

    @Test
    public void testThatCreatingATextStateActorCreatesTheDispatchableTable() {
        verify(createTableInterest).createDispatchableTable(dynamodb, DISPATCHABLE_TABLE_NAME);
    }

    @Test
    public void testThatWritingAndReadingTransactionReturnsCurrentState() {
        State<K> currentState = randomState();
        doWrite(stateStore, currentState, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.Success, currentState.id, currentState);

        doRead(stateStore, currentState.id, currentState.typed(), readResultInterest);
        verify(readResultInterest, timeout(DEFAULT_TIMEOUT)).readResultedIn(StateStore.Result.Success, currentState.id, currentState);
    }

    @Test
    public void testThatWritingToATableCallsCreateTableInterest() {
        dropTable(TABLE_NAME);

        doWrite(stateStore, randomState(), writeResultInterest);
        verify(createTableInterest, timeout(DEFAULT_TIMEOUT)).createEntityTable(dynamodb, TABLE_NAME);
    }

    @Test
    public void testThatWritingToATableThatDoesntExistFails() {
        dropTable(TABLE_NAME);
        State<K> state = randomState();

        doWrite(stateStore, state, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(
                eq(StateStore.Result.NoTypeStore),
                any(IllegalStateException.class),
                eq(state.id),
                eq(nullState())
        );
    }

    @Test
    public void testThatReadingAnUnknownStateFailsWithNotFound() {
        State<K> state = randomState();

        doRead(stateStore, state.id, Entity1.class, readResultInterest);
        verify(readResultInterest, timeout(DEFAULT_TIMEOUT)).readResultedIn(
                StateStore.Result.NotFound,
                state.id,
                nullState()
        );
    }

    @Test
    public void testThatReadingOnAnUnknownTableFails() {
        dropTable(TABLE_NAME);
        State<K> state = randomState();

        doRead(stateStore, state.id, Entity1.class, readResultInterest);
        verify(readResultInterest, timeout(DEFAULT_TIMEOUT)).readResultedIn(
                eq(StateStore.Result.NoTypeStore),
                any(IllegalStateException.class),
                eq(state.id),
                eq(nullState())
        );
    }

    @Test
    public void testThatShouldNotAcceptWritingAnOldDataVersion() {
        State<K> oldState = randomState();
        State<K> newState = newFor(oldState);

        doWrite(stateStore, newState, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.Success, newState.id, newState);

        doWrite(stateStore, oldState, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.ConcurrentyViolation, newState.id, newState);
    }

    @Test
    public void testThatDispatchesOnWrite() {
        State<K> state = randomState();

        doWrite(stateStore, state, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.Success, state.id, state);

        verifyDispatched(dispatcher, state.type + ":" + state.id, state);
//        verify(dispatcher, timeout(DEFAULT_TIMEOUT)).dispatch(state.type + ":" + state.id, state.asTextState());
    }

    @Test
    public void testThatWritingStoresTheDispatchableOnDynamoDB() {
        State<K> state = randomState();

        doWrite(stateStore, state, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.Success, state.id, state);

        StateStore.Dispatchable<K> dispatchable = dispatchableByState(state);
        Assert.assertEquals(state, dispatchable.state);
    }

    @Test
    public void testThatDispatchUnconfirmedShouldDispatchAllOnDynamoDB() {
        State<K> state = randomState();

        doWrite(stateStore, state, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.Success, state.id, state);

        StateStore.Dispatchable<K> dispatchable = dispatchableByState(state);

        dispatcherControl.dispatchUnconfirmed();
        verifyDispatched(dispatcher, dispatchable.id, dispatchable);
//        verify(dispatcher).dispatch(dispatchable.id, stateFromDispatchable(dispatchable));
    }

    @Test
    public void testThatConfirmDispatchRemovesRecordFromDynamoDB() {
        State<K> state = randomState();

        doWrite(stateStore, state, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.Success, state.id, state);

        StateStore.Dispatchable<K> dispatchable = dispatchableByState(state);
        dispatcherControl.confirmDispatched(dispatchable.id, confirmDispatchedResultInterest);

        verify(confirmDispatchedResultInterest, timeout(DEFAULT_TIMEOUT))
                .confirmDispatchedResultedIn(StateStore.Result.Success, dispatchable.id);

        assertNull(dispatchableByState(state));
    }

    @Test
    public void testThatConfirmDispatchFailsWithFailureIfTableDoesNotExist() {
        dropTable(DISPATCHABLE_TABLE_NAME);

        String dispatchableId = UUID.randomUUID().toString();
        dispatcherControl.confirmDispatched(dispatchableId, confirmDispatchedResultInterest);

        verify(confirmDispatchedResultInterest, timeout(DEFAULT_TIMEOUT))
                .confirmDispatchedResultedIn(StateStore.Result.Failure, dispatchableId);
    }


    private void createTable(String tableName) {
        AmazonDynamoDB syncDynamoDb = dynamoDBSyncClient();

        List<AttributeDefinition> attributeDefinitions = new ArrayList<>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("Id").withAttributeType("S"));

        List<KeySchemaElement> keySchema = new ArrayList<>();
        keySchema.add(new KeySchemaElement().withAttributeName("Id").withKeyType(KeyType.HASH));

        CreateTableRequest request = new CreateTableRequest()
                .withTableName(tableName)
                .withKeySchema(keySchema)
                .withAttributeDefinitions(attributeDefinitions)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        syncDynamoDb.createTable(request);
    }

    private void dropTable(String tableName) {
        AmazonDynamoDB syncDynamoDb = dynamoDBSyncClient();

        try {
            syncDynamoDb.deleteTable(tableName);
        } catch (Exception ex) {

        }
    }

    private StateStore.Dispatchable<K> dispatchableByState(State<K> state) {
        String dispatchableId = state.type + ":" + state.id;
        GetItemResult item = dynamoDBSyncClient().getItem(DISPATCHABLE_TABLE_NAME, recordAdapter().marshallForQuery(dispatchableId));

        Map<String, AttributeValue> dispatchableSerializedItem = item.getItem();
        if (dispatchableSerializedItem == null) {
            return null;
        }

        return recordAdapter().unmarshallDispatchable(dispatchableSerializedItem);
    }

    private AmazonDynamoDB dynamoDBSyncClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(DYNAMODB_ENDPOINT_CONFIGURATION)
                .withCredentials(DYNAMODB_CREDENTIALS)
                .build();
    }
}
