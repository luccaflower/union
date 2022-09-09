package result;

import option.*;

import java.util.function.*;
import java.util.stream.*;

import static option.Option.none;
import static option.Option.some;

public class Err<T, E extends Exception> implements Result<T, E> {
    private final E error;
    public Err(E error) {
        this.error = error;
    }

    @Override
    public T unwrap() {
        throw new UnwrappedOkOnError(error);
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
        throw new UnwrappedOkOnError(reason, error);
    }

    @Override
    public boolean isOk() {
        return false;
    }

    @Override
    public boolean isErr() {
        return true;
    }

    @Override
    public boolean isOkAnd(Predicate<T> p) {
        return false;
    }

    @Override
    public boolean isErrAnd(Predicate<E> p) {
        return p.test(error);
    }

    @Override
    public Option<T> ok() {
        return none();
    }

    @Override
    public Option<E> err() {
        return some(error);
    }

    @Override
    public <R> Result<R, E> map(Function<T, R> func) {
        return Result.err(error);
    }

    @Override
    public <R> R mapOr(R defaultValue, Function<T, R> func) {
        return defaultValue;
    }

    @Override
    public <R> R mapOrElse(Function<E, R> onErr, Function<T, R> onOk) {
        return onErr.apply(error);
    }

    @Override
    public <F extends Exception> Result<T, F> mapErr(Function<E, F> func) {
        return Result.err(func.apply(error));
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }

    @Override
    public E expectErr(String message) {
        return error;
    }

    @Override
    public E unwrapErr() {
        return error;
    }

    @Override
    public <R> Result<R, E> and(Result<R, E> res) {
        return Result.err(error);
    }

    @Override
    public <R> Result<R, E> andThen(Function<T, R> func) {
        return Result.err(error);
    }

    @Override
    public <F extends Exception> Result<T, F> or(Result<T, F> res) {
        return res;
    }

    @Override
    public boolean contains(T candidate) {
        return false;
    }

    @Override
    public boolean containsErr(E candidate) {
        return candidate.equals(error);
    }
}
