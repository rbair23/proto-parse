package com.hedera.hashgraph.protoparse;

// Use this to hold a reference to the underlying unparsed entity, and then parse it later.
public record OneOf<E, T>(int fieldNum, E kind, T value) {
    public <V> V as() {
        //noinspection unchecked
        return (V) value;
    }
}
