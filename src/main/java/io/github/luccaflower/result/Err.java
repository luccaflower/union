package io.github.luccaflower.result;


import java.util.function.*;
@SuppressWarnings("unused")
public class Err<T> implements Result<T> {
    private final Exception error;
    protected Err(Exception error) {
        this.error = error;
    }

    @Override
    public T unwrap() {
        throw new UnwrappedErrorExpectingOk(error);
    }
    @Override
    public <R> Result<R> flatMap(
        Function<? super T, ? extends Result<R>> func
    ) {
        return Result.err(error);
    }

    @Override
    public Result<T> flatMapErr(
        Function<? super Exception, ? extends Result<T>> func
    ) {
        return func.apply(error);
    }

    @Override
    public Exception unwrapErr() {
        return error;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Err
            && ((Err<?>) other).error.equals(this.error);
    }

    @Override
    public int hashCode() {
        return 5 * error.hashCode();
    }
}
