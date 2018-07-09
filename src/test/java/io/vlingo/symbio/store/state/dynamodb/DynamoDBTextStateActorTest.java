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
import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.World;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.Entity1;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.TextStateStore;
import io.vlingo.symbio.store.state.dynamodb.interests.CreateTableInterest;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class DynamoDBTextStateActorTest {
    private static final String DYNAMODB_HOST = "http://localhost:8000";
    private static final String DYNAMODB_REGION = "eu-west-1";
    private static final AWSStaticCredentialsProvider DYNAMODB_CREDENTIALS = new AWSStaticCredentialsProvider(new BasicAWSCredentials("1", "2"));
    private static final AwsClientBuilder.EndpointConfiguration DYNAMODB_ENDPOINT_CONFIGURATION = new AwsClientBuilder.EndpointConfiguration(DYNAMODB_HOST, DYNAMODB_REGION);
    private static final String TABLE_NAME = "vlingo_io_vlingo_symbio_store_state_Entity1";
    private static final String DISPATCHABLE_TABLE_NAME = "vlingo_dispatchables";
    private static final int DEFAULT_TIMEOUT = 6000;
    private static DynamoDBProxyServer dynamodbServer;

    private World world;
    private AmazonDynamoDBAsync dynamodb;
    private CreateTableInterest createTableInterest;
    private TextStateStore stateStore;
    private StateStore.DispatcherControl dispatcherControl;
    private StateStore.WriteResultInterest<String> writeResultInterest;
    private StateStore.ReadResultInterest<String> readResultInterest;
    private StateStore.Dispatcher dispatcher;

    @BeforeClass
    public static void setUpDynamoDB() throws Exception {
        System.setProperty("sqlite4java.library.path", "native-libs");
        final String[] localArgs = { "-inMemory" };

        dynamodbServer = ServerRunner.createServerFromCommandLineArgs(localArgs);
        dynamodbServer.start();
    }

    @AfterClass
    public static void tearDownDynamoDb() throws Exception {
        dynamodbServer.stop();
    }

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
        dispatcher = mock(StateStore.Dispatcher.class);

        Protocols protocols = world.actorFor(
                Definition.has(DynamoDBTextStateActor.class, Definition.parameters(dispatcher, dynamodb, createTableInterest)),
                new Class[] { TextStateStore.class, StateStore.DispatcherControl.class}
        );

        stateStore = protocols.get(0);
        dispatcherControl = protocols.get(1);
    }

    @After
    public void tearDown() {
        dropTable(TABLE_NAME);
        dropTable(DISPATCHABLE_TABLE_NAME);
    }

    @Test
    public void testThatCreatingATextStateActorCreatesTheDispatchableTable() throws Exception {
        verify(createTableInterest).createDispatchableTable(dynamodb, DISPATCHABLE_TABLE_NAME);
    }

    @Test
    public void testThatWritingAndReadingTransactionReturnsCurrentState() throws Exception {
        State<String> currentState = randomState();
        stateStore.write(currentState, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.Success, currentState.id, currentState);

        stateStore.read(currentState.id, currentState.typed(), readResultInterest);
        verify(readResultInterest, timeout(DEFAULT_TIMEOUT)).readResultedIn(StateStore.Result.Success, currentState.id, currentState);
    }

    @Test
    public void testThatWritingToATableCallsCreateTableInterest() throws Exception {
        dropTable(TABLE_NAME);

        stateStore.write(randomState(), writeResultInterest);
        verify(createTableInterest, timeout(DEFAULT_TIMEOUT)).createEntityTable(dynamodb, TABLE_NAME);
    }

    @Test
    public void testThatWritingToATableThatDoesntExistFails() throws Exception {
        dropTable(TABLE_NAME);
        State<String> state = randomState();

        stateStore.write(state, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.NoTypeStore, state.id, null);
    }

    @Test
    public void testThatReadingAnUnknownStateFailsWithNotFound() throws Exception {
        State<String> state = randomState();

        stateStore.read(state.id, Entity1.class, readResultInterest);
        verify(readResultInterest, timeout(DEFAULT_TIMEOUT)).readResultedIn(StateStore.Result.NotFound, state.id, null);
    }

    @Test
    public void testThatReadingOnAnUnknownTableFails() throws Exception {
        dropTable(TABLE_NAME);
        State<String> state = randomState();

        stateStore.read(state.id, Entity1.class, readResultInterest);
        verify(readResultInterest, timeout(DEFAULT_TIMEOUT)).readResultedIn(StateStore.Result.NoTypeStore, state.id, null);
    }

    @Test
    public void testThatShouldNotAcceptWritingAnOldDataVersion() throws Exception {
        State<String> oldState = randomState();
        State<String> newState = newFor(oldState);

        stateStore.write(newState, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.Success, newState.id, newState);

        stateStore.write(oldState, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.ConcurrentyViolation, newState.id, newState);
    }

    @Test
    public void testThatDispatchesOnWrite() throws Exception {
        State<String> state = randomState();

        stateStore.write(state, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.Success, state.id, state);

        verify(dispatcher, timeout(DEFAULT_TIMEOUT)).dispatch(DISPATCHABLE_TABLE_NAME + ":" + state.id, state.asTextState());
    }

    @Test
    public void testThatWritingStoresTheDispatchableOnDynamoDB() throws Exception {
        State<String> state = randomState();

        stateStore.write(state, writeResultInterest);
        verify(writeResultInterest, timeout(DEFAULT_TIMEOUT)).writeResultedIn(StateStore.Result.Success, state.id, state);

        StateStore.Dispatchable<String> dispatchable = dispatchableByState(state);
        Assert.assertEquals(state, dispatchable.state);
    }

    private State<String> randomState() {
        return new State.TextState(
                UUID.randomUUID().toString(),
                Entity1.class,
                1,
                UUID.randomUUID().toString(),
                1,
                new Metadata(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        );
    }

    private State<String> newFor(State<String> oldState) {
        return new State.TextState(
                oldState.id,
                oldState.typed(),
                oldState.typeVersion,
                oldState.data,
                oldState.dataVersion + 1,
                oldState.metadata
        );
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

    private StateStore.Dispatchable<String> dispatchableByState(State<String> state) {
        String dispatchableId = DISPATCHABLE_TABLE_NAME + ":" + state.id;
        GetItemResult item = dynamoDBSyncClient().getItem(DISPATCHABLE_TABLE_NAME, StateRecordAdapter.marshallForQuery(dispatchableId));

        Map<String, AttributeValue> dispatchableSerializedItem = item.getItem();
        String stateAsJson = dispatchableSerializedItem.get("State").getS();
        return new StateStore.Dispatchable<>(dispatchableId, JsonSerialization.deserialized(stateAsJson, State.TextState.class));
    }

    private AmazonDynamoDB dynamoDBSyncClient() {
        return AmazonDynamoDBClientBuilder.standard()
                .withEndpointConfiguration(DYNAMODB_ENDPOINT_CONFIGURATION)
                .withCredentials(DYNAMODB_CREDENTIALS)
                .build();
    }
}
