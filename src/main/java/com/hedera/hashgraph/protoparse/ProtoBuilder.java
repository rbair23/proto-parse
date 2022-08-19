package com.hedera.hashgraph.protoparse;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

import static com.hedera.hashgraph.protoparse.ProtoConstants.WIRE_TYPE_DELIMITED;

// A protobuf builder. How does it work? Nobody knows!
public abstract class ProtoBuilder {
    // I kind of want to use a normal builder pattern for this, but the protobuf bytes are
    // written out as you call the various "write" methods... so you can stream it out
    // instead of buffering it in RAM. However, I would also like subclasses that have
    // type specific data, so kind of the same pattern as ProtoParser. I think the only
    // tricky thing here is there needs to be a "reset" method if we want to reuse these
    // things.

    private ProtoBuffer buffer = new ProtoBuffer();

    public byte[] build() {
        return buffer.toByteArray();
    }

    protected void reset() {
        this.buffer = new ProtoBuffer();
    }

    protected void writeInt(FieldDefinition f, int value) {

    }

    protected void writeLong(FieldDefinition f, long value) {

    }

    protected void writeFloat(FieldDefinition f, float value) {

    }

    protected void writeDouble(FieldDefinition f, double value) {

    }

    protected void writeBoolean(FieldDefinition f, boolean value) {

    }

    protected void writeEnum(FieldDefinition f, Enum<?> value) {

    }

    protected void writeString(FieldDefinition f, String value) {
        // Intentionally throw NPE if value is null (or field)
        if (value.length() > 0) {
            final int tag = (f.number() << 3) | WIRE_TYPE_DELIMITED;
            buffer.writeByte(tag);
            final byte[] data = value.getBytes(StandardCharsets.UTF_8);
            buffer.writeVarint(data.length);
            for (int i = 0; i < data.length; i++) {
                buffer.writeByte(data[i]);
            }
        }
    }

    protected void writeBytes(FieldDefinition f, byte[] value) {

    }

    protected void writeMessage(FieldDefinition f, Object value) {

    }

    /**
     * As though it were a DataOutputStream. Call it ProtoOutputStream or ProtobufOutputStream.
     * Do we use FieldDefinition instead of fieldNumber and do validation on the schema at write time?
     *
     *  - private writeVarint(long, boolean zigZag) // zig zag support is missing
     *  // You will need these 4 because protobuf is little endian :-)
     *  - private writeLong(long)
     *  - private writeInteger(int)
     *  - private writeFloat(float)
     *  - private writeDouble(double)
     *
     *  - writeInt32(fieldNumber, int)
     *  - writeSint32(fieldNumber, int) // uses zig zag, also the 64-bit version
     *  - ...
     *  - writeInt32List(fieldNumber, List of ints or whatever)
     *  - writeStringList(fieldNumber, List of ints or whatever)
     *  - ...
     *  - Hmmm. For one-of, what happens if the writer writes more than one item in the one-of? I assume the parser has to deal with it. Do we?
     *  - writeBytes(fieldNumber, byte[]) // could have ByteBuffer and InputStream variants if you wanted to
     *  - writeEnum(fieldNumber, int ordinal)
     *  - writeMessage(fieldNumber, ProtoBuilder)
     *  - writeMessage(fieldNumber, byte[]) // could have ByteBuffer and InputStream variants if you wanted to
     */
    private static final class ProtoBuffer {
        private LinkedList<byte[]> arrays = new LinkedList<>();
        private byte[] currentBuffer;
        private int offset;
        private int totalBytes;

        public ProtoBuffer() {
            this.currentBuffer = new byte[4096];
        }

        private void writeByte(int value) {
            currentBuffer[offset++] = (byte) value;
            totalBytes++;
            if (offset == currentBuffer.length) {
                arrays.add(currentBuffer);
                offset = 0;
                currentBuffer = new byte[4096];
            }
        }

        private void writeVarint(long value) {
            // We never write a varint with length 0.
            // TODO This needs to be validated.
            if (value == 0) {
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
                writeByte((int) (0x80 | (0x7F & value)));
                value >>= 7;
            }

            // And now we can send whatever is left as the last byte, knowing that
            // the high order bit will never be set.
            writeByte((int) value);
        }

        private byte[] toByteArray() {
            final var results = new byte[totalBytes];
            int dst = 0;
            final var itr = arrays.iterator();
            while (itr.hasNext()) {
                final var arr = itr.next();
                System.arraycopy(arr, 0, results, dst, arr.length);
                dst += arr.length;
            }
            if (offset > 0) {
                System.arraycopy(currentBuffer, 0, results, dst, offset);
            }
            return results;
        }
    }
}
