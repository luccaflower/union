package option;

import java.util.function.*;
import java.util.stream.*;

public class Some<T> extends Option<T> {
    private final T something;

    public Some(T something) {
        this.something = something;
    }

    @Override
    public T unwrap() {
        return something;
    }

    @Override
    public T unwrapOr(T defaultValue) {
        return something;
    }

    @Override
    public T unwrapOrElse(Supplier<T> defaultFunc) {
        return something;
    }

    @Override
    public T expect(String reason) {
        return something;
    }

    @Override
    public<R> Option<R> map(Function<T, R> func) {
        return some(func.apply(something));
    }

    @Override
    public <R> R mapOr(R defaultValue, Function<T, R> func) {
        return func.apply(something);
    }

    @Override
    public boolean isSome() {
        return true;
    }

    @Override
    public boolean isSomeAnd(Predicate<T> p) {
        return p.test(something);
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public Stream<T> stream() {
        return Stream.of(something);
    }

    @Override
    public Option<T> or(Option<T> some) {
        return this;
    }

    @Override
    public Option<T> orElse(Supplier<Option<T>> other) {
        return this;
    }

    @Override
    public <R> R mapOrElse(Supplier<R> defaultFunc, Function<T, R> presentFunc) {
        return presentFunc.apply(something);
    }

    @Override
    public boolean contains(T candidate) {
        return something.equals(candidate);
    }

    @Override
    public Option<T> and(Option<T> other) {
        return other;
    }

    @Override
    public <R> Option<R> andThen(Function<T, Option<R>> func) {
        return func.apply(something);
    }

    @Override
    public Option<T> filter(Predicate<T> p) {
        return p.test(something)
            ? this
            : none();
    }

    @Override
    public Option<T> xor(Option<T> other) {
        return other.isSome()
            ? none()
            : this;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Some<?>) {
            return ((Some<?>) other).something.equals(this.something);
        } else {
            return false;
        }
    }
}
