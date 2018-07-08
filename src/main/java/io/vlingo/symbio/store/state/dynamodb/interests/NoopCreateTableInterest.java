package io.vlingo.symbio.store.state.dynamodb.interests;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;
import io.vlingo.symbio.store.state.dynamodb.CreateTableInterest;

public class NoopCreateTableInterest implements CreateTableInterest {
    @Override
    public void createTable(AmazonDynamoDBAsync dynamoDBAsync, String tableName) {

    }
}
