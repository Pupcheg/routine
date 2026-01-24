package me.supcheg.routine;

import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

class EitherTest {

    static final String LEFT = "_left";
    static final String RIGHT = "_right";

    static final String MAP = "_map";
    static final String FOLD = "_fold";
    static final String FLAT_MAP = "_flat_map";

    @Test
    void map() {
        assertThat(left(LEFT).map(l -> l + MAP + LEFT, r -> r + MAP + RIGHT)).isEqualTo(left(LEFT + MAP + LEFT));
        assertThat(right(RIGHT).map(l -> l + MAP + LEFT, r -> r + MAP + RIGHT)).isEqualTo(right(RIGHT + MAP + RIGHT));
    }

    @Test
    void mapLeft() {
        assertThat(left(LEFT).mapLeft(l -> l + MAP)).isEqualTo(left(LEFT + MAP));
        assertThat(right(RIGHT).mapLeft(l -> l + MAP)).isEqualTo(right(RIGHT));
    }

    @Test
    void mapRight() {
        assertThat(left(LEFT).mapRight(r -> r + MAP)).isEqualTo(left(LEFT));
        assertThat(right(RIGHT).mapRight(r -> r + MAP)).isEqualTo(right(RIGHT + MAP));
    }

    @Test
    void flatMap() {
        assertThat(left(LEFT).flatMap(l -> right(l + FLAT_MAP + LEFT), r -> left(r + FLAT_MAP + RIGHT)))
                .isEqualTo(right(LEFT + FLAT_MAP + LEFT));
        assertThat(right(RIGHT).flatMap(l -> right(l + FLAT_MAP + LEFT), r -> left(r + FLAT_MAP + RIGHT)))
                .isEqualTo(left(RIGHT + FLAT_MAP + RIGHT));
    }

    @Test
    void flatMapLeft() {
        assertThat(left(LEFT).flatMapLeft(l -> right(l + FLAT_MAP + LEFT))).isEqualTo(right(LEFT + FLAT_MAP + LEFT));
        assertThat(right(RIGHT).flatMapLeft(l -> right(l + FLAT_MAP + LEFT))).isEqualTo(right(RIGHT));
    }

    @Test
    void flatMapRight() {
        assertThat(left(LEFT).flatMapRight(r -> left(r + FLAT_MAP + RIGHT))).isEqualTo(left(LEFT));
        assertThat(right(RIGHT).flatMapRight(r -> left(r + FLAT_MAP + RIGHT))).isEqualTo(left(RIGHT + FLAT_MAP + RIGHT));
    }

    @Test
    void flip() {
        assertThat(left(LEFT).flip()).isEqualTo(right(LEFT));
        assertThat(right(RIGHT).flip()).isEqualTo(left(RIGHT));
    }

    @Test
    void fold() {
        assertThat((String) left(LEFT).fold(l -> l + FOLD + LEFT, r -> r + FOLD + RIGHT)).isEqualTo(LEFT + FOLD + LEFT);
        assertThat((String) right(RIGHT).fold(l -> l + FOLD + LEFT, r -> r + FOLD + RIGHT)).isEqualTo(RIGHT + FOLD + RIGHT);
    }

    @Test
    void optionalLeft() {
        assertThat(left(LEFT).left()).contains(LEFT);
        assertThat(right(RIGHT).left()).isEmpty();
    }

    @Test
    void optionalRight() {
        assertThat(left(LEFT).right()).isEmpty();
        assertThat(right(RIGHT).right()).contains(RIGHT);
    }

    @Test
    void leftPeek() {
        var leftPeek = consumerMock();
        var rightPeek = consumerMock();

        assertThat(left(LEFT).peek(leftPeek, rightPeek)).isEqualTo(left(LEFT));
        verify(leftPeek, only()).accept(any());
        verify(rightPeek, never()).accept(any());
    }

    @Test
    void rightPeek() {
        var leftPeek = consumerMock();
        var rightPeek = consumerMock();

        assertThat(right(RIGHT).peek(leftPeek, rightPeek)).isEqualTo(right(RIGHT));
        verify(leftPeek, never()).accept(any());
        verify(rightPeek, only()).accept(any());
    }

    @Test
    void leftPeekLeft() {
        var peek = consumerMock();

        assertThat(left(LEFT).peekLeft(peek)).isEqualTo(left(LEFT));
        verify(peek, only()).accept(any());
    }

    @Test
    void rightPeekLeft() {
        var peek = consumerMock();

        assertThat(right(RIGHT).peekLeft(peek)).isEqualTo(right(RIGHT));
        verify(peek, never()).accept(any());
    }

    @Test
    void leftPeekRight() {
        var peek = consumerMock();

        assertThat(left(LEFT).peekRight(peek)).isEqualTo(left(LEFT));
        verify(peek, never()).accept(any());
    }

    @Test
    void rightPeekRight() {
        var peek = consumerMock();

        assertThat(right(RIGHT).peekRight(peek)).isEqualTo(right(RIGHT));
        verify(peek, only()).accept(any());
    }

    @SuppressWarnings("unchecked")
    private static Consumer<String> consumerMock() {
        return (Consumer<String>) mock(Consumer.class);
    }

    private static Either.Left<String, String> left(String value) {
        return Either.left(value);
    }

    private static Either.Right<String, String> right(String value) {
        return Either.right(value);
    }
}
