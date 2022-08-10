package com.hedera.hashgraph.protoparse;

import java.util.List;

public interface ParseListener {
	default void intField(int fieldNum, int value) {

	}

	default void longField(int fieldNum, long value) {

	}

	default void booleanField(int fieldNum, boolean value) {

	}

	default void enumField(int fieldNum, Object enumValue) {

	}

	default void floatField(int fieldNum, float value) {

	}

	default void doubleField(int fieldNum, double value) {

	}

	default void stringField(int fieldNum, String value) {

	}

	default void byteField(int fieldNum, byte[] value) {

	}

	default void intList(int fieldNum, List<Integer> value) {

	}

	default void longList(int fieldNum, List<Long> value) {

	}

	default void booleanList(int fieldNum, List<Boolean> value) {

	}

	default void floatList(int fieldNum, List<Float> value) {

	}

	default void doubleList(int fieldNum, List<Double> value) {

	}

	default void stringList(int fieldNum, List<String> value) {

	}

	default void bytesList(int fieldNum, List<byte[]> value) {

	}

	default void objectList(int fieldNum, List<Object> value) {

	}
}