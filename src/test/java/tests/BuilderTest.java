package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sample.target.proto.builders.AppleBuilder;

import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BuilderTest {
    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "A String",
            "I need some HBAR to run work on Hedera!",
            "I need some ℏ to run work on Hedera!",
            """
                    To be, or not to be, that is the question:
                    Whether ’tis nobler in the mind to suffer
                    The slings and arrows of outrageous fortune,
                    Or to take arms against a sea of troubles
                    And by opposing end them. To die—to sleep,
                    No more; and by a sleep to say we end
                    The heart-ache and the thousand natural shocks
                    That flesh is heir to: ’tis a consummation
                    Devoutly to be wish’d. To die, to sleep;
                    To sleep, perchance to dream—ay, there’s the rub:
                    For in that sleep of death what dreams may come,
                    When we have shuffled off this mortal coil,
                    Must give us pause—there’s the respect
                    That makes calamity of so long life…"""
    })
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
