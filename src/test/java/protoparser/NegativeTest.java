package protoparser;

public class NegativeTest {
	// Take a valid protobuf, and send 1 byte, then 2 bytes, and so forth until all bytes - 1. All calls
	// should fail, though they may fail in different ways.

	// There should also be a test that specifically send a varint of 10+ bytes in a row with the
	// continuation bit set.

	// There should be a test for forwards compatibility where valid protobuf is sent to a parser that
	// doesn't know about all the different types of fields.
}
