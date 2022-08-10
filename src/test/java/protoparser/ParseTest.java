package protoparser;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ParseTest {
	@Test
	void parseTimestamp() throws Exception {
		// 0000_1000, 1010_1110, 0010_1100, 0001_0000, 1101_0010, 0000_1001
		final var timestamp = TimestampParser.parse(new byte[] { 8, -82, 44, 16, -46, 9 });
		assertNotNull(timestamp);
		assertEquals(5678L, timestamp.seconds());
		assertEquals(1234, timestamp.nanos());
	}
}
