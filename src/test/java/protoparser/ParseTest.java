package protoparser;

import com.google.protobuf.ByteString;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import protoparser.model.Everything;
import protoparser.model.Fruit;
import protoparser.model.Suit;
import protoparser.parsers.OmnibusParser;
import test.proto.Apple;
import test.proto.Banana;
import test.proto.Nested;
import test.proto.Omnibus;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

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

	static Stream<byte[]> byteArrays() {
		return Stream.of(
				new byte[0],
				new byte[]{0b001},
				new byte[]{0b001, 0b010, 0b011});
	}

	@ParameterizedTest
	@MethodSource("byteArrays")
	void parseByteArraysOnly(byte[] val) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setRandomBytes(ByteString.copyFrom(val))
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertArrayEquals(val, parser.getRandomBytes());
	}

	@ParameterizedTest
	@ValueSource(strings = {"", "", ""})
	void parseNestedObjectOnly(String nestedMemo) throws Exception {
		final var protobuf = test.proto.Omnibus.newBuilder()
				.setNested(Nested.newBuilder().setNestedMemo(nestedMemo))
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(new protoparser.model.Nested(nestedMemo), parser.getNested());
	}

	@Test
	void parseOneOfFruitOnly() throws Exception {
		var protobuf = Omnibus.newBuilder()
				.setApple(Apple.newBuilder().setVariety("Gala").build())
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Fruit.APPLE, parser.getFruit().kind());
		assertEquals(new protoparser.model.Apple("Gala"), parser.getFruit().value());

		protobuf = Omnibus.newBuilder()
				.setBanana(Banana.newBuilder().setVariety("Yellow").build())
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Fruit.BANANA, parser.getFruit().kind());
		assertEquals(new protoparser.model.Banana("Yellow"), parser.getFruit().value());
	}

	@Test
	void parseOneOfFruitOnlyLastOneWins() throws Exception {
		var protobuf = Omnibus.newBuilder()
				.setApple(Apple.newBuilder().setVariety("Gala").build())
				.setBanana(Banana.newBuilder().setVariety("Yellow").build())
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Fruit.BANANA, parser.getFruit().kind());
		assertEquals(new protoparser.model.Banana("Yellow"), parser.getFruit().value());
	}

	@Test
	void parseOneOfEverythingOnly() throws Exception {
		var protobuf = Omnibus.newBuilder()
				.setInt32Unique(-42)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.INT32, parser.getEverything().kind());
		assertEquals(-42, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setInt64Unique(-43)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.INT64, parser.getEverything().kind());
		assertEquals(-43L, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setUint32Unique(44)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.UINT32, parser.getEverything().kind());
		assertEquals(44, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setUint64Unique(45)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.UINT64, parser.getEverything().kind());
		assertEquals(45L, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setFlagUnique(true)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.FLAG, parser.getEverything().kind());
		assertTrue((boolean) parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setSuitEnumUnique(test.proto.Suit.CLUBS)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.SUIT, parser.getEverything().kind());
		assertEquals(Suit.CLUBS, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setSint32Unique(-46)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.SINT32, parser.getEverything().kind());
		assertEquals(-46, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setSint64Unique(-47)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.SINT64, parser.getEverything().kind());
		assertEquals(-47L, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setSfixed32Unique(-48)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.SFIXED32, parser.getEverything().kind());
		assertEquals(-48, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setSfixed64Unique(-49)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.SFIXED64, parser.getEverything().kind());
		assertEquals(-49L, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setFixed32Unique(50)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.FIXED32, parser.getEverything().kind());
		assertEquals(50, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setFixed64Unique(51)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.FIXED64, parser.getEverything().kind());
		assertEquals(51L, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setFloatUnique(52.3f)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.FLOAT, parser.getEverything().kind());
		assertEquals(52.3f, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setDoubleUnique(54.5)
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.DOUBLE, parser.getEverything().kind());
		assertEquals(54.5, parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setMemoUnique("Learn BASIC Now!")
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.MEMO, parser.getEverything().kind());
		assertEquals("Learn BASIC Now!", parser.getEverything().value());

		protobuf = Omnibus.newBuilder()
				.setRandomBytesUnique(ByteString.copyFrom(new byte[]{(byte) 55, (byte) 56}))
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.RANDOM_BYTES, parser.getEverything().kind());
		assertArrayEquals(new byte[]{(byte) 55, (byte) 56}, parser.getEverything().as());

		protobuf = Omnibus.newBuilder()
				.setNestedUnique(Nested.newBuilder().setNestedMemo("Reminder").build())
				.build()
				.toByteArray();

		parser.parse(protobuf);
		assertEquals(Everything.NESTED, parser.getEverything().kind());
		assertEquals(new protoparser.model.Nested("Reminder"), parser.getEverything().value());
	}

}
