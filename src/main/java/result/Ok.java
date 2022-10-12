package result;

import option.*;

import java.util.function.*;
import java.util.stream.*;

import static option.Option.none;
import static option.Option.some;
@SuppressWarnings("unused")
public class Ok<T, E extends Exception> implements Result<T, E>{
    private final T value;

    public Ok(T value) {
        this.value = value;
    }

    @Override
    public T unwrap() {
        return value;
    }

    @Override
    public T unwrapOr(T defaultValue) {
        return value;
    }

    @Override
    public T unwrapOrElse(Supplier<T> defaultFunc) {
        return value;
    }

    @Override
    public T expect(String reason) {
        return value;
    }

    @Override
    public boolean isOk() {
        return true;
    }

    @Override
    public boolean isErr() {
        return false;
    }

    @Override
    public boolean isOkAnd(Predicate<T> p) {
        return p.test(value);
    }

    @Override
    public boolean isErrAnd(Predicate<E> p) {
        return false;
    }

    @Override
    public Option<T> okToOption() {
        return some(value);
    }

    @Override
    public Option<E> errToOption() {
        return none();
    }

    @Override
    public <R> Result<R, E> map(Function<T, R> func) {
        return Result.ok(func.apply(value));
    }
    @Override
    public <R, Fa, Fr> Result<R, E> flatMap(Function<Fa, Fr> func) {
        return this.<Fa, E>flatten().map(func).flatten();
    }

    @Override
    public <R> R mapOr(R defaultValue, Function<T, R> func) {
        return func.apply(value);
    }

    @Override
    public <R> R mapOrElse(Function<E, R> onErr, Function<T, R> onOk) {
        return onOk.apply(value);
    }

    @Override
    public <F extends Exception> Result<T, F> mapErr(Function<E, F> func) {
        return Result.ok(value);
    }

    @Override
    public Stream<T> stream() {
        return Stream.of(value);
    }

    @Override
    public E expectErr(String message) {
        throw new UnwrappedOkExpectingError(message);
    }

    @Override
    public E unwrapErr() {
        throw new UnwrappedOkExpectingError();
    }

    @Override
    public <R, F extends Exception> Result<R, F> and(Result<R, F> res) {
        return res;
    }

    @Override
    public <R> Result<R, E> andThen(Function<T, R> func) {
        return Result.ok(func.apply(value));
    }

    @Override
    public <F extends Exception> Result<T, F> or(Result<T, F> res) {
        return Result.ok(value);
    }

    @Override
    public boolean contains(T candidate) {
        return value.equals(candidate);
    }

    @Override
    public boolean containsErr(E candidate) {
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, F extends Exception> Result<R, F> flatten() {
        return value instanceof Result<?,?>
            ? ((Result<?, ?>) value).flatten()
            : (Result<R, F>) Result.ok(value);
    }

    @Override
    public <R> R matches(Function<T, R> ok, Function<E, R> err) {
        return ok.apply(value);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Ok
            && ((Ok<?, ?>) other).value.equals(this.value);
    }
}
