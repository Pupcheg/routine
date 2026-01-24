package me.supcheg.routine;

import java.util.function.Consumer;

import static org.mockito.Mockito.mock;

final class TestEithers {

    static final String LEFT = "_left";
    static final String RIGHT = "_right";

    static Either.Left<String, String> left(String value) {
        return Either.left(value);
    }

    static Either.Right<String, String> right(String value) {
        return Either.right(value);
    }

    @SuppressWarnings("unchecked")
    static Consumer<String> consumerMock() {
        return (Consumer<String>) mock(Consumer.class);
    }
}
