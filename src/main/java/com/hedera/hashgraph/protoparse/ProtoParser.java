package com.hedera.hashgraph.protoparse;

import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class ProtoParser implements ParseListener {

	private static final int WIRE_TYPE_VARINT_OR_ZIGZAG = 0;
	private static final int WIRE_TYPE_FIXED_64_BIT = 1;
	private static final int WIRE_TYPE_DELIMITED = 2;
	private static final int WIRE_TYPE_GROUP_START = 3;
	private static final int WIRE_TYPE_GROUP_END = 4;
	private static final int WIRE_TYPE_FIXED_32_BIT = 5;

	private static final int TAG_FIELD_OFFSET = 3;
	private static final int TAG_WRITE_TYPE_MASK = 0b0000_0111;
	private static final int MSB_SET_MASK = 0b1000_0000;

	private final byte[] protobuf;
	private int currentIndex = 0;
	private final int maxLength;

	protected ProtoParser(byte[] protobuf) {
		this.protobuf = protobuf;
		this.maxLength = protobuf.length;
	}
	

	protected final void start() throws MalformedProtobufException {

		// If protobuf byte[] is empty, then return null (valid protobuf encoding can be 0+ tag/value pairs)
		if (protobuf == null || protobuf.length == 0) {
			return;
		}

		while (currentIndex < maxLength - 1) {
			int tag = readByte();
			int field = tag >> TAG_FIELD_OFFSET;
			int wireType = tag & TAG_WRITE_TYPE_MASK;

			// Validate we have more bytes to read
			if (currentIndex >= maxLength) {
				throw new MalformedProtobufException("Bad protobuf encoding. We read a tag, but not a value");
			}

			// Validate the field number is valid (must be > 0)
			if (field <= 0) {
				throw new MalformedProtobufException("Bad protobuf encoding. We read a field value of " + field);
			}

			// Validate the wire type is valid (must be >=0 && <= 5). Otherwise we cannot parse this.
			// Note: it is always >= 0 at this point (see code above where it is defined).
			if (wireType > 5) {
				throw new MalformedProtobufException("Cannot understand wire_type of " + wireType);
			}

			final var f = getFieldDefinition(field);
//			if (wt.ordinal() != wireType) {
//				throw new MalformedProtobufException("Bad protobuf encoding. Unexpected wire type of " + wt + " for field " + field);
//			}

			switch (f.type()) {
				case INT_64 -> longField(field, readInt64());
				case INT_32 -> intField(field, readInt32());
				case BOOLEAN -> booleanField(field, readBool());
				default -> {
				}
				// Unknown field (maybe OK if we're parsing a newer version of the schema...?)
			}
		}
	}

	protected abstract FieldDefinition getFieldDefinition(int fieldNumber);

	private long readVarint() {
		// TODO Potentially reads bit values Can be quite a number of bytes! 9 bytes max? Or 10? Worst case when int64 is all 1's
		int maxIndex = maxLength; //Math.min(currentIndex + 9, encoded.length);
		long value = 0;
		long shift = 0;
		while(currentIndex < maxIndex) {
			byte b = protobuf[currentIndex++];
			if ((b & MSB_SET_MASK) != 0) {
				// msb is set, so there is another byte following this one
//					if (currentIndex > maxIndex) {
//						throw new RuntimeException("Bad protobuf encoding, MSB set on last byte of varint!");
//					}

				b = (byte)(b & (byte) 0b0111_1111);
				long toBeAdded = (long)b << shift;
				value |= toBeAdded;
				shift += 7;
			} else {
				long toBeAdded = (long)b << shift;
				value |= toBeAdded;
				break;
			}
		}

		return value;
	}


	private int readByte() {
		return (int) readVarint();
	}

	private int readInt32() {
		return (int) readVarint();
	}

	private long readInt64() {
		return readVarint();
	}

	private int readUint32() {
		throw new UnsupportedOperationException("Ya, not yet.");
	}

	private long readUint64() {
		throw new UnsupportedOperationException("Ya, not yet.");
	}

	private int readSint32() {
		throw new UnsupportedOperationException("Ya, not yet.");
	}

	private long readSint64() {
		throw new UnsupportedOperationException("Ya, not yet.");
	}

	private boolean readBool() {
		final var i = readVarint();
		if (i != 1 && i != 0) {
			throw new RuntimeException("Bad protobuf encoding. Boolean was not 0 or 1");
		}
		return i == 1;
	}
}
