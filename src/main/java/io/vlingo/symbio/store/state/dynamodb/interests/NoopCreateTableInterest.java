package io.vlingo.symbio.store.state.dynamodb.interests;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;

public class NoopCreateTableInterest implements CreateTableInterest {
    @Override
    public void createDispatchableTable(AmazonDynamoDBAsync dynamoDBAsync, String tableName) {

    }

    @Override
    public void createEntityTable(AmazonDynamoDBAsync dynamoDBAsync, String tableName) {

    }
}
