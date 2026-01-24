package me.supcheg.routine;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static me.supcheg.routine.TestEithers.LEFT;
import static me.supcheg.routine.TestEithers.RIGHT;
import static me.supcheg.routine.TestEithers.left;
import static me.supcheg.routine.TestEithers.right;
import static org.assertj.core.api.Assertions.assertThat;

class EitherStreamTest {
    @Test
    void mapMultiIfLeft() {
        assertThat(Stream.of(left(LEFT), right(RIGHT)).<String>mapMulti(Either::ifLeft))
                .singleElement()
                .isEqualTo(LEFT);
    }

    @Test
    void mapMultiIfRight() {
        assertThat(Stream.of(left(LEFT), right(RIGHT)).<String>mapMulti(Either::ifRight))
                .singleElement()
                .isEqualTo(RIGHT);
    }
}
