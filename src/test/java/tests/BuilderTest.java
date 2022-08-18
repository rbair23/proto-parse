package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sample.target.proto.builders.AppleBuilder;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class BuilderTest {
    @ParameterizedTest
    @ValueSource(strings = {"", "A String", "I need some HBAR to run work on Hedera!", "I need some ℏ to run work on Hedera!"})
    void buildStringOnly(String val) throws Exception {
        final var protobuf = test.proto.Omnibus.newBuilder()
                .setMemo(val)
                .build()
                .toByteArray();

        final var protobuf2 = new AppleBuilder()
                .variety(val)
                .build();

        assertArrayEquals(protobuf, protobuf2);
    }

    @Test
    void buildNullStringThrows() throws Exception {
        final var out = new ByteArrayOutputStream();
        final var builder = new AppleBuilder();
        assertThrows(NullPointerException.class, () -> builder.variety(null));
    }
}
