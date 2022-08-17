package protoparser;

import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import protoparser.model.Suit;
import protoparser.parsers.OmnibusParser;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParseTest {

	private final OmnibusParser parser = new OmnibusParser();

	@ParameterizedTest
	@ValueSource(ints = { Integer.MIN_VALUE, -5, 1, 3, 5, Integer.MAX_VALUE })
	void parseInt32Only(int val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setInt32Number(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getInt32Number());
	}

	@ParameterizedTest
	@ValueSource(longs = { Long.MIN_VALUE, -5, 1, 3, 5, Long.MAX_VALUE })
	void parseInt64Only(long val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setInt64Number(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getInt64Number());
	}

	@ParameterizedTest
	@ValueSource(ints = { Integer.MIN_VALUE, -5, 1, 3, 5, Integer.MAX_VALUE })
	void parseUint32Only(int val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setUint32Number(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getUint32Number());
	}

	@ParameterizedTest
	@ValueSource(longs = { Long.MIN_VALUE, -5, 1, 3, 5, Long.MAX_VALUE })
	void parseUint64Only(long val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setUint64Number(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getUint64Number());
	}

	@ParameterizedTest
	@ValueSource(booleans = { false, true })
	void parseBooleanOnly(boolean val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setFlag(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.isFlag());
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 2, 3 })
	void parseSuitOnly(int ordinal) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setSuitEnum(test.proto.Suit.valueOf(Suit.fromOrdinal(ordinal).name()))
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(ordinal, parser.getSuitEnum().ordinal());
	}

	@ParameterizedTest
	@ValueSource(ints = { Integer.MIN_VALUE, -102, -5, 1, 3, 5, 42, Integer.MAX_VALUE })
	void parseSint32Only(int val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setSint32Number(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getSint32Number());
	}

	@ParameterizedTest
	@ValueSource(longs = { Long.MIN_VALUE, -102, -5, 1, 3, 5, 42, Long.MAX_VALUE })
	void parseSint64Only(long val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setSint64Number(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getSint64Number());
	}

	@ParameterizedTest
	@ValueSource(ints = { Integer.MIN_VALUE, -102, -5, 1, 3, 5, 42, Integer.MAX_VALUE })
	void parseSfixed32Only(int val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setSfixed32Number(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getSfixed32Number());
	}

	@ParameterizedTest
	@ValueSource(longs = { Long.MIN_VALUE, -102, -5, 1, 3, 5, 42, Long.MAX_VALUE })
	void parseSfixed64Only(long val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setSfixed64Number(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getSfixed64Number());
	}

	@ParameterizedTest
	@ValueSource(ints = { Integer.MIN_VALUE, -102, -5, 1, 3, 5, 42, Integer.MAX_VALUE })
	void parseFixed32Only(int val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setFixed32Number(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getFixed32Number());
	}

	@ParameterizedTest
	@ValueSource(longs = { Long.MIN_VALUE, -102, -5, 1, 3, 5, 42, Long.MAX_VALUE })
	void parseFixed64Only(long val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setFixed64Number(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getFixed64Number());
	}

	@ParameterizedTest
	@ValueSource(floats = { Float.NEGATIVE_INFINITY, Float.MIN_VALUE, -102.7f, -5f, 1.7f, 3, 5.2f, 42.1f, Float.MAX_VALUE, Float.POSITIVE_INFINITY, Float.NaN })
	void parseFloatOnly(float val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setFloatNumber(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getFloatNumber());
	}

	@ParameterizedTest
	@ValueSource(doubles = { Double.NEGATIVE_INFINITY, Double.MIN_VALUE, -102.7, -5, 1.7, 3, 5.2, 42.1, Double.MAX_VALUE, Double.POSITIVE_INFINITY, Double.NaN })
	void parseDoubleOnly(double val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setDoubleNumber(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getDoubleNumber());
	}

	@ParameterizedTest
	@ValueSource(strings = { "", "A String", "I need some ℏ to run work on Hedera!" })
	void parseStringOnly(String val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setMemo(val)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(val, parser.getMemo());
	}

	@Test
	void parseByteArraysOnly() throws Exception {
//		final var protobuf = test.proto.Omnibus.newBuilder()
//				.setRandomBytes(val)
//				.build()
//				.toByteArray();
//
//		parser.parse(protobuf);
//		assertEquals(val, parser.getMemo());
	}
}
