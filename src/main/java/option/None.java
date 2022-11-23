package option;

import result.*;

import java.util.function.*;
import java.util.stream.*;

import static result.Result.err;

@SuppressWarnings("unused")
public class None<T> implements Option<T> {
    @Override
    public T unwrap() {
        throw new UnwrappedNone();
    }

    @Override
    public T unwrapOr(T defaultValue) {
        return defaultValue;
    }

    @Override
    public T unwrapOr(Supplier<T> defaultFunc) {
        return defaultFunc.get();
    }

    @Override
    public T expect(String reason) {
        throw new UnwrappedNone(reason);
    }

    @Override
    public Option<T> ifSome(Consumer<T> onSome) {
        return this;
    }

    @Override
    public Option<T> ifNone(Action onNone) {
        onNone.run();
        return this;
    }

    @Override
    public <E extends Exception> Result<T, E> okOr(E error) {
        return err(error);
    }

    @Override
    public <E extends Exception> Result<T, E> okOrElse(Supplier<E> error) {
        return err(error.get());
    }

    @Override
    public <R> Option<R> map(Function<T, R> func) {
        return Option.none();
    }

    @Override
    public <U, R> Option<R> flatMap(Function<U, Option<R>> func) {
        return Option.none();
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
    public Option<T> or(Option<T> other) {
        return other;
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
    public <R> Option<R> and(Option<R> other) {
        return Option.none();
    }

    @Override
    public <R> Option<R> andThen(Function<T, Option<R>> func) {
        return Option.none();
    }

    @Override
    public Option<T> filter(Predicate<T> p) {
        return this;
    }

    @Override
    public Option<T> xor(Option<T> other) {
        return other;
    }

    @Override
    public <R> Option<R> flatten() {
        return Option.none();
    }

    @Override
    public <R> R matches(Function<T, R> some, Supplier<R> none) {
        return none.get();
    }

    private static Option<?> noop(Option<?> o) {
        return o;
    }
}
