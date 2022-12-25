package io.github.luccaflower.result;

import java.util.function.*;

/**
 * ForwardingResult is a utility interface that functions as a wrapper around any given {@link Result}
 * and delegates calls to the inner Result.
 */
@SuppressWarnings("unused")
public interface ForwardingResult<T> extends Result<T> {
    Result<T> inner();

    @Override
    default Result<T> flatMapErr(
        Function<? super Exception, ? extends Result<T>> func
    ) {
        return inner().flatMapErr(func);
    }

    @Override
    default T unwrap() {
        return inner().unwrap();
    }

    @Override
    default Exception unwrapErr() {
        return inner().unwrapErr();
    }

    @Override
    default <R> Result<R> flatMap(
        Function<? super T, ? extends Result<R>> func
    ) {
        return inner().flatMap(func);
    }
}
