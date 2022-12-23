package io.github.luccaflower.result;


import java.util.function.*;

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
    public <R> Result<R, E> flatMap(
        Function<? super T, ? extends Result<R, E>> func
    ) {
        return func.apply(value);
    }

    @Override
    public <F extends Exception> Result<T, F> flatMapErr(
        Function<? super E, ? extends Result<T, F>> func) {
        return Result.ok(value);
    }

    @Override
    public E unwrapErr() {
        throw new UnwrappedOkExpectingError();
    }
    @Override
    public boolean containsErr(E candidate) {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Ok
            && ((Ok<?, ?>) other).value.equals(this.value);
    }

    public int hashCode() {
        return 13 * value.hashCode();
    }
}
