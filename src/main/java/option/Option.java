package option;

import result.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface Option<T> {

    static <T> None<T> none() {
        return new None<>();
    }

    static <T> Some<T> some(T thing) {
        return new Some<>(thing);
    }

    static <T> Option<T> from(Optional<T> optional) {
        return optional.isPresent()
            ? some(optional.get())
            : none();
    }

    static <T> Collector<Option<T>, List<T>, Option<List<T>>> orCollector() {
        return new Collector<>() {
            @Override
            public Supplier<List<T>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<T>, Option<T>> accumulator() {
                return (acc, e) -> {
                    if (e.isSome()) acc.add(e.unwrap());
                };
            }

            @Override
            public BinaryOperator<List<T>> combiner() {
                return (one, other) -> {
                    one.addAll(other);
                    return one;
                };
            }

            @Override
            public Function<List<T>, Option<List<T>>> finisher() {
                return list -> list.isEmpty()
                    ? none()
                    : some(list);
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.UNORDERED);
            }
        };
    }

    static <T> Collector<Option<T>, List<Option<T>>, Option<List<T>>> andCollector() {
        return new Collector<>() {
            @Override
            public Supplier<List<Option<T>>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<Option<T>>, Option<T>> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<Option<T>>> combiner() {
                return (one, other) -> {
                    one.addAll(other);
                    return one;
                };
            }

            @Override
            public Function<List<Option<T>>, Option<List<T>>> finisher() {
                return list -> list.stream().<Option<List<T>>>reduce(
                    Option.some(new ArrayList<>()),
                    (acc, e) -> e.and(acc).map(some -> {
                        some.add(e.unwrap());
                        return some;
                    }),
                    (one, other) -> one.and(other).map(some -> {
                        some.addAll(one.unwrap());
                        return some;
                    })
                );
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.UNORDERED);
            }
        };
    }

    T unwrap();
    T unwrapOr(T defaultValue);
    T unwrapOr(Supplier<T> defaultFunc);
    T expect(String reason);
    Option<T> ifSome(Consumer<T> onSome);
    @SuppressWarnings("UnusedReturnValue")
    Option<T> ifNone(Action onNone);
    <E extends Exception> Result<T, E> okOr(E error);
    <E extends Exception> Result<T, E> okOrElse(Supplier<E> error);
    <R> Option<R> map(Function<T, R> func);
    <U, R> Option<R> flatMap(Function<U, Option<R>> func);
    <R> R mapOr(R defaultValue, Function<T, R> func);
    <R> R mapOrElse(Supplier<R> defaultFunc, Function<T, R> presentFunc);
    boolean isSome();
    boolean isSomeAnd(Predicate<T> p);
    boolean isNone();
    Stream<T> stream();
    Option<T> or(Option<T> other);
    Option<T> orElse(Supplier<Option<T>> other);
    boolean contains(T candidate);
    <R> Option<R> and(Option<R> other);
    <R> Option<R> andThen(Function<T, Option<R>> func);
    Option<T> filter(Predicate<T> p);
    Option<T> xor(Option<T> other);
    <R> Option<R> flatten();
    <R> R matches(Function<T, R> some,  Supplier<R> none);

    @FunctionalInterface
    interface Action {
        void run();
    }
}
