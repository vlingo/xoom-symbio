package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.verify;

public class DynamoDBTextStateActorTest {
    @Test
    public void testThatInterestIsCalledForCreatingATable() {
        AmazonDynamoDBAsync dynamodb = Mockito.mock(AmazonDynamoDBAsync.class);
        CreateTableInterest interest = Mockito.mock(CreateTableInterest.class);

        DynamoDBTextStateActor actor = new DynamoDBTextStateActor(dynamodb, interest);

        verify(interest).createTable(dynamodb);
    }
}
