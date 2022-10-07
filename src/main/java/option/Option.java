package option;

import result.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface Option<T> {

    static <T> None<T> none() {
        return new None<>();
    }

    static <T> Some<T> some(T thing) {
        return new Some<>(thing);
    }

    static <T> Option<T> from(Optional<T> optional) {
        return optional.isPresent()
            ? some(optional.get())
            : none();
    }

    T unwrap();
    T unwrapOr(T defaultValue);
    T unwrapOr(Supplier<T> defaultFunc);
    T expect(String reason);
    <E extends Exception> Result<T, E> okOr(E error);
    <E extends Exception> Result<T, E> okOrElse(Supplier<E> error);
    <R> Option<R> map(Function<T, R> func);
    <R> R mapOr(R defaultValue, Function<T, R> func);
    <R> R mapOrElse(Supplier<R> defaultFunc, Function<T, R> presentFunc);
    boolean isSome();
    boolean isSomeAnd(Predicate<T> p);
    boolean isNone();
    Stream<T> stream();
    Option<T> or(Option<?> other);
    Option<T> orElse(Supplier<Option<T>> other);
    boolean contains(T candidate);
    Option<T> and(Option<T> other);
    <R> Option<R> andThen(Function<T, Option<R>> func);
    Option<T> filter(Predicate<T> p);
    Option<T> xor(Option<T> other);
    <R> Option<R> flatten();
    <R> R matches(Function<T, R> some,  Supplier<R> none);
}
