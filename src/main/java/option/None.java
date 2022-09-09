package option;

import java.util.function.*;
import java.util.stream.*;

public class None<T> extends Option<T> {
    @Override
    public T unwrap() {
        throw new UnwrappedNone();
    }

    @Override
    public T unwrapOr(T defaultValue) {
        return defaultValue;
    }

    @Override
    public T unwrapOrElse(Supplier<T> defaultFunc) {
        return defaultFunc.get();
    }

    @Override
    public T expect(String reason) {
        throw new UnwrappedNone(reason);
    }

    @Override
    public <R> Option<R> map(Function<T, R> func) {
        return none();
    }

    @Override
    public <R> R mapOr(R defaultValue, Function<T, R> func) {
        return defaultValue;
    }

    @Override
    public boolean isSome() {
        return false;
    }

    @Override
    public boolean isSomeAnd(Predicate<T> p) {
        return false;
    }

    @Override
    public boolean isNone() {
        return true;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof None<?>;
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }

    @Override
    public Option<T> or(Option<T> some) {
        return some;
    }

    @Override
    public Option<T> orElse(Supplier<Option<T>> other) {
        return other.get();
    }

    @Override
    public <R> R mapOrElse(Supplier<R> defaultFunc, Function<T, R> presentFunc) {
        return defaultFunc.get();
    }

    @Override
    public boolean contains(T candidate) {
        return false;
    }

    @Override
    public Option<T> and(Option<T> other) {
        return none();
    }

    @Override
    public <R> Option<R> andThen(Function<T, Option<R>> func) {
        return none();
    }

    @Override
    public Option<T> filter(Predicate<T> p) {
        return this;
    }

    @Override
    public Option<T> xor(Option<T> other) {
        return other;
    }

}
