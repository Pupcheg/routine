package me.supcheg.routine;

import java.util.Objects;
import java.util.function.Function;

public record Pair<L, R>(L left, R right) {

    public Pair {
        Objects.requireNonNull(left, "left");
        Objects.requireNonNull(right, "right");
    }

    static <L, R> Pair<L, R> pair(L left, R right) {
        return new Pair<>(left, right);
    }

    public <NL, NR> Pair<NL, NR> map(Function<? super L, ? extends NL> left, Function<? super R, ? extends NR> right) {
        return new Pair<>(left.apply(this.left), right.apply(this.right));
    }

    public <NL> Pair<NL, R> mapLeft(Function<? super L, ? extends NL> left) {
        return new Pair<>(left.apply(this.left), right);
    }

    public <NR> Pair<L, NR> mapRight(Function<? super R, ? extends NR> right) {
        return new Pair<>(left, right.apply(this.right));
    }

    public <NL> Pair<NL, R> withLeft(NL left) {
        return new Pair<>(left, right);
    }

    public <NR> Pair<L, NR> withRight(NR right) {
        return new Pair<>(left, right);
    }

    public Pair<R, L> flip() {
        return new Pair<>(right, left);
    }
}
