package me.supcheg.routine;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import static java.util.function.Predicate.not;
import static me.supcheg.routine.Pair.pair;

/// Utility class providing collectors for aggregating [Either].
///
/// @see Either
/// @see Pair
public final class EitherCollectors {

    private EitherCollectors() {}

    /// Collects a stream of [Either] into a [Pair], applying separate downstream collectors for left and right values.
    ///
    /// @param leftDownstream  collector to accumulate left values
    /// @param rightDownstream collector to accumulate right values
    /// @param <L>             type of left values
    /// @param <R>             type of right values
    /// @param <ILS>           intermediate accumulation type of the left collector
    /// @param <IRS>           intermediate accumulation type of the right collector
    /// @param <LS>            final collection type for left values
    /// @param <RS>            final collection type for right values
    /// @return a [Collector] producing a [Pair] containing all left and right values from the stream
    ///
    /// ```java
    /// Stream<Either<Integer, String>> stream = Stream.of(
    ///     Either.left(1),
    ///     Either.right("a"),
    ///     Either.left(2),
    ///     Either.right("b")
    /// );
    ///
    /// Pair<List<Integer>, List<String>> result = stream.collect(
    ///     EitherCollectors.groupingTo(
    ///         Collectors.toList(),
    ///         Collectors.toList()
    ///     )
    /// );
    /// // result.left() -> [1, 2]
    /// // result.right() -> ["a", "b"]
    /// ```
    public static <L, R, ILS, IRS, LS, RS> Collector<Either<L, R>, ?, Pair<LS, RS>> groupingTo(
            Collector<? super L, ILS, ? extends LS> leftDownstream,
            Collector<? super R, IRS, ? extends RS> rightDownstream) {
        return Collector.of(
                () -> pair(
                        leftDownstream.supplier().get(),
                        rightDownstream.supplier().get()),
                (Pair<ILS, IRS> pair, Either<L, R> either) -> either.accept(
                        value -> leftDownstream.accumulator().accept(pair.left(), value),
                        value -> rightDownstream.accumulator().accept(pair.right(), value)),
                (Pair<ILS, IRS> left, Pair<ILS, IRS> right) -> left.map(
                        value -> leftDownstream.combiner().apply(value, right.left()),
                        value -> rightDownstream.combiner().apply(value, right.right())),
                (Pair<ILS, IRS> pair) -> pair.map(leftDownstream.finisher(), rightDownstream.finisher()),
                intersection(leftDownstream.characteristics(), rightDownstream.characteristics()));
    }

    private static Characteristics[] intersection(Set<Characteristics> left, Set<Characteristics> right) {
        var intersection = EnumSet.noneOf(Characteristics.class);
        intersection.addAll(left);
        intersection.removeIf(not(right::contains));
        return intersection.toArray(Characteristics[]::new);
    }
}
