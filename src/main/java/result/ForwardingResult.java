package result;

import java.util.function.*;

@SuppressWarnings("unused")
public interface ForwardingResult<T, E extends Exception> extends Result<T, E> {
    Result<T, E> inner();

    @Override
    default <R, F extends Exception> Result<R, F> flatMapErr(
        Function<? super E, ? extends Result<R, F>> func
    ) {
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
    default <R, F extends Exception> Result<R, F> flatMap(
        Function<? super T, ? extends Result<R, F>> func
    ) {
        return inner().flatMap(func);
    }
}
