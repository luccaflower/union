package result;

import option.*;

import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface ForwardingResult<T, E extends Exception> extends Result<T, E> {
    Result<T, E> inner();

    @Override
    default T unwrap() {
        return inner().unwrap();
    }

    @Override
    default T unwrapOr(T defaultValue) {
        return inner().unwrapOr(defaultValue);
    }

    @Override
    default T unwrapOrElse(Supplier<T> defaultFunc) {
        return inner().unwrapOrElse(defaultFunc);
    }

    @Override
    default E unwrapErr() {
        return inner().unwrapErr();
    }

    @Override
    default T expect(String reason) {
        return inner().expect(reason);
    }

    @Override
    default E expectErr(String message) {
        return inner().expectErr(message);
    }

    @Override
    default Result<T, E> ifOk(Consumer<T> onOk) {
        return inner().ifOk(onOk);
    }

    @Override
    default Result<T, E> ifErr(Consumer<E> onErr) {
        return inner().ifErr(onErr);
    }

    @Override
    default boolean isOk() {
        return inner().isOk();
    }

    @Override
    default boolean isOkAnd(Predicate<T> p) {
        return inner().isOkAnd(p);
    }

    @Override
    default boolean isErr() {
        return inner().isErr();
    }

    @Override
    default boolean isErrAnd(Predicate<E> p) {
        return inner().isErrAnd(p);
    }

    @Override
    default Option<T> okToOption() {
        return inner().okToOption();
    }

    @Override
    default Option<E> errToOption() {
        return inner().errToOption();
    }

    @Override
    default <R> Result<R, E> map(Function<T, R> func) {
        return inner().map(func);
    }

    @Override
    default <R, F extends Exception> Result<R, F> flatMap(Function<T, Result<R, F>> func) {
        return inner().flatMap(func);
    }

    @Override
    default <F extends Exception> Result<T, F> mapErr(Function<E, F> func) {
        return inner().mapErr(func);
    }

    @Override
    default <R> R mapOr(R defaultValue, Function<T, R> func) {
        return inner().mapOr(defaultValue, func);
    }

    @Override
    default <R> R mapOrElse(Function<E, R> onErr, Function<T, R> onOk) {
        return inner().mapOrElse(onErr, onOk);
    }

    @Override
    default Stream<T> stream() {
        return inner().stream();
    }

    @Override
    default <R, F extends Exception> Result<R, F> and(Result<R, F> res) {
        return inner().and(res);
    }

    @Override
    default <R> Result<R, E> andThen(Function<T, R> func) {
        return inner().andThen(func);
    }

    @Override
    default <F extends Exception> Result<T, F> or(Result<T, F> res) {
        return inner().or(res);
    }

    @Override
    default boolean contains(T candidate) {
        return inner().contains(candidate);
    }

    @Override
    default boolean containsErr(E candidate) {
        return inner().containsErr(candidate);
    }

    @Override
    default <R, F extends Exception> Result<R, F> flatten() {
        return inner().flatten();
    }

    @Override
    default <R> R matches(Function<T, R> ok, Function<E, R> err) {
        return inner().matches(ok, err);
    }
}
