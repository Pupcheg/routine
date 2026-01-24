package me.supcheg.routine;

import java.util.Optional;
import java.util.function.Function;

public sealed interface Either<L, R> {

    record Left<L, R>(L value) implements Either<L, R> {}
    record Right<L, R>(R value) implements Either<L, R> {}

    static <L, R> Left<L, R> left(L value) {
        return new Left<>(value);
    }

    static <L, R> Right<L, R> right(R value) {
        return new Right<>(value);
    }

    default <NL, NR> Either<NL, NR> map(Function<? super L, ? extends NL> left,
                                                            Function<? super R, ? extends NR> right) {
        return flatMap(value -> left(left.apply(value)), value -> right(right.apply(value)));
    }

    default <NL> Either<NL, ? extends R> mapLeft(Function<? super L, ? extends NL> left) {
        return map(left, Function.identity());
    }

    default <NR> Either<L, NR> mapRight(Function<? super R, ? extends NR> right) {
        return map(Function.identity(), right);
    }

    default <NL, NR> Either<NL, NR> flatMap(Function<? super L, Either<NL, NR>> left,
                                            Function<? super R, Either<NL, NR>> right) {
        return switch (this) {
            case Left(var value) -> left.apply(value);
            case Right(var value) -> right.apply(value);
        };
    }

    default <NL> Either<NL, R> flatMapLeft(Function<? super L, Either<NL, R>> left) {
        return flatMap(left, Either::right);
    }

    default <NR> Either<L, NR> flatMapRight(Function<? super R, Either<L, NR>> right) {
        return flatMap(Either::left, right);
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
}
