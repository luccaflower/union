package io.github.luccaflower.option;


import java.util.function.*;

@SuppressWarnings("unused")
public class None<T> implements Option<T> {
    protected None() {}
    @Override
    public T unwrap() {
        throw new UnwrappedNone();
    }

    @Override
    public <R> Option<R> flatMap(Function<? super T, ? extends Option<R>> func) {
        return Option.none();
    }

    @Override
    public Option<T> orElse(Supplier<? extends Option<T>> other) {
        return other.get();
    }
    @Override
    public boolean equals(Object other) {
        return other instanceof None<?>;
    }

    @Override public int hashCode() {
        return 0;
    }
}
