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
            // TODO Need to have an implementation of varint production... booo.
            final byte[] data = value.getBytes(StandardCharsets.UTF_8);
            buffer.writeByte(data.length);
            for (int i = 0; i < data.length; i++) {
                buffer.writeByte(data[i]);
            }
        }
    }

    protected void writeBytes(FieldDefinition f, byte[] value) {

    }

    protected void writeMessage(FieldDefinition f, Object value) {

    }

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
