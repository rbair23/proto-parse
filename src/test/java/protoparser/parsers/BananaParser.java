package protoparser.parsers;

import com.hedera.hashgraph.protoparse.MalformedProtobufException;
import protoparser.model.Banana;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class BananaParser extends FruitParser {
	public Banana parse(byte[] protobuf) throws MalformedProtobufException {
		variety = "";
		super.start(protobuf);
		return new Banana(variety);
	}

	public Banana parse(ByteBuffer protobuf) throws MalformedProtobufException {
		variety = "";
		super.start(protobuf);
		return new Banana(variety);
	}

	public Banana parse(InputStream protobuf) throws IOException, MalformedProtobufException {
		variety = "";
		super.start(protobuf);
		return new Banana(variety);
	}
}
