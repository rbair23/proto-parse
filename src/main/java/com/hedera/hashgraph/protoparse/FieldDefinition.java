package com.hedera.hashgraph.protoparse;

// What about enum? What about oneof? What about optionals?
public record FieldDefinition(String name, FieldType type, boolean repeated, int number) {
}
