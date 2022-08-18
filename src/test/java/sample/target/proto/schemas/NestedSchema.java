package sample.target.proto.schemas;

import com.hedera.hashgraph.protoparse.FieldDefinition;
import com.hedera.hashgraph.protoparse.FieldType;

public final class NestedSchema {
    public static final FieldDefinition NESTED_MEMO = new FieldDefinition("nestedMemo", FieldType.STRING, false, 100);
}
