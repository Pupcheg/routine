package me.supcheg.routine;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

/// An immutable algebraic data type representing an **ordered pair of two non-null values**.
///
/// `Pair<L, R>` always contains **both** a value of type `L` and a value of type `R`.
///
/// @param <L> type of the left component
/// @param <R> type of the right component
/// @see Either
/// @see Map.Entry
public record Pair<L, R>(L left, R right) {

    /// Constructs a [Pair] with non-null components.
    ///
    /// @throws NullPointerException if either [Pair#left] or [Pair#right] is `null`
    public Pair {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(right, "right");
    }

    /// Creates a [Pair] instance.
    ///
    /// This is a convenience factory method equivalent to invoking the constructor directly.
    ///
    /// @param left  left component
    /// @param right right component
    /// @param <L>   left type
    /// @param <R>   right type
    /// @return a new [Pair] containing the given values
    public static <L, R> Pair<L, R> pair(L left, R right) {
        return new Pair<>(left, right);
    }

    /// Creates a [Pair] from a [Map.Entry].
    ///
    /// The entry key becomes the left component, and the entry value becomes the right component.
    ///
    /// @param entry source map entry
    /// @param <L>   key type
    /// @param <R>   value type
    /// @return a [Pair] representing the given entry
    /// @throws NullPointerException if the key or value of the entry is `null`
    public static <L, R> Pair<L, R> pairFromEntry(Map.Entry<L, R> entry) {
        return pair(entry.getKey(), entry.getValue());
    }

    /// Applies independent mapping functions to both components of this pair.
    ///
    /// This operation corresponds to a *bifunctor map* over a product type.
    ///
    /// @param left  mapping function for the left component
    /// @param right mapping function for the right component
    /// @param <NL>  new left type
    /// @param <NR>  new right type
    /// @return a new [Pair] with both components transformed
    public <NL, NR> Pair<NL, NR> map(Function<? super L, ? extends NL> left, Function<? super R, ? extends NR> right) {
        return new Pair<>(left.apply(this.left), right.apply(this.right));
    }

    /// Maps the left component while leaving the right component unchanged.
    ///
    /// @param left mapping function for the left component
    /// @param <NL> new left type
    /// @return a new [Pair] with a transformed left component
    public <NL> Pair<NL, R> mapLeft(Function<? super L, ? extends NL> left) {
        return new Pair<>(left.apply(this.left), right);
    }

    /// Maps the right component while leaving the left component unchanged.
    ///
    /// @param right mapping function for the right component
    /// @param <NR>  new right type
    /// @return a new [Pair] with a transformed right component
    public <NR> Pair<L, NR> mapRight(Function<? super R, ? extends NR> right) {
        return new Pair<>(left, right.apply(this.right));
    }

    /// Performs monadic-like composition over both components of this [Pair].
    ///
    /// @param function a bifunction applied to both components
    /// @param <NL>     new left type
    /// @param <NR>     new right type
    /// @return [Pair] produced by the given function
    public <NL, NR> Pair<NL, NR> flatMap(BiFunction<? super L, ? super R, Pair<NL, NR>> function) {
        return function.apply(left, right);
    }

    /// Performs composition based on the left component only.
    ///
    /// @param function mapping function for the left component
    /// @param <NL>     new left type
    /// @param <NR>     new right type
    /// @return [Pair] produced by applying the function to the left component
    public <NL, NR> Pair<NL, NR> flatMapLeft(Function<? super L, Pair<NL, NR>> function) {
        return function.apply(left);
    }

    /// Performs composition based on the right component only.
    ///
    /// @param function mapping function for the right component
    /// @param <NL>     new left type
    /// @param <NR>     new right type
    /// @return [Pair] produced by applying the function to the right component
    public <NL, NR> Pair<NL, NR> flatMapRight(Function<? super R, Pair<NL, NR>> function) {
        return function.apply(right);
    }

    /// Replaces the left component with a new value.
    ///
    /// @param left new left value
    /// @param <NL> new left type
    /// @return a new [Pair] with the specified left component
    public <NL> Pair<NL, R> withLeft(NL left) {
        return new Pair<>(left, right);
    }

    /// Replaces the right component with a new value.
    ///
    /// @param right new right value
    /// @param <NR>  new right type
    /// @return a new [Pair] with the specified right component
    public <NR> Pair<L, NR> withRight(NR right) {
        return new Pair<>(left, right);
    }

    /// Swaps the left and right components.
    ///
    /// @return a [Pair] with inverted type parameters and component order
    public Pair<R, L> flip() {
        return new Pair<>(right, left);
    }

    /// Folds this [Pair] into a single value.
    ///
    /// @param function a bifunction applied to the left and right components
    /// @param <T>      result type
    /// @return the result of applying the function to both components
    public <T> T fold(BiFunction<? super L, ? super R, ? extends T> function) {
        return function.apply(left, right);
    }

    /// Converts this pair into a standard [Map.Entry].
    ///
    /// The left component becomes the key, and the right component becomes the value.
    ///
    /// @return a [Map.Entry] view of this pair
    public Map.Entry<L, R> asEntry() {
        return Map.entry(left, right);
    }
}
