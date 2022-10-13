package result;

import option.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface Result<T, E extends Exception> {

    static<T, E extends Exception> Ok<T, E> ok(T value) {
        return new Ok<>(value);
    }

    static<T, E extends Exception> Err<T, E> err(E error) {
        return new Err<>(error);
    }

    static<E extends Exception> Ok<Void, E> ok() {
        return ok(new Void());
    }

    static<T> Err<T, Exception> err() {
        return err(new Exception());
    }

    T unwrap();
    T unwrapOr(T defaultValue);
    T unwrapOrElse(Supplier<T> defaultFunc);
    E unwrapErr();
    T expect(String reason);
    E expectErr(String message);
    boolean isOk();
    boolean isOkAnd(Predicate<T> p);
    boolean isErr();
    boolean isErrAnd(Predicate<E> p);
    Option<T> okToOption();
    Option<E> errToOption();
    <R> Result<R, E> map(Function<T, R> func);
    <R, Fa, Fr> Result<R, E> flatMap(Function<Fa, Fr> func);
    <F extends Exception> Result<T, F> mapErr(Function<E, F> func);
    <R> R mapOr(R defaultValue, Function<T, R> func);
    <R> R mapOrElse(Function<E, R> onErr, Function<T, R> onOk);
    Stream<T> stream();
    <R, F extends Exception> Result<R, F> and(Result<R, F> res);
    <R> Result<R, E> andThen(Function<T, R> func);
    <F extends Exception> Result<T, F> or(Result<T, F> res);
    boolean contains(T candidate);
    boolean containsErr(E candidate);
    <R, F extends Exception> Result<R, F> flatten();
    <R> R matches(Function<T, R> ok, Function<E, R> err);

    final class Void {
        @Override
        public boolean equals(Object other) {
            return other instanceof Void;
        }
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
                    (acc, e) -> e.<List<T>>map(
                        ok -> {
                            var res = acc.unwrapOr(new ArrayList<>());
                            res.add(ok);
                            return res;
                        }
                    ).or(acc),
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
}
