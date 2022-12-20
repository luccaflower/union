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

    T unwrap();
    <R> Option<R> flatMap(Function<T, Option<R>> func);
    default T unwrapOr(T defaultValue) {
        try {
            return unwrap();
        } catch (UnwrappedNone ignored) {
            return defaultValue;
        }
    }
    default T unwrapOrElse(Supplier<T> defaultFunc) {
        return unwrapOr(defaultFunc.get());
    }
    default T expect(String reason) {
        return unwrapOrElse(() -> { throw new UnwrappedNone(reason); });
    }
    default Option<T> ifSome(Consumer<T> onSome) {
        return map(some -> {
            onSome.accept(some);
            return some;
        });
    }
    @SuppressWarnings("UnusedReturnValue")
    Option<T> ifNone(Action onNone);
    default <E extends Exception> Result<T, E> okOr(E error) {
        return this.<Result<T, E>>map(Result::ok)
            .unwrapOr(Result.err(error));
    }
    default <E extends Exception> Result<T, E> okOrElse(Supplier<E> error) {
        return okOr(error.get());
    }
    default <R> Option<R> map(Function<T, R> func) {
        return flatMap(some -> some(func.apply(some)));
    }
    default <R> R mapOr(R defaultValue, Function<T, R> func) {
        return map(func).unwrapOr(defaultValue);
    }
    default <R> R mapOrElse(Supplier<R> defaultFunc, Function<T, R> presentFunc) {
        return mapOr(defaultFunc.get(), presentFunc);
    }
    default boolean isSome() {
        return map(some -> true)
            .unwrapOr(false);
    }
    default boolean isSomeAnd(Predicate<T> p) {
        return map(p::test)
            .unwrapOr(false);
    }
    default boolean isNone() {
        return map(some -> false)
            .unwrapOr(true);
    }
    Stream<T> stream();
    Option<T> or(Option<T> other);
    Option<T> orElse(Supplier<Option<T>> other);
    default boolean contains(T candidate) {
        return isSomeAnd(some -> some.equals(candidate));
    }
    default <R> Option<R> and(Option<R> other) {
        return flatMap(some -> other);
    }
    default <R> Option<R> andThen(Function<T, Option<R>> func) {
        return flatMap(func);
    }
    default Option<T> filter(Predicate<T> p) {
        return flatMap(some -> p.test(some) ? some(some) : none());
    }
    Option<T> xor(Option<T> other);

    @SuppressWarnings("unchecked")
    default <R> Option<R> flatten() {
        return flatMap(some -> some instanceof Option<?>
            ? ((Option<?>) some).flatten()
            : (Option<R>) Option.some(some));
    }
    default <R> R matches(Function<T, R> some,  Supplier<R> none) {
        return map(some)
            .unwrapOrElse(none);
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

    @FunctionalInterface
    interface Action {
        void run();
    }
}
