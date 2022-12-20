package result;


import java.util.function.*;
import java.util.stream.*;

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
    public <R, F extends Exception> Result<R, F> flatMap(Function<T, Result<R, F>> func) {
        return func.apply(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R, F extends Exception> Result<R, F> flatMapErr(Function<E, Result<R, F>> func) {
        return Result.ok((R) value);
    }

    @Override
    public Stream<T> stream() {
        return Stream.of(value);
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
