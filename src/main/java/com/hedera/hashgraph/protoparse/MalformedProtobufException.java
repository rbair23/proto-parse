package com.hedera.hashgraph.protoparse;

public class MalformedProtobufException extends Exception {
	public MalformedProtobufException(final String message) {
		super(message);
	}

	public MalformedProtobufException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
