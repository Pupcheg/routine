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
/// @since 1.0.0
public sealed interface Either<L, R> {

    /// Represents the left variant of [Either].
    ///
    /// `value` is guaranteed to be non-null.
    ///
    /// @param value left value
    /// @param <L>   type of the stored value
    /// @param <R>   type of the right branch (phantom type parameter)
    /// @since 1.0.0
    record Left<L, R>(L value) implements Either<L, R> {

        /// Constructs a [Left] instance with a non-null value.
        ///
        /// @throws NullPointerException if [Left#value] is `null`
        /// @since 1.0.0
        public Left {
            Objects.requireNonNull(value, "value");
        }
    }

    /// Represents the right variant of [Either].
    ///
    /// `value` is guaranteed to be non-null.
    ///
    /// @param value right value
    /// @param <L>   type of the left branch (phantom type parameter)
    /// @param <R>   type of the stored value
    /// @since 1.0.0
    record Right<L, R>(R value) implements Either<L, R> {

        /// Constructs a [Right] instance with a non-null value.
        ///
        /// @throws NullPointerException if [Right#value] is `null`
        /// @since 1.0.0
        public Right {
            Objects.requireNonNull(value, "value");
        }
    }

    /// Creates a [Left] instance.
    ///
    /// @param value left value
    /// @param <L>   left type
    /// @param <R>   right type
    /// @return an [Either] in the [Left] state
    /// @since 1.0.0
    static <L, R> Left<L, R> left(L value) {
        return new Left<>(value);
    }

    /// Creates a [Right] instance.
    ///
    /// @param value right value
    /// @param <L>   left type
    /// @param <R>   right type
    /// @return an [Either] in the [Right] state
    /// @since 1.0.0
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
    /// @since 1.0.0
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
    /// @since 1.0.0
    default <NL> Either<NL, R> mapLeft(Function<? super L, ? extends NL> left) {
        return switch (this) {
            case Left(var value) -> left(left.apply(value));
            case Right<L, R> right -> {
                @SuppressWarnings("unchecked")
                var result = (Either<NL, R>) right;
                yield result;
            }
        };
    }

    /// Maps only the right branch.
    ///
    /// @param right mapping function
    /// @param <NR>  new right type
    /// @return [Either] with transformed right value
    /// @since 1.0.0
    default <NR> Either<L, NR> mapRight(Function<? super R, ? extends NR> right) {
        return switch (this) {
            case Left<L, R> left -> {
                @SuppressWarnings("unchecked")
                var result = (Either<L, NR>) left;
                yield result;
            }
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
    /// @since 1.0.0
    default <NL, NR> Either<NL, NR> flatMap(
            Function<? super L, ? extends Either<? extends NL, ? extends NR>> left,
            Function<? super R, ? extends Either<? extends NL, ? extends NR>> right) {
        @SuppressWarnings("unchecked")
        var result = (Either<NL, NR>)
                switch (this) {
                    case Left(var value) -> left.apply(value);
                    case Right(var value) -> right.apply(value);
                };
        return Objects.requireNonNull(result);
    }

    /// Monadic composition on the left branch only.
    ///
    /// @param left mapping function
    /// @param <NL> new left type
    /// @return composed [Either]
    /// @since 1.0.0
    default <NL> Either<NL, R> flatMapLeft(Function<? super L, ? extends Either<? extends NL, ? extends R>> left) {
        @SuppressWarnings("unchecked")
        var result = (Either<NL, R>)
                switch (this) {
                    case Left(var value) -> Objects.requireNonNull(left.apply(value));
                    case Right<L, R> right -> right;
                };
        return result;
    }

    /// Monadic composition on the right branch only.
    ///
    /// @param right mapping function
    /// @param <NR>  new right type
    /// @return composed [Either]
    /// @since 1.0.0
    default <NR> Either<L, NR> flatMapRight(Function<? super R, ? extends Either<? extends L, ? extends NR>> right) {
        @SuppressWarnings("unchecked")
        var result = (Either<L, NR>)
                switch (this) {
                    case Left<L, R> left -> left;
                    case Right(var value) -> Objects.requireNonNull(right.apply(value));
                };
        return result;
    }

    /// Swaps the left and right branches.
    ///
    /// @return an [Either] with inverted type parameters
    /// @since 1.0.0
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
    /// @since 1.0.0
    default <T> T fold(Function<? super L, ? extends T> left, Function<? super R, ? extends T> right) {
        return switch (this) {
            case Left(var value) -> left.apply(value);
            case Right(var value) -> right.apply(value);
        };
    }

    /// Returns the left value wrapped in [Optional], if present.
    ///
    /// @return optional left value
    /// @since 1.0.0
    default Optional<L> left() {
        return fold(Optional::of, _ -> Optional.empty());
    }

    /// Returns the right value wrapped in [Optional], if present.
    ///
    /// @return optional right value
    /// @since 1.0.0
    default Optional<R> right() {
        return fold(_ -> Optional.empty(), Optional::of);
    }

    /// Executes side effects for the corresponding branch without modifying this [Either].
    ///
    /// @param left  consumer for the left branch
    /// @param right consumer for the right branch
    /// @return this [Either]
    /// @since 1.0.0
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
    /// @since 1.0.0
    default Either<L, R> peekLeft(Consumer<? super L> left) {
        ifLeft(left);
        return this;
    }

    /// Executes a side effect if this value is [Right].
    ///
    /// @param right consumer for the right branch
    /// @return this [Either]
    /// @since 1.0.0
    default Either<L, R> peekRight(Consumer<? super R> right) {
        ifRight(right);
        return this;
    }

    /// Consumes the value using the appropriate consumer.
    ///
    /// @param left  consumer for the left branch
    /// @param right consumer for the right branch
    /// @since 1.0.0
    default void accept(Consumer<? super L> left, Consumer<? super R> right) {
        switch (this) {
            case Left(var value) -> left.accept(value);
            case Right(var value) -> right.accept(value);
        }
    }

    /// Executes the given consumer if this value is [Left].
    ///
    /// @param left consumer for the left branch
    /// @since 1.0.0
    default void ifLeft(Consumer<? super L> left) {
        if (this instanceof Left(var value)) {
            left.accept(value);
        }
    }

    /// Executes the given consumer if this value is [Right].
    ///
    /// @param right consumer for the right branch
    /// @since 1.0.0
    default void ifRight(Consumer<? super R> right) {
        if (this instanceof Right(var value)) {
            right.accept(value);
        }
    }
}
