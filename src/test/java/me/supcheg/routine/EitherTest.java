package me.supcheg.routine;

import org.junit.jupiter.api.Test;

import static me.supcheg.routine.TestEithers.LEFT;
import static me.supcheg.routine.TestEithers.RIGHT;
import static me.supcheg.routine.TestEithers.consumerMock;
import static me.supcheg.routine.TestEithers.left;
import static me.supcheg.routine.TestEithers.right;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

class EitherTest {

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
        assertThat(right(RIGHT).flatMapRight(r -> left(r + FLAT_MAP + RIGHT)))
                .isEqualTo(left(RIGHT + FLAT_MAP + RIGHT));
    }

    @Test
    void flip() {
        assertThat(left(LEFT).flip()).isEqualTo(right(LEFT));
        assertThat(right(RIGHT).flip()).isEqualTo(left(RIGHT));
    }

    @Test
    void fold() {
        assertThat((String) left(LEFT).fold(l -> l + FOLD + LEFT, r -> r + FOLD + RIGHT))
                .isEqualTo(LEFT + FOLD + LEFT);
        assertThat((String) right(RIGHT).fold(l -> l + FOLD + LEFT, r -> r + FOLD + RIGHT))
                .isEqualTo(RIGHT + FOLD + RIGHT);
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

        var left = left(LEFT);
        assertThat(left.peek(leftPeek, rightPeek)).isSameAs(left);
        verify(leftPeek, only()).accept(LEFT);
        verify(rightPeek, never()).accept(any());
    }

    @Test
    void rightPeek() {
        var leftPeek = consumerMock();
        var rightPeek = consumerMock();

        var right = right(RIGHT);
        assertThat(right.peek(leftPeek, rightPeek)).isSameAs(right);
        verify(leftPeek, never()).accept(any());
        verify(rightPeek, only()).accept(RIGHT);
    }

    @Test
    void leftPeekLeft() {
        var peek = consumerMock();

        var left = left(LEFT);
        assertThat(left.peekLeft(peek)).isSameAs(left);
        verify(peek, only()).accept(LEFT);
    }

    @Test
    void rightPeekLeft() {
        var peek = consumerMock();

        var right = right(RIGHT);
        assertThat(right.peekLeft(peek)).isSameAs(right);
        verify(peek, never()).accept(any());
    }

    @Test
    void leftPeekRight() {
        var peek = consumerMock();

        var left = left(LEFT);
        assertThat(left.peekRight(peek)).isSameAs(left);
        verify(peek, never()).accept(any());
    }

    @Test
    void rightPeekRight() {
        var peek = consumerMock();

        var right = right(RIGHT);
        assertThat(right.peekRight(peek)).isSameAs(right);
        verify(peek, only()).accept(RIGHT);
    }

    @Test
    void leftIfLeft() {
        var ifLeft = consumerMock();
        left(LEFT).ifLeft(ifLeft);
        verify(ifLeft, only()).accept(LEFT);
    }

    @Test
    void rightIfLeft() {
        var ifLeft = consumerMock();
        right(LEFT).ifLeft(ifLeft);
        verify(ifLeft, never()).accept(any());
    }

    @Test
    void leftIfRight() {
        var ifRight = consumerMock();
        left(LEFT).ifRight(ifRight);
        verify(ifRight, never()).accept(any());
    }

    @Test
    void rightIfRight() {
        var ifRight = consumerMock();
        right(RIGHT).ifRight(ifRight);
        verify(ifRight, only()).accept(RIGHT);
    }
}
