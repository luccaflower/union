package io.github.luccaflower.result;


import java.util.function.*;
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
    @SuppressWarnings("unchecked")
    public <R> Result<R, E> flatMap(
        Function<? super T, ? extends Result<R, E>> func
    ) {
        return Result.err(error);
    }

    @Override
    public <F extends Exception> Result<T, F> flatMapErr(
        Function<? super E, ? extends Result<T, F>> func
    ) {
        return func.apply(error);
    }

    @Override
    public E unwrapErr() {
        return error;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Err
            && ((Err<?, ?>) other).error.equals(this.error);
    }

    @Override
    public int hashCode() {
        return 5 * error.hashCode();
    }
}
