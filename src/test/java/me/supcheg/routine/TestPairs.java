package me.supcheg.routine;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.mockito.Mockito.mock;

final class TestPairs {
    @SuppressWarnings("unchecked")
    static Consumer<String> consumerMock() {
        return (Consumer<String>) mock(Consumer.class);
    }

    @SuppressWarnings("unchecked")
    static BiConsumer<String, String> biConsumerMock() {
        return (BiConsumer<String, String>) mock(BiConsumer.class);
    }
}
