package option;

import java.util.function.*;
import java.util.stream.*;

public interface Option<T> {

    static <T> None<T> none() {
        return new None<>();
    }

    static <T> Some<T> some(T thing) {
        return new Some<>(thing);
    }

    T unwrap();

    T unwrapOr(T defaultValue);

    T unwrapOrElse(Supplier<T> defaultFunc);

    T expect(String reason);

    <R> Option<R> map(Function<T, R> func);

    <R> R mapOr(R defaultValue, Function<T, R> func);

    boolean isSome();

    boolean isSomeAnd(Predicate<T> p);

    boolean isNone();

    Stream<T> stream();

    Option<T> or(Option<T> some);

    Option<T> orElse(Supplier<Option<T>> other);

    <R> R mapOrElse(Supplier<R> defaultFunc, Function<T, R> presentFunc);

    boolean contains(T candidate);

    Option<T> and(Option<T> other);

    <R> Option<R> andThen(Function<T, Option<R>> func);

    Option<T> filter(Predicate<T> p);

    Option<T> xor(Option<T> none);

}
