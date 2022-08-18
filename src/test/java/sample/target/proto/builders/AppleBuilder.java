package sample.target.proto.builders;

import com.hedera.hashgraph.protoparse.FieldDefinition;
import com.hedera.hashgraph.protoparse.FieldType;
import com.hedera.hashgraph.protoparse.ProtoBuilder;

import java.io.IOException;

public class AppleBuilder extends ProtoBuilder {
    private static final FieldDefinition VARIETY = new FieldDefinition("variety", FieldType.STRING, false, 1);

    public AppleBuilder start() {
        super.reset();
        return this;
    }

    public AppleBuilder variety(String value) throws IOException {
        super.writeString(VARIETY, value);
        return this;
    }
}
