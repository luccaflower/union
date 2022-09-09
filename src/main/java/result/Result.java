package result;

import option.*;

import java.util.function.*;
import java.util.stream.*;

public interface Result<T, E extends Exception> {

    static<T, E extends Exception> Ok<T, E> ok(T value) {
        return new Ok<>(value);
    }

    static<T, E extends Exception> Err<T, E> err(E error) {
        return new Err<>(error);
    }

    T unwrap();
    T unwrapOr(T defaultValue);
    T unwrapOrElse(Supplier<T> defaultFunc);
    T expect(String reason);
    boolean isOk();
    boolean isErr();
    boolean isOkAnd(Predicate<T> p);
    boolean isErrAnd(Predicate<E> p);
    Option<T> ok();
    Option<E> err();
    <R> Result<R, E> map(Function<T, R> func);
    <R> R mapOr(R defaultValue, Function<T, R> func);
    <R> R mapOrElse(Function<E, R> onErr, Function<T, R> onOk);
    <F extends Exception> Result<T, F> mapErr(Function<E, F> func);
    Stream<T> stream();
    E expectErr(String message);
    E unwrapErr();
    <R> Result<R, E> and(Result<R, E> res);
    <R> Result<R, E> andThen(Function<T, R> func);
    <F extends Exception> Result<T, F> or(Result<T, F> res);
    boolean contains(T candidate);
    boolean containsErr(E candidate);

}
