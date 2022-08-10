package com.hedera.hashgraph.protoparse;

// Use this to hold a reference to the underlying unparsed entity, and then parse it later.
public class OneOf<T> {
	private byte[] bytes;
	private int fieldNum;
	private T value;

	OneOf(byte[] bytes, int fieldNum) {
		this.bytes = bytes;
		this.fieldNum = fieldNum;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public int getFieldNum() {
		return fieldNum;
	}

	// TODO Can only be called after it has been parsed and set. Kinda weird.
	public T getValue() {
		return value;
	}
}
