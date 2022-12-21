package result;

import option.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static option.Option.some;
import static result.Unit.unit;

@SuppressWarnings("unused")
public interface Result<T, E extends Exception> {

    static<T, E extends Exception> Ok<T, E> ok(T value) {
        return new Ok<>(value);
    }

    static<T, E extends Exception> Err<T, E> err(E error) {
        return new Err<>(error);
    }

    static<E extends Exception> Ok<Unit, E> ok() {
        return ok(unit());
    }

    static<T> Err<T, Exception> err() {
        return err(new ResultException());
    }
    T unwrap();
    E unwrapErr();
    <R, F extends Exception> Result<R, F> flatMap(
        Function<? super T, ? extends Result<R, F>> func
    );

    <R, F extends Exception> Result<R, F> flatMapErr(
        Function<? super E, ? extends Result<R, F>> func
    );
    default Stream<T> stream() {
        return map(Stream::of)
            .unwrapOr(Stream.empty());
    }
    default T unwrapOr(T defaultValue) {
        try {
            return unwrap();
        } catch (UnwrappedErrorExpectingOk ignored) {
            return defaultValue;
        }
    }
    default T unwrapOrElse(Supplier<? extends T> defaultFunc) {
        return unwrapOr(defaultFunc.get());
    }
    default T expect(String reason) {
        try {
            return unwrap();
        } catch (UnwrappedErrorExpectingOk ignored) {
            throw new UnwrappedErrorExpectingOk(reason, unwrapErr());
        }
    }
    default E expectErr(String message) {
        try {
            return unwrapErr();
        } catch (UnwrappedOkExpectingError ignored) {
            throw new UnwrappedOkExpectingError(message);
        }
    }
    default Result<T, E> ifOk(Consumer<? super T> onOk) {
        return this.map(ok -> {
            onOk.accept(ok);
            return ok;
        });
    }
    default Result<T, E> ifErr(Consumer<? super E> onErr) {
        return mapErr(err -> {
            onErr.accept(err);
            return err;
        });
    }
    default boolean isOk() {
        return map(ok -> true)
            .unwrapOr(false);
    }
    default boolean isOkAnd(
        Predicate<? super T> p
    ) {
        return map(p::test).unwrapOr(false);
    }
    default boolean isErr() {
        return map(ok -> false)
            .flatMapErr(err -> ok(true))
            .unwrap();
    }
    default boolean isErrAnd(Predicate<? super E> p) {
        return map(ok -> false)
            .flatMapErr(err -> ok(p.test(err)))
            .unwrap();
    }
    default Option<T> okToOption() {
        return map(Option::some)
            .flatMapErr(err -> ok(Option.<T>none()))
            .unwrap();
    }
    default Option<E> errToOption() {
        return map(ok -> Option.<E>none())
            .flatMapErr(err -> ok(some(err)))
            .unwrap();
    }
    default <R> Result<R, E> map(Function<? super T, ? extends R> func) {
        return flatMap(ok -> ok(func.apply(ok)));
    }
    default <F extends Exception> Result<T, F> mapErr(
        Function<? super E, ? extends F> func
    ) {
        return flatMapErr(err -> err(func.apply(err)));
    }
    default <R> R mapOr(
        R defaultValue,
        Function<? super T, ? extends R> func) {
        return this
            .<R>map(func).unwrapOr(defaultValue);
    }
    default <R> R mapOrElse(
        Function<? super E,? extends R> onErr,
        Function<? super T,? extends R> onOk
    ) {
        return matches(onOk, onErr);
    }
    default <R, F extends Exception> Result<R, F> and(Result<R, F> other) {
        return flatMap(ok -> other);
    }
    default <R> Result<R, E> andThen(Function<? super T, ? extends R> func) {
        return map(func);
    }
    default <F extends Exception> Result<T, F> or(Result<T, F> res) {
        return flatMapErr(err -> res);
    }
    default boolean contains(T candidate) {
        return isOkAnd(ok -> ok.equals(candidate));
    }
    default boolean containsErr(E candidate) {
        return isErrAnd(err -> err.equals(candidate));
    }
    @SuppressWarnings("unchecked")
    default <R, F extends Exception> Result<R, F> flatten() {
        return flatMap(ok -> ok instanceof Result<?,?>
            ? ((Result<?, ?>) ok).flatten()
            : (Result<R, F>) Result.ok(ok));

    }
    default <R> R matches(
        Function<? super T, ? extends R> ok,
        Function<? super E, ? extends R> err
    ) {
        return map(ok)
            .flatMapErr(e -> ok(err.apply(e)))
            .unwrap();
    }

    static <T, E extends Exception> Collector<Result<T, E>, List<Result<T, E>>, Result<List<T>, E>> andCollector() {
        return new Collector<>() {
            @Override
            public Supplier<List<Result<T, E>>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<Result<T, E>>, Result<T, E>> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<Result<T, E>>> combiner() {
                return (one, other) -> {
                    one.addAll(other);
                    return one;
                };
            }

            @Override
            public Function<List<Result<T, E>>, Result<List<T>, E>> finisher() {
                return list -> list.stream().<Result<List<T>, E>>reduce(
                    ok(new ArrayList<>()),
                    (acc, e) -> e.and(acc).map(ok -> {
                        ok.add(e.unwrap());
                        return ok;
                    }),
                    (one, other) -> one.and(other).map(ok -> {
                        ok.addAll(one.unwrap());
                        return ok;
                    })
                );
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.UNORDERED);
            }
        };
    }

    static <T, E extends Exception> Collector<Result<T, E>, List<Result<T, E>>, Result<List<T>, E>> orCollector() {
        return new Collector<>() {
            @Override
            public Supplier<List<Result<T, E>>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<Result<T, E>>, Result<T, E>> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<Result<T, E>>> combiner() {
                return (one, other) -> {
                    one.addAll(other);
                    return one;
                };
            }

            @Override
            public Function<List<Result<T, E>>, Result<List<T>, E>> finisher() {
                return list -> list.stream().reduce(
                    err().flatten(),
                    (acc, e) -> e.map(
                        ok -> {
                            var res = acc.unwrapOr(new ArrayList<>());
                            res.add(ok);
                            return res;
                        }
                    ).<E>or(acc),
                    (one, other) -> one.and(other).map(ok -> {
                        ok.addAll(other.unwrap());
                        return ok;
                    }).or(one)
                        .or(other)
                );
            }
            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.UNORDERED);
            }
        };
    }

    class ResultException extends Exception {
        @Override
        public boolean equals(Object other) {
            return other instanceof ResultException;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }
}
