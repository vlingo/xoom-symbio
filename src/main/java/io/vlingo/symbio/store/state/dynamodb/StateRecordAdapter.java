package io.vlingo.symbio.store.state.dynamodb;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import io.vlingo.common.serialization.JsonSerialization;
import io.vlingo.symbio.Metadata;
import io.vlingo.symbio.State;
import io.vlingo.symbio.store.state.StateStore;

import java.util.HashMap;
import java.util.Map;

public final class StateRecordAdapter {
    public static final String ID_FIELD = "Id";
    public static final String STATE_FIELD = "State";
    public static final String DATA_FIELD = "Data";
    public static final String TYPE_FIELD = "Type";
    public static final String METADATA_FIELD = "Metadata";
    public static final String TYPE_VERSION_FIELD = "TypeVersion";
    public static final String DATA_VERSION_FIELD = "DataVersion";

    public static Map<String, AttributeValue> marshall(State<String> state) {
        String metadataAsJson = JsonSerialization.serialized(state.metadata);

        Map<String, AttributeValue> stateItem = new HashMap<>();
        stateItem.put(ID_FIELD, new AttributeValue().withS(state.id));
        stateItem.put(DATA_FIELD, new AttributeValue().withS(state.data));
        stateItem.put(TYPE_FIELD, new AttributeValue().withS(state.type));
        stateItem.put(METADATA_FIELD, new AttributeValue().withS(metadataAsJson));
        stateItem.put(TYPE_VERSION_FIELD, new AttributeValue().withN(String.valueOf(state.typeVersion)));
        stateItem.put(DATA_VERSION_FIELD, new AttributeValue().withN(String.valueOf(state.dataVersion)));

        return stateItem;
    }

    public static Map<String, AttributeValue> marshall(StateStore.Dispatchable<String> dispatchable) {
        Map<String, AttributeValue> stateItem = new HashMap<>();
        stateItem.put(ID_FIELD, new AttributeValue().withS(dispatchable.id));
        stateItem.put(STATE_FIELD, new AttributeValue().withS(JsonSerialization.serialized(dispatchable.state)));

        return stateItem;
    }

    public static Map<String, AttributeValue> marshallForQuery(String id) {
        Map<String, AttributeValue> stateItem = new HashMap<>();
        stateItem.put(ID_FIELD, new AttributeValue().withS(id));

        return stateItem;
    }

    public static State<String> unmarshallState(Map<String, AttributeValue> record) throws ClassNotFoundException {
        return new State.TextState(
                record.get(ID_FIELD).getS(),
                Class.forName(record.get(TYPE_FIELD).getS()),
                Integer.valueOf(record.get(TYPE_VERSION_FIELD).getN()),
                record.get(DATA_FIELD).getS(),
                Integer.valueOf(record.get(DATA_VERSION_FIELD).getN()),
                JsonSerialization.deserialized(record.get(METADATA_FIELD).getS(), Metadata.class)
        );
    }
}
