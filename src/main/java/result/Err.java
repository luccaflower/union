package result;


import java.util.function.*;
import java.util.stream.*;
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
    public <R, F extends Exception> Result<R, F> flatMap(
        Function<T, Result<R, F>> func
    ) {
        return Result.err((F) error);
    }

    @Override
    public <R, F extends Exception> Result<R, F> flatMapErr(Function<E, Result<R, F>> func) {
        return func.apply(error);
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
    public E unwrapErr() {
        return error;
    }

    @Override
    public <R> Result<R, E> andThen(Function<T, R> func) {
        return Result.err(error);
    }

    @Override
    public boolean containsErr(E candidate) {
        return candidate.equals(error);
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
