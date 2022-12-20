package result;

import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface ForwardingResult<T, E extends Exception> extends Result<T, E> {
    Result<T, E> inner();

    @Override
    default <R, F extends Exception> Result<R, F> flatMapErr(Function<E, Result<R, F>> func) {
        return inner().flatMapErr(func);
    }

    @Override
    default T unwrap() {
        return inner().unwrap();
    }

    @Override
    default E unwrapErr() {
        return inner().unwrapErr();
    }

    @Override
    default <R, F extends Exception> Result<R, F> flatMap(Function<T, Result<R, F>> func) {
        return inner().flatMap(func);
    }

    @Override
    default Stream<T> stream() {
        return inner().stream();
    }
}