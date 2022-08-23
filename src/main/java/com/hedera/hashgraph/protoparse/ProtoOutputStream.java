package com.hedera.hashgraph.protoparse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

import static com.hedera.hashgraph.protoparse.ProtoConstants.*;

/**
 *  - Hmmm. For one-of, what happens if the writer writes more than one item in the one-of? I assume the parser has to deal with it. Do we?
 *  - writeBytes(fieldNumber, byte[]) // could have ByteBuffer and InputStream variants if you wanted to
 *  - writeEnum(fieldNumber, int ordinal)
 *  - writeMessage(fieldNumber, ProtoBuilder)
 *  - writeMessage(fieldNumber, byte[]) // could have ByteBuffer and InputStream variants if you wanted to
 */
public class ProtoOutputStream {
    private final OutputStream out;
    // When we have a schema interface it can have the goodies on it...
//    private final Schema schema;

    public ProtoOutputStream(OutputStream out) {
        this.out = Objects.requireNonNull(out);
    }

    public void writeInteger(FieldDefinition field, int value) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (value == 0) {
            return;
        }

        switch (field.type()) {
            case INT_32 -> {
                writeTag(field, WIRE_TYPE_VARINT_OR_ZIGZAG);
                writeVarint(value, false);
            }
            case UINT_32 -> {
                writeTag(field, WIRE_TYPE_VARINT_OR_ZIGZAG);
                writeVarint(Integer.toUnsignedLong(value), false);
            }
            case SINT_32 -> {
                writeTag(field, WIRE_TYPE_VARINT_OR_ZIGZAG);
                writeVarint(value, true);
            }
            case SFIXED_32, FIXED_32 -> {
                // The bytes in protobuf are in little-endian order -- backwards for Java.
                // Smallest byte first.
                writeTag(field, WIRE_TYPE_FIXED_32_BIT);
                writeIntToStream(value);
            }
            default ->
                throw new RuntimeException(
                        "Unsupported field type for integer. Bug in ProtoOutputStream, shouldn't happen.");
        }
    }

    public void writeLong(FieldDefinition field, long value) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (value == 0) {
            return;
        }

        switch (field.type()) {
            case INT_64, UINT_64 -> {
                writeTag(field, WIRE_TYPE_VARINT_OR_ZIGZAG);
                writeVarint(value, false);
            }
            case SINT_64 -> {
                writeTag(field, WIRE_TYPE_VARINT_OR_ZIGZAG);
                writeVarint(value, true);
            }
            case SFIXED_64, FIXED_64 -> {
                // The bytes in protobuf are in little-endian order -- backwards for Java.
                // Smallest byte first.
                writeTag(field, WIRE_TYPE_FIXED_64_BIT);
                writeLongToStream(value);
            }
            default ->
                    throw new RuntimeException(
                            "Unsupported field type for long. Bug in ProtoOutputStream, shouldn't happen.");
        }
    }

    private void writeIntToStream(int value) throws IOException {
        out.write(value & 0x000000FF);
        out.write((value & 0x0000FF00) >> 8);
        out.write((value & 0x00FF0000) >> 16);
        out.write((value & 0xFF000000) >> 24);
    }

    private void writeLongToStream(long value) throws IOException {
        out.write((int) (value & 0x00000000000000FF));
        out.write((int) ((value & 0x000000000000FF00) >> 8));
        out.write((int) ((value & 0x0000000000FF0000) >> 16));
        out.write((int) ((value & 0x00000000FF000000) >> 24));
        out.write((int) ((value & 0x000000FF00000000L) >> 32));
        out.write((int) ((value & 0x0000FF0000000000L) >> 40));
        out.write((int) ((value & 0x00FF000000000000L) >> 48));
        out.write((int) ((value & 0xFF00000000000000L) >> 56));
    }

    public void writeFloat(FieldDefinition field, float value) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (value == 0) {
            return;
        }
        writeTag(field, WIRE_TYPE_FIXED_32_BIT);
        writeIntToStream(Float.floatToRawIntBits(value)); // todo should I use the non-raw variant?
    }

    public void writeDouble(FieldDefinition field, double value) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (value == 0) {
            return;
        }
        writeTag(field, WIRE_TYPE_FIXED_64_BIT);
        writeLongToStream(Double.doubleToRawLongBits(value)); // todo should I use the non-raw variant?
    }

    public void writeBoolean(FieldDefinition field, boolean value) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (value) {
            writeTag(field, WIRE_TYPE_VARINT_OR_ZIGZAG);
            out.write(1);
        }
    }

    public void writeEnum(FieldDefinition field, int ordinal) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (ordinal == 0) {
            return;
        }

        writeTag(field, WIRE_TYPE_VARINT_OR_ZIGZAG);
        writeVarint(ordinal, false);
    }

    public void writeString(FieldDefinition field, String value) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (value == null || value.isBlank()) {
            return;
        }

        writeTag(field, WIRE_TYPE_DELIMITED);
        final var bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarint(bytes.length, false);
        out.write(bytes);
    }

    public void writeBytes(FieldDefinition field, byte[] value) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (value.length == 0) {
            return;
        }
        writeTag(field, WIRE_TYPE_DELIMITED);
        writeVarint(value.length, false);
        out.write(value);
    }

    public <T> void writeMessage(FieldDefinition field, T message, ProtoWriter<T> writer) throws IOException {
        if (message != null) {
            writeTag(field, WIRE_TYPE_DELIMITED);
            final var baos = new ByteArrayOutputStream();
            writer.write(message, baos);
            writeVarint(baos.size(), false);
            if (baos.size() > 0) {
                out.write(baos.toByteArray());
            }
        }
    }

    public void writeIntegerList(FieldDefinition field, List<Integer> list) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (list.isEmpty()) {
            return;
        }

        final var buffer = new ByteArrayOutputStream();
        switch (field.type()) {
            case INT_32 -> {
                writeTag(field, WIRE_TYPE_DELIMITED);
                for (final int i : list) {
                    writeVarint(i, false, buffer);
                }
                writeVarint(buffer.size(), false);
                out.write(buffer.toByteArray());
            }
            case UINT_32 -> {
                writeTag(field, WIRE_TYPE_DELIMITED);
                for (final int i : list) {
                    writeVarint(Integer.toUnsignedLong(i), false, buffer);
                }
                writeVarint(buffer.size(), false);
                out.write(buffer.toByteArray());
            }
            case SINT_32 -> {
                writeTag(field, WIRE_TYPE_DELIMITED);
                for (final int i : list) {
                    writeVarint(i, true, buffer);
                }
                writeVarint(buffer.size(), false);
                out.write(buffer.toByteArray());
            }
            case SFIXED_32, FIXED_32 -> {
                // The bytes in protobuf are in little-endian order -- backwards for Java.
                // Smallest byte first.
                writeTag(field, WIRE_TYPE_DELIMITED);
                writeVarint(list.size() * 4L, false, out);
                for (final int i : list) {
                    writeIntToStream(i);
                }
            }
            default ->
                    throw new RuntimeException(
                            "Unsupported field type for integer. Bug in ProtoOutputStream, shouldn't happen.");
        }
    }

    public void writeLongList(FieldDefinition field, List<Long> list) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (list.isEmpty()) {
            return;
        }

        final var buffer = new ByteArrayOutputStream();
        switch (field.type()) {
            case INT_64, UINT_64 -> {
                writeTag(field, WIRE_TYPE_DELIMITED);
                for (final long i : list) {
                    writeVarint(i, false, buffer);
                }
                writeVarint(buffer.size(), false);
                out.write(buffer.toByteArray());
            }
            case SINT_64 -> {
                writeTag(field, WIRE_TYPE_DELIMITED);
                for (final long i : list) {
                    writeVarint(i, true, buffer);
                }
                writeVarint(buffer.size(), false);
                out.write(buffer.toByteArray());
            }
            case SFIXED_64, FIXED_64 -> {
                // The bytes in protobuf are in little-endian order -- backwards for Java.
                // Smallest byte first.
                writeTag(field, WIRE_TYPE_DELIMITED);
                writeVarint(list.size() * 8L, false, out);
                for (final long i : list) {
                    writeLongToStream(i);
                }
            }
            default ->
                    throw new RuntimeException(
                            "Unsupported field type for integer. Bug in ProtoOutputStream, shouldn't happen.");
        }
    }

    public void writeBooleanList(FieldDefinition field, List<Boolean> list) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (list.isEmpty()) {
            return;
        }

        final var buffer = new ByteArrayOutputStream();
        writeTag(field, WIRE_TYPE_DELIMITED);
        for (final boolean b : list) {
            writeVarint(b ? 1 : 0, false, buffer);
        }
        writeVarint(buffer.size(), false);
        out.write(buffer.toByteArray());
    }

    public void writeEnumList(FieldDefinition field, List<Integer> list) throws IOException {
        // TODO Call Schema to validate the field (do a couple of asserts)
        if (list.isEmpty()) {
            return;
        }

        final var buffer = new ByteArrayOutputStream();
        writeTag(field, WIRE_TYPE_DELIMITED);
        for (final int ordinal : list) {
            writeVarint(ordinal, false, buffer);
        }
        writeVarint(buffer.size(), false);
        out.write(buffer.toByteArray());
    }

    public void writeStringList(FieldDefinition field, List<String> list) throws IOException {
        if (list.isEmpty()) {
            return;
        }

        for (final String value : list) {
            writeString(field, value);
        }
    }

    public <T> void writeMessageList(FieldDefinition field, List<T> list, ProtoWriter<T> writer) throws IOException {
        if (list.isEmpty()) {
            return;
        }

        for (final T value : list) {
            writeMessage(field, value, writer);
        }
    }

    public void writeBytesList(FieldDefinition field, List<byte[]> list) throws IOException {
        if (list.isEmpty()) {
            return;
        }

        for (final byte[] value : list) {
            writeBytes(field, value);
        }
    }

    private void writeTag(FieldDefinition field, int wireType) throws IOException {
        final int tag = (field.number() << 3) | wireType;
        writeVarint(tag, false);
    }

    private void writeVarint(long value, boolean zigZag) throws IOException {
        writeVarint(value, zigZag, out);
    }

    private void writeVarint(long value, boolean zigZag, OutputStream stream) throws IOException {
        if (zigZag) {
            value = (value << 1) ^ (value >> 63);
        }

        // Small performance optimization for small values.
        if (value < 128 && value >= 0) {
            stream.write((int) value);
            return;
        }

        // We will send 7 bits of data with each byte we write. The high-order bit of
        // each byte indicates whether there are subsequent bytes coming. So we need
        // to know how many bytes we need to send. We do this by figuring out the position
        // of the highest set bit (counting from the left. So the first bit on the far
        // right is at position 1, the bit at the far left is at position 64).
        // Then, we calculate how many bytes it will take if we are sending 7 bits at
        // a time, being careful to round up when not aligned at a 7-bit boundary.
        int numLeadingZeros = Long.numberOfLeadingZeros(value);
        int bitPos = 64 - numLeadingZeros;
        int numBytesToSend = bitPos / 7 + (bitPos % 7 == 0 ? 0 : 1);

        // For all bytes except the last one, we need to mask off the last
        // 7 bits of the value and combine that with a byte with the leading
        // bit set. Then we shift the value 7 bits to the right.
        for (int i = 0; i < numBytesToSend - 1; i++) {
            stream.write((int) (0x80 | (0x7F & value)));
            value >>>= 7;
        }

        // And now we can send whatever is left as the last byte, knowing that
        // the high order bit will never be set.
        stream.write((int) value);
    }
}
