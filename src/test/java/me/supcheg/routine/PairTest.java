package me.supcheg.routine;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static me.supcheg.routine.Pair.pair;
import static me.supcheg.routine.Pair.pairFromEntry;
import static org.assertj.core.api.Assertions.assertThat;

class PairTest {

    static final String LEFT = "_left";
    static final String RIGHT = "_right";
    static final String MAP = "_map";
    static final String WITH = "_with";

    @Test
    void map() {
        assertThat(pair(LEFT, RIGHT).map(left -> left + MAP + LEFT, right -> right + MAP + RIGHT))
                .isEqualTo(pair(LEFT + MAP + LEFT, RIGHT + MAP + RIGHT));
        assertThat(pair(LEFT, RIGHT).mapLeft(left -> left + MAP + LEFT)).isEqualTo(pair(LEFT + MAP + LEFT, RIGHT));
        assertThat(pair(LEFT, RIGHT).mapRight(right -> right + MAP + RIGHT)).isEqualTo(pair(LEFT, RIGHT + MAP + RIGHT));
    }

    @Test
    void with() {
        assertThat(pair(LEFT, RIGHT).withLeft(LEFT + WITH)).isEqualTo(pair(LEFT + WITH, RIGHT));
        assertThat(pair(LEFT, RIGHT).withRight(RIGHT + WITH)).isEqualTo(pair(LEFT, RIGHT + WITH));
    }

    @Test
    void flip() {
        assertThat(pair(LEFT, RIGHT).flip()).isEqualTo(pair(RIGHT, LEFT));
    }

    @Test
    void entry() {
        var entry = Map.entry(LEFT, RIGHT);
        var pair = pair(LEFT, RIGHT);
        assertThat(pairFromEntry(entry)).isEqualTo(pair);
        assertThat(pair.asEntry()).isEqualTo(entry);
    }
}
