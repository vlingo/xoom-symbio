package io.vlingo.symbio.store.state.dynamodb.interests;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;

public interface CreateTableInterest {
    void createDispatchableTable(AmazonDynamoDBAsync dynamoDBAsync, String tableName);
    void createEntityTable(AmazonDynamoDBAsync dynamoDBAsync, String tableName);
}
