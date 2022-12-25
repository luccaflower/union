package io.github.luccaflower.result;


import java.util.function.*;

@SuppressWarnings("unused")
public class Ok<T> implements Result<T>{
    private final T value;

    public Ok(T value) {
        this.value = value;
    }

    @Override
    public T unwrap() {
        return value;
    }

    @Override
    public <R> Result<R> flatMap(
        Function<? super T, ? extends Result<R>> func
    ) {
        return func.apply(value);
    }

    @Override
    public Result<T> flatMapErr(
        Function<? super Exception, ? extends Result<T>> func) {
        return Result.ok(value);
    }

    @Override
    public Exception unwrapErr() {
        throw new UnwrappedOkExpectingError();
    }
    @Override
    public boolean containsErr(Exception candidate) {
        return false;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Ok
            && ((Ok<?>) other).value.equals(this.value);
    }

    public int hashCode() {
        return 13 * value.hashCode();
    }
}
