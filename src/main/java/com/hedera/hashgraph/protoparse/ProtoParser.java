package com.hedera.hashgraph.protoparse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * An abstract class from which fast and highly efficient Protobuf parsers may be built.
 *
 * <p>A {@code ProtoParser} cannot be directly instantiated, it must be extended. Each parser
 * instance can only be used by a single thread at a time (it is **NOT** threadsafe).
 * Typically, subclasses will be specific to a single Protobuf message and will store
 * parsed fields as local instance variables. The parser itself is designed to minimize temporary
 * object allocation. This is accomplished in several ways.
 *
 * <p>First, the parser operates on ByteBuffers or InputStreams types rather than directly
 * on byte[]. A ByteBuffer or InputStream can wrap a byte[] which can be easily reused for
 * multiple independent parsing operations, which can eliminate object allocations unless
 * the backing byte[] must be expanded (and even then, additional arrays can be chained
 * together rather than reclaiming old arrays). While outside the scope of this class, it
 * is useful to understand that the choice of ByteBuffer and InputStream is deliberate
 * because it encourages optimizations that limit object allocations.
 *
 * <p>Second, while a single instance of a ProtoParser can be used by a single thread at
 * a time, a single instance can be reused between runs. If the subclass has any local
 * instance data that must be reset, it must do this before calling one of the {@code start}
 * methods.
 *
 * <p>Third, this class was designed to avoid creating temporary objects during the
 * parsing process by working with primitives wherever possible. Protobuf parsing involves
 * walking over a stream of bytes (possibly in a buffer or in an input stream) one at
 * a time, decoding them, determining the field to be parsed, and then parsing its data
 * in a type specific manner. The wire format does not indicate the type of the field,
 * this must be known based on a protobuf schema out-of-band of the actual bytes to
 * be parsed.
 *
 * <p>To support this efficiently, subclasses of the ProtoParser have three tasks.
 * First, they must override the {@link #getFieldDefinition(int)} method. Given a field
 * number, the subclass must return a {@link FieldDefinition}, which among other things,
 * includes the type. Second, for each type supported in the specific schema, the subclass
 * must implement the corresponding method defined in the {@link ParseListener} interface.
 * Third, for each parsed field, the subclass must hold onto the parsed data until
 * parsing is complete and the final object representing the protobuf can be created
 * (if using immutable types).
 */
public abstract class ProtoParser implements ParseListener {

	// In protobuf, the "wire type" indicates the way the value was encoded on the wire.
	// Normally this isn't needed because when I know the field type, I know what the wire
	// type should be (although this should be validated). However, this is needed when
	// "skipping" bytes for unknown fields.
	private static final int WIRE_TYPE_VARINT_OR_ZIGZAG = 0;
	private static final int WIRE_TYPE_FIXED_64_BIT = 1;
	private static final int WIRE_TYPE_DELIMITED = 2;
	private static final int WIRE_TYPE_GROUP_START = 3;
	private static final int WIRE_TYPE_GROUP_END = 4;
	private static final int WIRE_TYPE_FIXED_32_BIT = 5;

	/**
	 * The number of lower order bits from the "tag" byte that should be rotated out
	 * to reveal the field number
	 */
	private static final int TAG_FIELD_OFFSET = 3;

	/**
	 * Mask used to extract the wire type from the "tag" byte
	 */
	private static final int TAG_WRITE_TYPE_MASK = 0b0000_0111;

	/**
	 * Mask used to read the continuation bit from a varint encoded byte
	 */
	private static final int VARINT_CONTINUATION_MASK = 0b1000_0000;

	/**
	 * Mask used to read off the actual data bits from a varint encoded byte
	 */
	private static final int VARINT_DATA_MASK = 0b0111_1111;

	/**
	 * The number of actual data bits in a varint byte
	 */
	private static final int NUM_BITS_PER_VARINT_BYTE = 7;

	/**
	 * The protobuf data as a stream of bytes. This may be supplied directly by the caller
	 * seeking to parse protobuf, or a reusable InputStream adapter provided by this class
	 * can be used to wrap a ByteBuffer or byte[] passed to the class to be parsed.
 	 */
	private final ProtoStream protoStream = new ProtoStream();

	private final ByteArrayInputStreamAdapter byteArrayInputStreamAdapter = new ByteArrayInputStreamAdapter();
	private final ByteBufferInputStreamAdapter byteBufferInputStreamAdapter = new ByteBufferInputStreamAdapter();

	/**
	 * Used for reading longs from the input stream.
	 */
	private final byte[] readBuffer = new byte[8];

	/**
	 * Instances of this class may only be instantiated by subclasses. It is recommended to cache
	 * and reuse these parsers when object allocation is a concern.
	 */
	protected ProtoParser() {

	}

	/**
	 * Starts parsing the protobuf bytes within the given byte buffer. This method takes the byte buffer
	 * as given, at the position and limit given, and does not modify the position in the buffer.
	 *
	 * @param protobuf the protobuf bytes. May be null or empty.
	 * @throws MalformedProtobufException If the protobuf bytes themselves are non-empty and invalid
	 */
	protected final void start(ByteBuffer protobuf) throws MalformedProtobufException {
		// If protobuf buffer is empty, then return null (valid protobuf encoding can be 0+ tag/value pairs)
		if (protobuf == null) {
			return;
		}

		// Reuse the existing input stream that wraps a byte buffer and delegate to the other start method.
		try {
			byteBufferInputStreamAdapter.reset(protobuf);
			this.start(byteBufferInputStreamAdapter);
		} catch (IOException ignored) {
			assert false : "It should never be possible for IOException to be thrown here.";
		}
	}

	/**
	 * Starts parsing the protobuf bytes in the array.
	 *
	 * @param protobuf the protobuf bytes. May be null or empty.
	 * @throws MalformedProtobufException If the protobuf bytes themselves are non-empty and invalid
	 */
	protected final void start(byte[] protobuf) throws MalformedProtobufException {
		// If protobuf byte[] is empty, then return null (valid protobuf encoding can be 0+ tag/value pairs)
		if (protobuf == null) {
			return;
		}

		// Reuse the existing input stream that wraps a byte array and delegate to the other start method.
		try {
			byteArrayInputStreamAdapter.reset(protobuf);
			this.start(byteArrayInputStreamAdapter);
		} catch (IOException ignored) {
			assert false : "It should never be possible for IOException to be thrown here.";
		}
	}

	/**
	 * Starts parsing the given protobuf input stream. For each field found in the stream, the
	 * {@link #getFieldDefinition(int)} method will be called to understand how to parse out
	 * the field. For each parsed field, a corresponding callback from {@link ParseListener}
	 * will be called.
	 *
	 * @param protobuf The stream. If null, the method returns immediately. If there are no
	 *                 initial bytes, then the method also returns immediately.
	 * @throws IOException If thrown by the InputStream
	 * @throws MalformedProtobufException If the protobuf stream is not empty and has malformed
	 * 									  protobuf bytes (i.e. isn't valid protobuf).
	 */
	protected final void start(InputStream protobuf) throws IOException, MalformedProtobufException {
		// If protobuf stream is null, then return null (valid protobuf encoding can be 0+ tag/value pairs)
		if (protobuf == null) {
			return;
		}

		// Reset internal state
		protoStream.reset(protobuf);

		// Continue to parse bytes out of the input stream until we get to the end.
		while (!protoStream.eof()) {
			// Read the "tag" byte which gives us the field number for the next field to read
			// and the wire type (way it is encoded on the wire).
			int tag = (int) readVarint("TAG", false);
			// If the tag is -1 then we never read it from the InputStream because it was empty.
			// It is OK for the input stream to be empty, it just means we were handed an empty
			// InputStream to start with (or byte[] or byte buffer) and we can just return.
			if (tag == -1) {
				return;
			}

			// The field is the top 5 bits of the byte. Read this off
			int field = tag >> TAG_FIELD_OFFSET;
			// The wire type is the bottom 3 bits of the byte. Read that off
			int wireType = tag & TAG_WRITE_TYPE_MASK;

			// Validate the field number is valid (must be > 0)
			if (field == 0) {
				throw new MalformedProtobufException("Bad protobuf encoding. We read a field value of " + field);
			}

			// Validate the wire type is valid (must be >=0 && <= 5). Otherwise we cannot parse this.
			// Note: it is always >= 0 at this point (see code above where it is defined).
			if (wireType > 5) {
				throw new MalformedProtobufException("Cannot understand wire_type of " + wireType);
			}

			// Ask the subclass to inform us what field this represents.
			final var f = getFieldDefinition(field);

			// It may be that the parser subclass doesn't know about this field. In that case, we
			// just need to read off the bytes for this field to skip it and move on to the next one.
			if (f == null) {
				skipField(wireType);
			} else {
				// Given the wire type and the field type, parse the field
				// (which will also invoke the appropriate callback).
				// TODO Validate that the wire type is of the expected kind
				switch (f.type()) {
					case INT_32 -> {
						if (f.repeated()) {
							final var length = readLengthFromStream();
							throw new UnsupportedOperationException("Wow");
						} else {
							intField(field, readInt32(f.name()));
						}
					}
					case INT_64 -> longField(field, readInt64(f.name()));
					case UINT_32 -> intField(field, readUint32(f.name())); // Oof
					case UINT_64 -> longField(field, readUint64(f.name())); // Oof
					case BOOLEAN -> booleanField(field, readBool(f.name()));
					case ENUM -> enumField(field, readEnum(f.name()));

					case SINT_32 -> intField(field, readSignedInt32(f.name()));
					case SINT_64 -> longField(field, readSignedInt64(f.name()));

					case SFIXED_32 -> intField(field, readSignedFixed32(f.name()));
					case FIXED_32 -> intField(field, readFixed32(f.name()));
					case FLOAT -> floatField(field, readFloat(f.name()));
					case SFIXED_64 -> longField(field, readSignedFixed64(f.name()));
					case FIXED_64 -> longField(field, readFixed64(f.name()));
					case DOUBLE -> doubleField(field, readDouble(f.name()));
					case MESSAGE -> objectField(field, readLengthFromStream(), protoStream);
					case STRING -> stringField(field, readString(f.name()));
					case BYTES -> bytesField(field, readBytes(f.name()));
//					case PACKED -> null; // TODO
					default -> {
						throw new MalformedProtobufException(
								"Unexpected and unknown field type " + f.type() + " cannot be parsed");
					}
				}
			}
		}
	}

	/**
	 * Gets a {@link FieldDefinition} corresponding to the given field number. It may be that this protobuf
	 * parser is parsing from a newer version of the schema than this parser knows about (for example, maybe
	 * the schema known by this parser is the 10th revision but the bytes are from a newer 11th revision).
	 * In that case, {@code null} will be returned.
	 *
	 * @param fieldNumber The field number. Will always be positive.
	 * @return The corresponding {@link FieldDefinition}, or null if the field is unknown.
	 */
	protected abstract FieldDefinition getFieldDefinition(int fieldNumber);

	private int readInt32(String fieldName) throws IOException, MalformedProtobufException {
		return (int) readVarint(fieldName, false);
	}

	private long readInt64(String fieldName) throws IOException, MalformedProtobufException {
		return readVarint(fieldName, false);
	}

	private int readUint32(String fieldName) throws IOException, MalformedProtobufException {
		return (int) readVarint(fieldName, false);
	}

	private long readUint64(String fieldName) throws IOException, MalformedProtobufException {
		return readVarint(fieldName, false);
	}

	private boolean readBool(String fieldName) throws IOException, MalformedProtobufException {
		final var i = readVarint(fieldName, false);
		if (i != 1 && i != 0) {
			throw new MalformedProtobufException("Bad protobuf encoding. Boolean was not 0 or 1");
		}
		return i == 1;
	}

	private int readEnum(String fieldName) throws IOException, MalformedProtobufException {
		final var i = readVarint(fieldName, false);
		return (int) i;
	}

	private int readSignedInt32(String fieldName) throws IOException, MalformedProtobufException {
		return (int) readVarint(fieldName, true);
	}

	private long readSignedInt64(String fieldName) throws IOException, MalformedProtobufException {
		return readVarint(fieldName, true);
	}

	private int readSignedFixed32(String fieldName) throws IOException, MalformedProtobufException {
		return readIntFromStream();
	}

	private int readFixed32(String fieldName) throws IOException, MalformedProtobufException {
		return readIntFromStream();
	}

	private float readFloat(String fieldName) throws IOException, MalformedProtobufException {
		return Float.intBitsToFloat(readIntFromStream());
	}

	private long readSignedFixed64(String fieldName) throws IOException, MalformedProtobufException {
		return readLongFromStream();
	}

	private long readFixed64(String fieldName) throws IOException, MalformedProtobufException {
		return readLongFromStream();
	}

	private double readDouble(String fieldName) throws IOException, MalformedProtobufException {
		return Double.longBitsToDouble(readLongFromStream());
	}

	private String readString(String fieldName) throws IOException, MalformedProtobufException {
		final long length = readLengthFromStream(); // TODO If length > 2GB throw like mad
		final byte[] data = new byte[(int) length]; // TODO Reuse buffer
		final long read = protoStream.read(data, 0, (int) length);
		if (read != length) {
			throw new MalformedProtobufException("Truncated protobuf, missing at least " +
					(length - read) + " bytes");
		}
		return new String(data);
	}

	private byte[] readBytes(String fieldName) throws IOException, MalformedProtobufException {
		final long length = readLengthFromStream(); // TODO If length > 2GB throw like mad
		final byte[] data = new byte[(int) length];
		final long read = protoStream.read(data, 0, (int) length);
		if (read != length) {
			throw new MalformedProtobufException("Truncated protobuf, missing at least " +
					(length - read) + " bytes");
		}
		return data;
	}

	/**
	 * Reads a variable length encoded integer from the protobuf byte stream.
	 *
	 * @param fieldName The name of the field being decoded. Used for error messages only.
	 * @param zigZag Whether to decode using zig-zag decoding
	 * @return the 64-bit integer read and decoded from the protobuf byte stream
	 * @throws IOException if the proto stream cannot be read
	 * @throws MalformedProtobufException if the stream cannot be decoded properly due to a malformed stream
	 */
	private long readVarint(String fieldName, boolean zigZag) throws IOException, MalformedProtobufException {
		// Protobuf encodes smaller integers with fewer bytes than larger integers. It takes a full byte
		// to encode 7 bits of information. So, if all 64 bits of a long are in use (for example, if the
		// leading bit is 1, or even all bits are 1) then it will take 10 bytes to transmit what would
		// have otherwise been 8 bytes of data!
		//
		// Thus, at most, reading a varint should involve reading 10 bytes of data.
		//
		// The leading bit of each byte is a continuation bit. If set, another byte will follow.
		// If we read 10 bytes in sequence with a continuation bit set, then we have a malformed
		// byte stream.
		// The bytes come least to most significant 7 bits. So the first byte we read represents
		// the lowest 7 bytes, then the next byte is the next highest 7 bytes, etc.

		// Keeps track of the number of bytes that have been read. If we read 10 in a row all with
		// the leading continuation bit set, then throw a malformed protobuf exception.
		int numBytesRead = 0;
		// The final value.
		long value = 0;
		// The amount to shift the bits we read by before AND with the value
		long shift = 0;
		// The byte to read from the stream
		int b;

		while((b = protoStream.read()) != -1) {
			// Keep track of the number of bytes read
			numBytesRead++;
			// Checks whether the continuation bit is set
			final boolean continuationBitSet = (b & VARINT_CONTINUATION_MASK) != 0;
			// Strip off the continuation bit by keeping only the data bits
			b &= VARINT_DATA_MASK;
			// Shift the data bits left into position to AND with the value
			final long toBeAdded = (long)b << shift;
			value |= toBeAdded;
			// Increment the shift for the next data bits (if there are more bits)
			shift += NUM_BITS_PER_VARINT_BYTE;

			if (continuationBitSet) {
				// msb is set, so there is another byte following this one. If we've just read our 10th byte,
				// then we have a malformed protobuf stream
				if (numBytesRead == 10) {
					throw new MalformedProtobufException(
							"Bad protobuf encoding, MSB set on last byte of varint for field '" + fieldName + "'!");
				}
			} else {
				break;
			}
		}

		// "ZigZag" mode basically maps a signed number into unsigned bits such that small signed and
		// unsigned numbers are small numbers. The most significant bit indicates the sign. All negative
		// numbers of a msb of 1, while all positive numbers are 0. When encoding, you would take a number
		// and multiply it by two and XOR it with all 1's if it is negative and all 0's if it is positive
		// (XOR ends up flipping the bits if the mask is all 1's and leaving them be if all 0's). Of course,
		// multiple by two means to shift left by 1. So the encoding looks like:
		//
		//   (value << 1) ^ (value >> 63)
		//
		// The first term simply multiplies the value by 2 while the second term is the mask. It does an
		// arithmetic shift -- basically if the msb is 1 then it will fill it with all 1's. If 0 then it
		// fills with all 0's.
		//
		// The operation to undo this is slightly more tricky. The first term (value >>> 1) will divide
		// the number, but uses the *logical shift right* operation so the msb will always be 0. Then
		// it creates the XOR flag of all 1's if the *lsb* is 1, otherwise with 0's. The trick of
		// using (value << 63 >> 63) basically left shifts until the lsb is in the msb spot with all 0's
		// following it, and then arithmetically shifts right the same number of places, ending up with
		// either -1 (all 1's) or 0 (all 0's).
		//
		// I could have put an "if" operation in there instead (if value & 0x1 == 0 use mask of all 0's
		// else all 1's), but unless the JVM optimizes it, it will be less efficient. There you go.
		if (zigZag) {
			value = (value >>> 1) ^ (value << 63 >> 63);
		}

		return value;
	}

	private void skipField(int wireType) throws IOException, MalformedProtobufException {
		switch (wireType) {
			case WIRE_TYPE_FIXED_64_BIT -> protoStream.skipNBytes(8);
			case WIRE_TYPE_FIXED_32_BIT -> protoStream.skipNBytes(4);
			// The value for "zigZag" when calling varint doesn't matter because we are just reading past
			// the varint, we don't care how to interpret it (zigzag is only used for interpretation of
			// the bytes, not how many of them there are)
			case WIRE_TYPE_VARINT_OR_ZIGZAG -> readVarint("Unknown", false);
			case WIRE_TYPE_DELIMITED -> {
				final var length = readLengthFromStream();
				protoStream.skipNBytes(length);
			}
			case WIRE_TYPE_GROUP_START -> throw new MalformedProtobufException(
					"Wire type 'Group Start' is unsupported");
			case WIRE_TYPE_GROUP_END -> throw new MalformedProtobufException(
					"Wire type 'Group End' is unsupported");
			default -> throw new MalformedProtobufException(
					"Unhandled wire type while trying to skip a field " + wireType);
		}
	}

	private int readIntFromStream() throws IOException, MalformedProtobufException {
		int b1 = protoStream.read();
		int b2 = protoStream.read();
		int b3 = protoStream.read();
		int b4 = protoStream.read();
		if ((b1 | b2 | b3 | b4) < 0) {
			throw new MalformedProtobufException("Unexpected end of stream while parsing protobuf int");
		}

		// The bytes in protobuf come in little-endian order -- backwards for Java.
		return ((b4 << 24) + (b3 << 16) + (b2 << 8) + b1);
	}

	private long readLongFromStream() throws IOException, MalformedProtobufException {
		int lengthRead = protoStream.read(readBuffer, 0, 8);
		if (lengthRead != 8) {
			throw new MalformedProtobufException("Unexpected end of stream while parsing protobuf int");
		}

		// The bytes in protobuf come in little-endian order -- backwards for Java.
		return (((long)readBuffer[7] << 56) +
				((long)(readBuffer[6] & 255) << 48) +
				((long)(readBuffer[5] & 255) << 40) +
				((long)(readBuffer[4] & 255) << 32) +
				((long)(readBuffer[3] & 255) << 24) +
				((readBuffer[2] & 255) << 16) +
				((readBuffer[1] & 255) <<  8) +
				((readBuffer[0] & 255)));
	}

	private long readLengthFromStream() throws IOException, MalformedProtobufException {
		return readVarint("", false);
	}

	private static final class ProtoStream extends InputStream {
		private InputStream stream;
		private int nextByte = -1;

		private void reset(InputStream stream) throws IOException {
			this.stream = stream;
			this.nextByte = stream.read();
		}

		public boolean hasNextByte() {
			return nextByte != -1;
		}

		public boolean eof() {
			return nextByte == -1;
		}

		@Override
		public int read() throws IOException {
			int value = nextByte;
			nextByte = stream.read(); // TODO Does this ever throw an exception?
			return value;
		}
	}

	private static final class ByteArrayInputStreamAdapter extends InputStream {
		private int position = 0;
		private byte[] data;

		private void reset(byte[] newData) {
			assert newData != null : "The only code that calls this ensures this is true";
			this.position = 0;
			this.data = newData;
		}

		@Override
		public int read() {
			assert data != null : "This must be true unless read was somehow called before reset!";
			return position >= data.length
					? -1
					: 0b0000_0000_0000_0000_0000_0000_1111_1111 & data[position++]; // no sign extending
		}
	}

	private static final class ByteBufferInputStreamAdapter extends InputStream {
		private ByteBuffer buffer;
		private int position = 0;
		private int length = 0;

		private void reset(ByteBuffer buffer) {
			// TODO What about resetting the buffer position? Who is responsible for that kind of safety?
			assert buffer != null : "The only code that calls this ensures this is true";
			this.buffer = buffer;
			this.position = 0;
			this.length = buffer.limit();
		}

		@Override
		public int read() throws IOException {
			assert buffer != null : "This must be true unless read was somehow called before reset!";
			return position >= length ? -1 : buffer.get(position++);
		}
	}
}
