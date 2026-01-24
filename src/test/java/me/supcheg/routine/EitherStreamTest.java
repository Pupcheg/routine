package me.supcheg.routine;

import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static java.util.stream.Collector.Characteristics.IDENTITY_FINISH;
import static java.util.stream.Collector.Characteristics.UNORDERED;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.Collectors.toUnmodifiableSet;
import static me.supcheg.routine.EitherCollectors.toCollectionsPair;
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

    @Test
    void toListsPairs() {
        int perTypeAmount = 10;

        var result = Stream.concat(
                        Stream.generate(() -> left(LEFT)).limit(perTypeAmount),
                        Stream.generate(() -> right(RIGHT)).limit(perTypeAmount))
                .collect(toCollectionsPair(toList(), toList()));

        assertThat(result.left()).hasSize(perTypeAmount).allMatch(LEFT::equals);
        assertThat(result.right()).hasSize(perTypeAmount).allMatch(RIGHT::equals);
    }

    @Test
    void parallelToListsPairs() {
        int perTypeAmount = 10;

        var result = Stream.concat(
                        Stream.generate(() -> left(LEFT)).limit(perTypeAmount),
                        Stream.generate(() -> right(RIGHT)).limit(perTypeAmount))
                .parallel()
                .collect(toCollectionsPair(toList(), toList()));

        assertThat(result.left()).hasSize(perTypeAmount).allMatch(LEFT::equals);
        assertThat(result.right()).hasSize(perTypeAmount).allMatch(RIGHT::equals);
    }

    @Test
    void characteristics() {
        assertThat(toCollectionsPair(toSet(), toSet()).characteristics())
                .containsExactlyInAnyOrder(IDENTITY_FINISH, UNORDERED);
        assertThat(toCollectionsPair(toUnmodifiableSet(), toSet()).characteristics())
                .containsExactlyInAnyOrder(UNORDERED);
        assertThat(toCollectionsPair(toUnmodifiableSet(), toUnmodifiableSet()).characteristics())
                .containsExactlyInAnyOrder(UNORDERED);
    }
}
