package protoparser.parsers;

import com.hedera.hashgraph.protoparse.FieldDefinition;
import com.hedera.hashgraph.protoparse.FieldType;
import com.hedera.hashgraph.protoparse.MalformedProtobufException;
import com.hedera.hashgraph.protoparse.ProtoParser;
import protoparser.model.Apple;
import protoparser.model.Nested;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class NestedParser extends ProtoParser {
	private static final FieldDefinition NESTED_MEMO = new FieldDefinition("nestedMemo", FieldType.STRING, false, 100);

	private String memo = "";

	public Nested parse(byte[] protobuf) throws MalformedProtobufException {
		memo = "";
		super.start(protobuf);
		return new Nested(memo);
	}

	public Nested parse(ByteBuffer protobuf) throws MalformedProtobufException {
		memo = "";
		super.start(protobuf);
		return new Nested(memo);
	}

	public Nested parse(InputStream protobuf) throws IOException, MalformedProtobufException {
		memo = "";
		super.start(protobuf);
		return new Nested(memo);
	}

	@Override
	protected FieldDefinition getFieldDefinition(final int fieldNumber) {
		return switch (fieldNumber) {
			case 100 -> NESTED_MEMO;
			default -> null;
		};
	}

	@Override
	public void stringField(final int fieldNum, final String value) {
		if (fieldNum != NESTED_MEMO.number()) {
			throw new AssertionError("Unknown field number " + fieldNum);
		}

		this.memo = value;
	}
}
