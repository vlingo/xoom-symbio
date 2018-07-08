package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;

import java.util.HashMap;
import java.util.Map;

public final class StateRecordAdapter {
    public static Map<String, AttributeValue> marshall(State<String> state) {
        String metadataAsJson = JsonSerialization.serialized(state.metadata);

        Map<String, AttributeValue> stateItem = new HashMap<>();
        stateItem.put("Id", new AttributeValue().withS(state.id));
        stateItem.put("Data", new AttributeValue().withS(state.data));
        stateItem.put("Type", new AttributeValue().withS(state.type));
        stateItem.put("Metadata", new AttributeValue().withS(metadataAsJson));
        stateItem.put("TypeVersion", new AttributeValue().withN(String.valueOf(state.typeVersion)));
        stateItem.put("DataVersion", new AttributeValue().withN(String.valueOf(state.dataVersion)));

        return stateItem;
    }

    public static Map<String, AttributeValue> marshallForQuery(String id) {
        Map<String, AttributeValue> stateItem = new HashMap<>();
        stateItem.put("Id", new AttributeValue().withS(id));

        return stateItem;
    }

    public static String unmarshallForId(Map<String, AttributeValue> state) {
        return state.get("Id").getS();
    }

    public static State<String> unmarshall(Map<String, AttributeValue> record) throws ClassNotFoundException {
        return new State.TextState(
                record.get("Id").getS(),
                Class.forName(record.get("Type").getS()),
                Integer.valueOf(record.get("TypeVersion").getN()),
                record.get("Data").getS(),
                Integer.valueOf(record.get("DataVersion").getN()),
                JsonSerialization.deserialized(record.get("Metadata").getS(), Metadata.class)
        );
    }
}
