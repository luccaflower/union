package option;

import result.*;

import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface ForwardingOption<T> extends Option<T> {
    Option<T> inner();
    @Override
    default T unwrap() {
        return inner().unwrap();
    }

    @Override
    default T unwrapOr(T defaultValue) {
        return inner().unwrapOr(defaultValue);
    }

    @Override
    default T unwrapOrElse(Supplier<T> defaultFunc) {
        return inner().unwrapOrElse(defaultFunc);
    }

    @Override
    default T expect(String reason) {
        return inner().expect(reason);
    }

    @Override
    default Option<T> ifSome(Consumer<T> onSome) {
        return inner().ifSome(onSome);
    }

    @Override
    default Option<T> ifNone(Action onNone) {
        return inner().ifNone(onNone);
    }

    @Override
    default <E extends Exception> Result<T, E> okOr(E error) {
        return inner().okOr(error);
    }

    @Override
    default <E extends Exception> Result<T, E> okOrElse(Supplier<E> error) {
        return inner().okOrElse(error);
    }

    @Override
    default <R> Option<R> map(Function<T, R> func) {
        return inner().map(func);
    }

    @Override
    default <R> Option<R> flatMap(Function<T, Option<R>> func) {
        return inner().flatMap(func);
    }

    @Override
    default <R> R mapOr(R defaultValue, Function<T, R> func) {
        return inner().mapOr(defaultValue, func);
    }

    @Override
    default <R> R mapOrElse(Supplier<R> defaultFunc, Function<T, R> presentFunc) {
        return inner().mapOrElse(defaultFunc, presentFunc);
    }

    @Override
    default boolean isSome() {
        return inner().isSome();
    }

    @Override
    default boolean isSomeAnd(Predicate<T> p) {
        return inner().isSomeAnd(p);
    }

    @Override
    default boolean isNone() {
        return inner().isNone();
    }

    @Override
    default Stream<T> stream() {
        return inner().stream();
    }

    @Override
    default Option<T> or(Option<T> other) {
        return inner().or(other);
    }

    @Override
    default Option<T> orElse(Supplier<Option<T>> other) {
        return inner().orElse(other);
    }

    @Override
    default boolean contains(T candidate) {
        return inner().contains(candidate);
    }

    @Override
    default <R> Option<R> and(Option<R> other) {
        return inner().and(other);
    }

    @Override
    default <R> Option<R> andThen(Function<T, Option<R>> func) {
        return inner().andThen(func);
    }

    @Override
    default Option<T> filter(Predicate<T> p) {
        return inner().filter(p);
    }

    @Override
    default Option<T> xor(Option<T> other) {
        return inner().xor(other);
    }

    @Override
    default <R> Option<R> flatten() {
        return inner().flatten();
    }

    @Override
    default <R> R matches(Function<T, R> some, Supplier<R> none) {
        return inner().matches(some, none);
    }
}
