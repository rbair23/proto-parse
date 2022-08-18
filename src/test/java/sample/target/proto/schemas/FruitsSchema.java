package sample.target.proto.schemas;

import com.hedera.hashgraph.protoparse.FieldDefinition;
import com.hedera.hashgraph.protoparse.FieldType;

public final class FruitsSchema {
    public static final FieldDefinition APPLE = new FieldDefinition("apple", FieldType.MESSAGE, false, 1);
    public static final FieldDefinition BANANA = new FieldDefinition("banana", FieldType.MESSAGE, false, 2);
}
