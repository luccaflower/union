package option;

import java.util.function.*;
import java.util.stream.*;

public abstract class Option<T> {

    public static<T> None<T> none() {
        return new None<>();
    }

    public static<T> Some<T> some(T thing) {
        return new Some<>(thing);
    }

    public abstract T unwrap();
    public abstract T unwrapOr(T defaultValue);

    public abstract T unwrapOrElse(Supplier<T> defaultFunc);

    public abstract T expect(String reason);

    public abstract<R> Option<R> map(Function<T, R> func);

    public abstract<R> R mapOr(R defaultValue, Function<T, R> func);

    public abstract boolean isSome();

    public abstract boolean isSomeAnd(Predicate<T> p);

    public abstract boolean isNone();

    public abstract Stream<T> stream();

    public abstract Option<T> or(Option<T> some);

    public abstract Option<T> orElse(Supplier<Option<T>> other);

    public abstract<R> R mapOrElse(
        Supplier<R> defaultFunc,
        Function<T, R> presentFunc
    );

    public abstract boolean contains(T candidate);

    public abstract Option<T> and(Option<T> other);

    public abstract<R> Option<R> andThen(Function<T, Option<R>> func);

    public abstract Option<T> filter(Predicate<T> p);

    public abstract Option<T> xor(Option<T> none);

}
