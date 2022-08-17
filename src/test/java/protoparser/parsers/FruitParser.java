package protoparser.parsers;

import com.hedera.hashgraph.protoparse.FieldDefinition;
import com.hedera.hashgraph.protoparse.FieldType;
import com.hedera.hashgraph.protoparse.MalformedProtobufException;
import com.hedera.hashgraph.protoparse.ProtoParser;
import protoparser.model.Apple;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public abstract class FruitParser extends ProtoParser {
	private static final FieldDefinition VARIETY = new FieldDefinition("variety", FieldType.STRING, false, 1);

	@Override
	protected FieldDefinition getFieldDefinition(final int fieldNumber) {
		return switch (fieldNumber) {
			case 1 -> VARIETY;
			default -> null;
		};
	}

	protected String variety = "";

	@Override
	public void stringField(final int fieldNum, final String value) {
		if (fieldNum != VARIETY.number()) {
			throw new AssertionError("Unknown field number " + fieldNum);
		}

		this.variety = value;
	}
}
