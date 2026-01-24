package me.supcheg.routine;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;

import static java.util.function.Predicate.not;
import static me.supcheg.routine.Pair.pair;

public final class EitherCollectors {
    private EitherCollectors() {}

    public static <L, R, ILS, IRS, LS extends Collection<L>, RS extends Collection<R>>
            Collector<Either<L, R>, ?, Pair<LS, RS>> toCollectionsPair(
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
