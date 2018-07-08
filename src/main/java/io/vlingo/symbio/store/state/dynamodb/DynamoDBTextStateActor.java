package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBAsync;

public class DynamoDBTextStateActor {
    private final AmazonDynamoDBAsync dynamodb;
    private final CreateTableInterest interest;

    public DynamoDBTextStateActor(AmazonDynamoDBAsync dynamodb, CreateTableInterest interest) {
        this.dynamodb = dynamodb;
        this.interest = interest;

        interest.createTable(dynamodb);
    }
}
