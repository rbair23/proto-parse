package protoparser.parsers;

import com.hedera.hashgraph.protoparse.MalformedProtobufException;
import protoparser.model.Apple;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class AppleParser extends FruitParser {
	public Apple parse(byte[] protobuf) throws MalformedProtobufException {
		variety = "";
		super.start(protobuf);
		return new Apple(variety);
	}

	public Apple parse(ByteBuffer protobuf) throws MalformedProtobufException {
		variety = "";
		super.start(protobuf);
		return new Apple(variety);
	}

	public Apple parse(InputStream protobuf) throws IOException, MalformedProtobufException {
		variety = "";
		super.start(protobuf);
		return new Apple(variety);
	}
}
