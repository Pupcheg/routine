package me.supcheg.routine;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public sealed interface Either<L, R> {

    record Left<L, R>(L value) implements Either<L, R> {

        public Left {
            Objects.requireNonNull(value, "value");
        }

        @SuppressWarnings("unchecked")
        public <NR> Left<L, NR> castRight() {
            return (Left<L, NR>) this;
        }
    }

    record Right<L, R>(R value) implements Either<L, R> {

        public Right {
            Objects.requireNonNull(value, "value");
        }

        @SuppressWarnings("unchecked")
        public <NL> Right<NL, R> castLeft() {
            return (Right<NL, R>) this;
        }
    }

    static <L, R> Left<L, R> left(L value) {
        return new Left<>(value);
    }

    static <L, R> Right<L, R> right(R value) {
        return new Right<>(value);
    }

    default <NL, NR> Either<NL, NR> map(
            Function<? super L, ? extends NL> left, Function<? super R, ? extends NR> right) {
        return switch (this) {
            case Left(var value) -> left(left.apply(value));
            case Right(var value) -> right(right.apply(value));
        };
    }

    default <NL> Either<NL, ? extends R> mapLeft(Function<? super L, ? extends NL> left) {
        return switch (this) {
            case Left(var value) -> left(left.apply(value));
            case Right<L, R> right -> right.castLeft();
        };
    }

    default <NR> Either<L, NR> mapRight(Function<? super R, ? extends NR> right) {
        return switch (this) {
            case Left<L, R> left -> left.castRight();
            case Right(var value) -> right(right.apply(value));
        };
    }

    default <NL, NR> Either<NL, NR> flatMap(
            Function<? super L, Either<NL, NR>> left, Function<? super R, Either<NL, NR>> right) {
        return switch (this) {
            case Left(var value) -> left.apply(value);
            case Right(var value) -> right.apply(value);
        };
    }

    default <NL> Either<NL, R> flatMapLeft(Function<? super L, Either<NL, R>> left) {
        return switch (this) {
            case Left(var value) -> left.apply(value);
            case Right<L, R> right -> right.castLeft();
        };
    }

    default <NR> Either<L, NR> flatMapRight(Function<? super R, Either<L, NR>> right) {
        return switch (this) {
            case Left<L, R> left -> left.castRight();
            case Right(var value) -> right.apply(value);
        };
    }

    default Either<R, L> flip() {
        return flatMap(Either::right, Either::left);
    }

    default <T> T fold(Function<? super L, ? extends T> left, Function<? super R, ? extends T> right) {
        return switch (this) {
            case Left(var value) -> left.apply(value);
            case Right(var value) -> right.apply(value);
        };
    }

    default Optional<L> left() {
        return fold(Optional::of, _ -> Optional.empty());
    }

    default Optional<R> right() {
        return fold(_ -> Optional.empty(), Optional::of);
    }

    default Either<L, R> peek(Consumer<? super L> left, Consumer<? super R> right) {
        switch (this) {
            case Left(var value) -> left.accept(value);
            case Right(var value) -> right.accept(value);
        }
        return this;
    }

    default Either<L, R> peekLeft(Consumer<? super L> left) {
        ifLeft(left);
        return this;
    }

    default Either<L, R> peekRight(Consumer<? super R> right) {
        ifRight(right);
        return this;
    }

    default void accept(Consumer<? super L> left, Consumer<? super R> right) {
        switch (this) {
            case Left(var value) -> left.accept(value);
            case Right(var value) -> right.accept(value);
        }
    }

    default void ifLeft(Consumer<? super L> left) {
        if (this instanceof Left(var value)) {
            left.accept(value);
        }
    }

    default void ifRight(Consumer<? super R> right) {
        if (this instanceof Right(var value)) {
            right.accept(value);
        }
    }
}
