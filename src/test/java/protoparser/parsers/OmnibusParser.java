package protoparser.parsers;

import com.hedera.hashgraph.protoparse.*;
import protoparser.model.Everything;
import protoparser.model.Fruit;
import protoparser.model.Nested;
import protoparser.model.Suit;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class OmnibusParser extends ProtoParser {
	private static final FieldDefinition INT32_NUMBER = new FieldDefinition("int32Number", FieldType.INT_32, false, 10);
	private static final FieldDefinition INT64_NUMBER = new FieldDefinition("int64Number", FieldType.INT_64, false, 11);
	private static final FieldDefinition UINT32_NUMBER = new FieldDefinition("uint32Number", FieldType.UINT_32, false, 12);
	private static final FieldDefinition UINT64_NUMBER = new FieldDefinition("unit64Number", FieldType.UINT_64, false, 13);
	private static final FieldDefinition FLAG = new FieldDefinition("flag", FieldType.BOOLEAN, false, 14);
	private static final FieldDefinition SUIT = new FieldDefinition("suitEnum", FieldType.ENUM, false, 15);
	private static final FieldDefinition SINT32_NUMBER = new FieldDefinition("sint32Number", FieldType.SINT_32, false, 30);
	private static final FieldDefinition SINT64_NUMBER = new FieldDefinition("sint64Number", FieldType.SINT_64, false, 31);
	private static final FieldDefinition SFIXED32_NUMBER = new FieldDefinition("sfixed32Number", FieldType.SFIXED_32, false, 20);
	private static final FieldDefinition SFIXED64_NUMBER = new FieldDefinition("sfixed64Number", FieldType.SFIXED_64, false, 25);
	private static final FieldDefinition FIXED32_NUMBER = new FieldDefinition("fixed32Number", FieldType.FIXED_32, false, 21);
	private static final FieldDefinition FIXED64_NUMBER = new FieldDefinition("fixed64Number", FieldType.FIXED_64, false, 26);
	private static final FieldDefinition FLOAT_NUMBER = new FieldDefinition("floatNumber", FieldType.FLOAT, false, 22);
	private static final FieldDefinition DOUBLE_NUMBER = new FieldDefinition("doubleNumber", FieldType.DOUBLE, false, 27);
	private static final FieldDefinition MEMO = new FieldDefinition("memo", FieldType.STRING, false, 1);
	private static final FieldDefinition RANDOM_BYTES = new FieldDefinition("randomBytes", FieldType.BYTES, false, 2);
	private static final FieldDefinition NESTED = new FieldDefinition("nested", FieldType.MESSAGE, false, 3);
	private static final FieldDefinition FRUIT_APPLE = new FieldDefinition("fruit.apple", FieldType.MESSAGE, false, 200);
	private static final FieldDefinition FRUIT_BANANA = new FieldDefinition("fruit.banana", FieldType.MESSAGE, false, 201);
	private static final FieldDefinition INT32_UNIQUE = new FieldDefinition("int32Unique", FieldType.INT_32, false, 210);
	private static final FieldDefinition INT64_UNIQUE = new FieldDefinition("int64Unique", FieldType.INT_64, false, 211);
	private static final FieldDefinition UINT32_UNIQUE = new FieldDefinition("uint32Unique", FieldType.UINT_32, false, 212);
	private static final FieldDefinition UINT64_UNIQUE = new FieldDefinition("uint64Unique", FieldType.UINT_64, false, 213);
	private static final FieldDefinition FLAG_UNIQUE = new FieldDefinition("flagUnique", FieldType.BOOLEAN, false, 214);
	private static final FieldDefinition SUIT_UNIQUE = new FieldDefinition("suiteEnumUnique", FieldType.ENUM, false, 215);
	private static final FieldDefinition SINT32_UNIQUE = new FieldDefinition("sint32Unique", FieldType.SINT_32, false, 230);
	private static final FieldDefinition SINT64_UNIQUE = new FieldDefinition("sint64Unique", FieldType.SINT_64, false, 231);
	private static final FieldDefinition SFIXED32_UNIQUE = new FieldDefinition("sfixed32Unique", FieldType.SFIXED_32, false, 220);
	private static final FieldDefinition SFIXED64_UNIQUE = new FieldDefinition("sfixed64Unique", FieldType.SFIXED_64, false, 225);
	private static final FieldDefinition FIXED32_UNIQUE = new FieldDefinition("fixed32Unique", FieldType.FIXED_32, false, 221);
	private static final FieldDefinition FIXED64_UNIQUE = new FieldDefinition("fixed64Unique", FieldType.FIXED_64, false, 226);
	private static final FieldDefinition FLOAT_UNIQUE = new FieldDefinition("floatUnique", FieldType.FLOAT, false, 222);
	private static final FieldDefinition DOUBLE_UNIQUE = new FieldDefinition("doubleUnique", FieldType.DOUBLE, false, 227);
	private static final FieldDefinition MEMO_UNIQUE = new FieldDefinition("memoUnique", FieldType.STRING, false, 251);
	private static final FieldDefinition RANDOM_BYTES_UNIQUE = new FieldDefinition("randomBytesUnique", FieldType.BYTES, false, 252);
	private static final FieldDefinition NESTED_UNIQUE = new FieldDefinition("nestedUnique", FieldType.MESSAGE, false, 253);
	private static final FieldDefinition INT32_REPEATED = new FieldDefinition("int32NumberList", FieldType.INT_32, true, 300);
	private static final FieldDefinition INT64_REPEATED = new FieldDefinition("int64NumberList", FieldType.INT_64, true, 301);
	private static final FieldDefinition UINT32_REPEATED = new FieldDefinition("uint32NumberList", FieldType.UINT_32, true, 302);
	private static final FieldDefinition UINT64_REPEATED = new FieldDefinition("uint64NumberList", FieldType.UINT_64, true, 303);
	private static final FieldDefinition FLAG_REPEATED = new FieldDefinition("flagList", FieldType.BOOLEAN, true, 304);
	private static final FieldDefinition SUIT_REPEATED = new FieldDefinition("suitEnumList", FieldType.ENUM, true, 305);
	private static final FieldDefinition SINT32_REPEATED = new FieldDefinition("sint32NumberList", FieldType.SINT_32, true, 306);
	private static final FieldDefinition SINT64_REPEATED = new FieldDefinition("sint64NumberList", FieldType.SINT_64, true, 307);
	private static final FieldDefinition SFIXED32_REPEATED = new FieldDefinition("sfixed32NumberList", FieldType.SFIXED_32, true, 308);
	private static final FieldDefinition SFIXED64_REPEATED = new FieldDefinition("sfixed64NumberList", FieldType.SFIXED_64, true, 309);
	private static final FieldDefinition FIXED32_REPEATED = new FieldDefinition("fixed32NumberList", FieldType.FIXED_32, true, 310);
	private static final FieldDefinition FIXED64_REPEATED = new FieldDefinition("fixed64NumberList", FieldType.FIXED_64, true, 311);
	private static final FieldDefinition FLOAT_REPEATED = new FieldDefinition("floatNumberList", FieldType.FLOAT, true, 312);
	private static final FieldDefinition DOUBLE_REPEATED = new FieldDefinition("doubleNumberList", FieldType.DOUBLE, true, 313);
	private static final FieldDefinition MEMO_REPEATED = new FieldDefinition("memoList", FieldType.STRING, true, 314);
	private static final FieldDefinition RANDOM_BYTES_REPEATED = new FieldDefinition("randomBytesList", FieldType.BYTES, true, 315);
	private static final FieldDefinition NESTED_REPEATED = new FieldDefinition("nestedList", FieldType.MESSAGE, true, 316);
	private static final FieldDefinition FRUITS_REPEATED = new FieldDefinition("fruitsList", FieldType.MESSAGE, true, 317);

	private int int32Number;
	private long int64Number;
	private int uint32Number;
	private long uint64Number;
	private boolean flag;
	private Suit suitEnum;

	private int sint32Number;
	private long sint64Number;

	private int sfixed32Number;
	private long sfixed64Number;
	private int fixed32Number;
	private long fixed64Number;
	private float floatNumber;
	private double doubleNumber;

	private String memo;
	private byte[] randomBytes;
	private Nested nested;

	private OneOf<Fruit, Object> fruit; // Apple or Banana

	private OneOf<Everything, Object> everything; // WHAT DO WE DO HERE??!!

	private List<Integer> int32NumberList = Collections.emptyList();
	private List<Long> int64NumberList = Collections.emptyList();
	private List<Integer> uint32NumberList = Collections.emptyList();
	private List<Long> uint64NumberList = Collections.emptyList();
	private List<Boolean> flagList = Collections.emptyList();
	private List<Suit> suitEnumList = Collections.emptyList();

	private List<Integer> sint32NumberList = Collections.emptyList();
	private List<Long> sint64NumberList = Collections.emptyList();

	private List<Integer> sfixed32NumberList = Collections.emptyList();
	private List<Long> sfixed64NumberList = Collections.emptyList();
	private List<Integer> fixed32NumberList = Collections.emptyList();
	private List<Long> fixed64NumberList = Collections.emptyList();
	private List<Float> floatNumberList = Collections.emptyList();
	private List<Double> doubleNumberList = Collections.emptyList();

	private List<String> memoList = Collections.emptyList();
	private List<byte[]> randomBytesList = Collections.emptyList();
	private List<Nested> nestedList = Collections.emptyList();

	private List<OneOf<Fruit, Object>> fruitList = Collections.emptyList(); // Apple or Banana

	public int getInt32Number() {
		return int32Number;
	}

	public long getInt64Number() {
		return int64Number;
	}

	public int getUint32Number() {
		return uint32Number;
	}

	public long getUint64Number() {
		return uint64Number;
	}

	public boolean isFlag() {
		return flag;
	}

	public Suit getSuitEnum() {
		return suitEnum;
	}

	public int getSint32Number() {
		return sint32Number;
	}

	public long getSint64Number() {
		return sint64Number;
	}

	public int getSfixed32Number() {
		return sfixed32Number;
	}

	public long getSfixed64Number() {
		return sfixed64Number;
	}

	public int getFixed32Number() {
		return fixed32Number;
	}

	public long getFixed64Number() {
		return fixed64Number;
	}

	public float getFloatNumber() {
		return floatNumber;
	}

	public double getDoubleNumber() {
		return doubleNumber;
	}

	public String getMemo() {
		return memo;
	}

	public byte[] getRandomBytes() {
		return randomBytes;
	}

	public Nested getNested() {
		return nested;
	}

	public OneOf<Fruit, Object> getFruit() {
		return fruit;
	}

	public OneOf<Everything, Object> getEverything() {
		return everything;
	}

	public List<Integer> getInt32NumberList() {
		return int32NumberList;
	}

	public List<Long> getInt64NumberList() {
		return int64NumberList;
	}

	public List<Integer> getUint32NumberList() {
		return uint32NumberList;
	}

	public List<Long> getUint64NumberList() {
		return uint64NumberList;
	}

	public List<Boolean> getFlagList() {
		return flagList;
	}

	public List<Suit> getSuitEnumList() {
		return suitEnumList;
	}

	public List<Integer> getSint32NumberList() {
		return sint32NumberList;
	}

	public List<Long> getSint64NumberList() {
		return sint64NumberList;
	}

	public List<Integer> getSfixed32NumberList() {
		return sfixed32NumberList;
	}

	public List<Long> getSfixed64NumberList() {
		return sfixed64NumberList;
	}

	public List<Integer> getFixed32NumberList() {
		return fixed32NumberList;
	}

	public List<Long> getFixed64NumberList() {
		return fixed64NumberList;
	}

	public List<Float> getFloatNumberList() {
		return floatNumberList;
	}

	public List<Double> getDoubleNumberList() {
		return doubleNumberList;
	}

	public List<String> getMemoList() {
		return memoList;
	}

	public List<byte[]> getRandomBytesList() {
		return randomBytesList;
	}

	public List<Nested> getNestedList() {
		return nestedList;
	}

	public List<OneOf<Fruit, Object>> getFruitList() {
		return fruitList;
	}

	public void parse(byte[] protobuf) throws MalformedProtobufException {
		reset();
		super.start(protobuf);
	}

	public void parse(ByteBuffer protobuf) throws MalformedProtobufException {
		reset();
		super.start(protobuf);
	}

	public void parse(InputStream protobuf) throws IOException, MalformedProtobufException {
		reset();
		super.start(protobuf);
	}

	private void reset() {
		this.int32Number = 0;
		this.int64Number = 0;
		this.uint32Number = 0;
		this.uint64Number = 0;
		this.flag = false;
		this.suitEnum = Suit.ACES; // enums must have default of first enum (ordinal 0)

		this.sint32Number = 0;
		this.sint64Number = 0;

		this.sfixed32Number = 0;
		this.sfixed64Number = 0;
		this.fixed32Number = 0;
		this.fixed64Number = 0;
		this.floatNumber = 0;
		this.doubleNumber = 0;

		this.memo = "";
		this.randomBytes = new byte[0]; // arrays must have default of empty
		this.nested = null;

		this.fruit = null;

		this.everything = null;

		this.int32NumberList = new LinkedList<>();
		this.int64NumberList = new LinkedList<>();
		this.uint32NumberList = new LinkedList<>();
		this.uint64NumberList = new LinkedList<>();
		this.flagList = new LinkedList<>();
		this.suitEnumList = new LinkedList<>();

		this.sint32NumberList = new LinkedList<>();
		this.sint64NumberList = new LinkedList<>();

		this.sfixed32NumberList = new LinkedList<>();
		this.sfixed64NumberList = new LinkedList<>();
		this.fixed32NumberList = new LinkedList<>();
		this.fixed64NumberList = new LinkedList<>();
		this.floatNumberList = new LinkedList<>();
		this.doubleNumberList = new LinkedList<>();

		this.memoList = new LinkedList<>();
		this.randomBytesList = new LinkedList<>();
		this.nestedList = new LinkedList<>();

		this.fruitList = new LinkedList<>(); // Apple or Banana
	}

	@Override
	protected FieldDefinition getFieldDefinition(final int fieldNumber) {
		return switch(fieldNumber) {
			case 10 -> INT32_NUMBER;
			case 11 -> INT64_NUMBER;
			case 12 -> UINT32_NUMBER;
			case 13 -> UINT64_NUMBER;
			case 14 -> FLAG;
			case 15 -> SUIT;
			case 30 -> SINT32_NUMBER;
			case 31 -> SINT64_NUMBER;
			case 20 -> SFIXED32_NUMBER;
			case 25 -> SFIXED64_NUMBER;
			case 21 -> FIXED32_NUMBER;
			case 26 -> FIXED64_NUMBER;
			case 22 -> FLOAT_NUMBER;
			case 27 -> DOUBLE_NUMBER;
			case 1 -> MEMO;
			case 2 -> RANDOM_BYTES;
			case 3 -> NESTED;
			case 200 -> FRUIT_APPLE;
			case 201 -> FRUIT_BANANA;
			case 210 -> INT32_UNIQUE;
			case 211 -> INT64_UNIQUE;
			case 212 -> UINT32_UNIQUE;
			case 213 -> UINT64_UNIQUE;
			case 214 -> FLAG_UNIQUE;
			case 215 -> SUIT_UNIQUE;
			case 230 -> SINT32_UNIQUE;
			case 231 -> SINT64_UNIQUE;
			case 220 -> SFIXED32_UNIQUE;
			case 225 -> SFIXED64_UNIQUE;
			case 221 -> FIXED32_UNIQUE;
			case 226 -> FIXED64_UNIQUE;
			case 222 -> FLOAT_UNIQUE;
			case 227 -> DOUBLE_UNIQUE;
			case 251 -> MEMO_UNIQUE;
			case 252 -> RANDOM_BYTES_UNIQUE;
			case 253 -> NESTED_UNIQUE;
			case 300 -> INT32_REPEATED;
			case 301 -> INT64_REPEATED;
			case 302 -> UINT32_REPEATED;
			case 303 -> UINT64_REPEATED;
			case 304 -> FLAG_REPEATED;
			case 305 -> SUIT_REPEATED;
			case 306 -> SINT32_REPEATED;
			case 307 -> SINT64_REPEATED;
			case 308 -> SFIXED32_REPEATED;
			case 309 -> SFIXED64_REPEATED;
			case 310 -> FIXED32_REPEATED;
			case 311 -> FIXED64_REPEATED;
			case 312 -> FLOAT_REPEATED;
			case 313 -> DOUBLE_REPEATED;
			case 314 -> MEMO_REPEATED;
			case 315 -> RANDOM_BYTES_REPEATED;
			case 316 -> NESTED_REPEATED;
			case 317 -> FRUITS_REPEATED;
			default -> throw new AssertionError("Unknown field type!! Test bug? Or intentional...?");
		};
	}

	@Override
	public void intField(final int fieldNum, final int value) {
		switch (fieldNum) {
			case 10 -> int32Number = value;
			case 12 -> uint32Number = value;
			case 30 -> sint32Number = value;
			case 20 -> sfixed32Number = value;
			case 21 -> fixed32Number = value;
			case 210 -> everything = new OneOf<>(fieldNum, Everything.INT32, value);
			case 212 -> everything = new OneOf<>(fieldNum, Everything.UINT32, value);
			case 230 -> everything = new OneOf<>(fieldNum, Everything.SINT32, value);
			case 220 -> everything = new OneOf<>(fieldNum, Everything.SFIXED32, value);
			case 221 -> everything = new OneOf<>(fieldNum, Everything.FIXED32, value);
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void longField(final int fieldNum, final long value) {
		switch (fieldNum) {
			case 11 -> int64Number = value;
			case 13 -> uint64Number = value;
			case 31 -> sint64Number = value;
			case 25 -> sfixed64Number = value;
			case 26 -> fixed64Number = value;
			case 211 -> everything = new OneOf<>(fieldNum, Everything.INT64, value);
			case 213 -> everything = new OneOf<>(fieldNum, Everything.UINT64, value);
			case 231 -> everything = new OneOf<>(fieldNum, Everything.SINT64, value);
			case 225 -> everything = new OneOf<>(fieldNum, Everything.SFIXED64, value);
			case 226 -> everything = new OneOf<>(fieldNum, Everything.FIXED64, value);
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void booleanField(final int fieldNum, final boolean value) {
		switch (fieldNum) {
			case 14 -> flag = value;
			case 214 -> everything = new OneOf<>(fieldNum, Everything.FLAG, value);
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void floatField(final int fieldNum, final float value) {
		switch (fieldNum) {
			case 22 -> floatNumber = value;
			case 222 -> everything = new OneOf<>(fieldNum, Everything.FLOAT, value);
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void doubleField(final int fieldNum, final double value) {
		switch (fieldNum) {
			case 27 -> doubleNumber = value;
			case 227 -> everything = new OneOf<>(fieldNum, Everything.DOUBLE, value);
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void enumField(final int fieldNum, final int ordinal) {
		switch (fieldNum) {
			case 15 -> suitEnum = Suit.fromOrdinal(ordinal);
			case 215 -> everything = new OneOf<>(fieldNum, Everything.SUIT, Suit.fromOrdinal(ordinal));
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void stringField(final int fieldNum, final String value) {
		switch (fieldNum) {
			case 1 -> memo = value;
			case 251 -> everything = new OneOf<>(fieldNum, Everything.MEMO, value);
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void bytesField(int fieldNum, byte[] value) {
		switch (fieldNum) {
			case 2 -> randomBytes = Arrays.copyOf(value, value.length);
			case 252 -> everything = new OneOf<>(fieldNum, Everything.RANDOM_BYTES, Arrays.copyOf(value, value.length));
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void objectField(int fieldNum, long length, InputStream protoStream) throws IOException, MalformedProtobufException {
		switch (fieldNum) {
			case 3 -> nested = new NestedParser().parse(protoStream);
			case 200 -> fruit = new OneOf<>(fieldNum, Fruit.APPLE, new AppleParser().parse(protoStream));
			case 201 -> fruit = new OneOf<>(fieldNum, Fruit.BANANA, new BananaParser().parse(protoStream));
			case 253 -> everything = new OneOf<>(fieldNum, Everything.NESTED, new NestedParser().parse(protoStream));
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void intList(int fieldNum, List<Integer> value) {
		switch (fieldNum) {
			case 300 -> int32NumberList = value;
			case 302 -> uint32NumberList = value;
			case 306 -> sint32NumberList = value;
			case 308 -> sfixed32NumberList = value;
			case 310 -> fixed32NumberList = value;
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void longList(int fieldNum, List<Long> value) {
		switch (fieldNum) {
			case 301 -> int64NumberList = value;
			case 303 -> uint64NumberList = value;
			case 307 -> sint64NumberList = value;
			case 309 -> sfixed64NumberList = value;
			case 311 -> fixed64NumberList = value;
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void booleanList(int fieldNum, List<Boolean> value) {
		switch (fieldNum) {
			case 304 -> flagList = value;
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void stringList(int fieldNum, List<String> value) {
		switch (fieldNum) {
			case 314 -> memoList = value;
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

	@Override
	public void bytesList(int fieldNum, List<byte[]> value) {
		switch (fieldNum) {
			case 315 -> randomBytesList = value;
			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
		}
	}

//	@Override
//	public void objectList(int fieldNum, long length, InputStream protoStream) throws IOException, MalformedProtobufException {
//		switch (fieldNum) {
//			case 3 -> nested = new NestedParser().parse(protoStream);
//			case 200 -> fruit = new OneOf<>(fieldNum, Fruit.APPLE, new AppleParser().parse(protoStream));
//			case 201 -> fruit = new OneOf<>(fieldNum, Fruit.BANANA, new BananaParser().parse(protoStream));
//			case 253 -> everything = new OneOf<>(fieldNum, Everything.NESTED, new NestedParser().parse(protoStream));
//			default -> throw new AssertionError("Not implemented in test code fieldNum='" + fieldNum + "'");
//		}
//	}

}
