package result;

import option.*;

import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface Result<T, E extends Exception> {

    static<T, E extends Exception> Ok<T, E> ok(T value) {
        return new Ok<>(value);
    }

    static<T, E extends Exception> Err<T, E> err(E error) {
        return new Err<>(error);
    }

    static<E extends Exception> Ok<Void, E> ok() {
        return ok(new Void());
    }

    static<T> Err<T, Exception> err() {
        return err(new Exception());
    }

    T unwrap();
    T unwrapOr(T defaultValue);
    T unwrapOr(Supplier<T> defaultFunc);
    E unwrapErr();
    T expect(String reason);
    E expectErr(String message);
    boolean isOk();
    boolean isOkAnd(Predicate<T> p);
    boolean isErr();
    boolean isErrAnd(Predicate<E> p);
    Option<T> okToOption();
    Option<E> errToOption();
    <R> Result<R, E> map(Function<T, R> func);
    <F extends Exception> Result<T, F> mapErr(Function<E, F> func);
    <R> R mapOr(R defaultValue, Function<T, R> func);
    <R> R mapOrElse(Function<E, R> onErr, Function<T, R> onOk);
    Stream<T> stream();
    <R> Result<R, E> and(Result<R, E> res);
    <R> Result<R, E> and(Function<T, R> func);
    <F extends Exception> Result<T, F> or(Result<T, F> res);
    boolean contains(T candidate);
    boolean containsErr(E candidate);

    <R, F extends Exception> Result<R, F> flatten();

    <R> R matches(Function<T, R> ok, Function<E, R> err);

    final class Void {}

}
