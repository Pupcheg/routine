package me.supcheg.routine;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/// An algebraic data type representing a value that can be in **exactly one of two mutually exclusive states**:
/// - [Left] — contains a value of type `L`
/// - [Right] — contains a value of type `R`
///
/// @param <L> type of the left value
/// @param <R> type of the right value
/// @see EitherCollectors
/// @see Pair
public sealed interface Either<L, R> {

    /// Represents the left variant of [Either].
    ///
    /// `value` is guaranteed to be non-null.
    ///
    /// @param value left value
    /// @param <L> type of the stored value
    /// @param <R> type of the right branch (phantom type parameter)
    record Left<L, R>(L value) implements Either<L, R> {

        /// Constructs a [Left] instance with a non-null value.
        ///
        /// @throws NullPointerException if [Left#value] is `null`
        public Left {
            Objects.requireNonNull(value, "value");
        }

        /// Casts `Left<L, R>` to `Left<L, NR>` without modifying the stored value.
        ///
        /// This method preserves the left value while changing the right type parameter. It performs an
        /// unchecked cast and is therefore **type-unsafe**, but logically sound due to the absence of any
        /// right-side value.
        ///
        /// Intended for internal use in transformation methods.
        ///
        /// @param <NR> new right-side type
        /// @return the same object with a different type parameter
        @SuppressWarnings("unchecked")
        public <NR> Left<L, NR> castRight() {
            return (Left<L, NR>) this;
        }
    }

    /// Represents the right variant of [Either].
    ///
    /// `value` is guaranteed to be non-null.
    ///
    /// @param value right value
    /// @param <L>   type of the left branch (phantom type parameter)
    /// @param <R>   type of the stored value
    record Right<L, R>(R value) implements Either<L, R> {

        /// Constructs a [Right] instance with a non-null value.
        ///
        /// @throws NullPointerException if [Right#value] is `null`
        public Right {
            Objects.requireNonNull(value, "value");
        }

        /// Casts `Right<L, R>` to `Right<NL, R>` without modifying the stored value.
        ///
        /// @param <NL> new left-side type
        /// @return the same object with a different type parameter
        @SuppressWarnings("unchecked")
        public <NL> Right<NL, R> castLeft() {
            return (Right<NL, R>) this;
        }
    }

    /// Creates a [Left] instance.
    ///
    /// @param value left value
    /// @param <L>   left type
    /// @param <R>   right type
    /// @return an [Either] in the [Left] state
    static <L, R> Left<L, R> left(L value) {
        return new Left<>(value);
    }

    /// Creates a [Right] instance.
    ///
    /// @param value right value
    /// @param <L>   left type
    /// @param <R>   right type
    /// @return an [Either] in the [Right] state
    static <L, R> Right<L, R> right(R value) {
        return new Right<>(value);
    }

    /// Applies a bifunctional mapping to both branches.
    ///
    /// - if this is [Left], the `left` function is applied;
    /// - if this is [Right], the `right` function is applied.
    ///
    /// @param left  mapping function for the left branch
    /// @param right mapping function for the right branch
    /// @param <NL>  new left type
    /// @param <NR>  new right type
    /// @return a new `Either` with transformed value
    default <NL, NR> Either<NL, NR> map(
            Function<? super L, ? extends NL> left, Function<? super R, ? extends NR> right) {
        return switch (this) {
            case Left(var value) -> left(left.apply(value));
            case Right(var value) -> right(right.apply(value));
        };
    }

    /// Maps only the left branch.
    ///
    /// @param left mapping function
    /// @param <NL> new left type
    /// @return [Either] with transformed left value
    default <NL> Either<NL, ? extends R> mapLeft(Function<? super L, ? extends NL> left) {
        return switch (this) {
            case Left(var value) -> left(left.apply(value));
            case Right<L, R> right -> right.castLeft();
        };
    }

    /// Maps only the right branch.
    ///
    /// @param right mapping function
    /// @param <NR>  new right type
    /// @return [Either] with transformed right value
    default <NR> Either<L, NR> mapRight(Function<? super R, ? extends NR> right) {
        return switch (this) {
            case Left<L, R> left -> left.castRight();
            case Right(var value) -> right(right.apply(value));
        };
    }

    /// Performs monadic composition on both branches.
    ///
    /// @param left  mapping function for the left branch
    /// @param right mapping function for the right branch
    /// @param <NL>  new left type
    /// @param <NR>  new right type
    /// @return result of the composition
    default <NL, NR> Either<NL, NR> flatMap(
            Function<? super L, Either<NL, NR>> left, Function<? super R, Either<NL, NR>> right) {
        return switch (this) {
            case Left(var value) -> left.apply(value);
            case Right(var value) -> right.apply(value);
        };
    }

    /// Monadic composition on the left branch only.
    ///
    /// @param left mapping function
    /// @param <NL> new left type
    /// @return composed [Either]
    default <NL> Either<NL, R> flatMapLeft(Function<? super L, Either<NL, R>> left) {
        return switch (this) {
            case Left(var value) -> left.apply(value);
            case Right<L, R> right -> right.castLeft();
        };
    }

    /// Monadic composition on the right branch only.
    ///
    /// @param right mapping function
    /// @param <NR>  new right type
    /// @return composed [Either]
    default <NR> Either<L, NR> flatMapRight(Function<? super R, Either<L, NR>> right) {
        return switch (this) {
            case Left<L, R> left -> left.castRight();
            case Right(var value) -> right.apply(value);
        };
    }

    /// Swaps the left and right branches.
    ///
    /// @return an [Either] with inverted type parameters
    default Either<R, L> flip() {
        return flatMap(Either::right, Either::left);
    }

    /// Folds this `Either` into a single value.
    ///
    /// This is the fundamental elimination operation, equivalent to exhaustive pattern matching.
    ///
    /// @param left  function applied to the left value
    /// @param right function applied to the right value
    /// @param <T>   result type
    /// @return result of applying the corresponding function
    default <T> T fold(Function<? super L, ? extends T> left, Function<? super R, ? extends T> right) {
        return switch (this) {
            case Left(var value) -> left.apply(value);
            case Right(var value) -> right.apply(value);
        };
    }

    /// Returns the left value wrapped in [Optional], if present.
    ///
    /// @return optional left value
    default Optional<L> left() {
        return fold(Optional::of, _ -> Optional.empty());
    }

    /// Returns the right value wrapped in [Optional], if present.
    ///
    /// @return optional right value
    default Optional<R> right() {
        return fold(_ -> Optional.empty(), Optional::of);
    }

    /// Executes side effects for the corresponding branch without modifying this [Either].
    ///
    /// @param left  consumer for the left branch
    /// @param right consumer for the right branch
    /// @return this [Either]
    default Either<L, R> peek(Consumer<? super L> left, Consumer<? super R> right) {
        switch (this) {
            case Left(var value) -> left.accept(value);
            case Right(var value) -> right.accept(value);
        }
        return this;
    }

    /// Executes a side effect if this value is [Left].
    ///
    /// @param left consumer for the left branch
    /// @return this [Either]
    default Either<L, R> peekLeft(Consumer<? super L> left) {
        ifLeft(left);
        return this;
    }

    /// Executes a side effect if this value is [Right].
    ///
    /// @param right consumer for the right branch
    /// @return this [Either]
    default Either<L, R> peekRight(Consumer<? super R> right) {
        ifRight(right);
        return this;
    }

    /// Consumes the value using the appropriate consumer.
    ///
    /// @param left  consumer for the left branch
    /// @param right consumer for the right branch
    default void accept(Consumer<? super L> left, Consumer<? super R> right) {
        switch (this) {
            case Left(var value) -> left.accept(value);
            case Right(var value) -> right.accept(value);
        }
    }

    /// Executes the given consumer if this value is [Left].
    ///
    /// @param left consumer for the left branch
    default void ifLeft(Consumer<? super L> left) {
        if (this instanceof Left(var value)) {
            left.accept(value);
        }
    }

    /// Executes the given consumer if this value is [Right].
    ///
    /// @param right consumer for the right branch
    default void ifRight(Consumer<? super R> right) {
        if (this instanceof Right(var value)) {
            right.accept(value);
        }
    }
}
