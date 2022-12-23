package io.github.luccaflower.result;

import java.util.function.*;

/**
 * ForwardingResult is a utility interface that functions as a wrapper around any given {@link Result}
 * and delegates calls to the inner Result.
 */
@SuppressWarnings("unused")
public interface ForwardingResult<T, E extends Exception> extends Result<T, E> {
    Result<T, E> inner();

    @Override
    default <F extends Exception> Result<T, F> flatMapErr(
        Function<? super E, ? extends Result<T, F>> func
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
    default <R> Result<R, E> flatMap(
        Function<? super T, ? extends Result<R, E>> func
    ) {
        return inner().flatMap(func);
    }
}
