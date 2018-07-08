package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;

public interface CreateTableInterest {
    void createTable(AmazonDynamoDBAsync dynamoDBAsync, String tableName);
}
