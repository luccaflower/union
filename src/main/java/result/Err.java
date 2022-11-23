package result;

import option.*;

import java.util.function.*;
import java.util.stream.*;

import static option.Option.none;
import static option.Option.some;

@SuppressWarnings("unused")
public class Err<T, E extends Exception> implements Result<T, E> {
    private final E error;
    public Err(E error) {
        this.error = error;
    }

    @Override
    public T unwrap() {
        throw new UnwrappedErrorExpectingOk(error);
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
        throw new UnwrappedErrorExpectingOk(reason, error);
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
    public Option<T> okToOption() {
        return none();
    }

    @Override
    public Option<E> errToOption() {
        return some(error);
    }

    @Override
    public <R> Result<R, E> map(Function<T, R> func) {
        return Result.err(error);
    }

    public <U, R> Result<R, E> flatMap(Function<U, Result<R, E>> func) {
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
    public Result<T, E> ifOk(Consumer<T> onOk) {
        return this;
    }

    @Override
    public Result<T, E> ifErr(Consumer<E> onErr) {
        onErr.accept(error);
        return this;
    }

    @Override
    public E unwrapErr() {
        return error;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, F extends Exception> Result<R, F> and(Result<R, F> res) {
        return (Result<R, F>) this;
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

    @Override
    @SuppressWarnings("unchecked")
    public <R, F extends Exception> Result<R, F> flatten() {
        return (Result<R, F>) Result.err(error);
    }

    @Override
    public <R> R matches(Function<T, R> ok, Function<E, R> err) {
        return err.apply(error);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Err
            && ((Err<?, ?>) other).error.equals(this.error);
    }
}
