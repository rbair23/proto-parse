package sample.target.proto.writers;

import com.hedera.hashgraph.protoparse.ProtoOutputStream;
import com.hedera.hashgraph.protoparse.ProtoWriter;
import sample.target.model.Nested;
import sample.target.proto.schemas.NestedSchema;

import java.io.IOException;
import java.io.OutputStream;

public class NestedWriter implements ProtoWriter<Nested> {
    @Override
    public void write(Nested nested, OutputStream out) throws IOException {
        final var pb = new ProtoOutputStream(out);
        pb.writeString(NestedSchema.NESTED_MEMO, nested.nestedMemo());
    }
}
