package io.github.luccaflower.option;

import io.github.luccaflower.result.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

/**
 * <p>
 *     A Maybe-type, similar to Java's built-in {@link Optional} class.
 *     This class provides static instantiation methods for either case, as well
 *     as a conversion method from @{@link Optional}
 * </p>
 * <p>
 *     This interface contains 3 abstract methods: unwrap, flatMap, and orElse,
 *     and then provides default implementations with respect to these methods.
 * </p>
 * <p>
 *     Further more, the {@link ForwardingOption} interface is provided for easy
 *     implementation of wrapper classes.
 * </p>
 * @param <T> The object type contained within the Some-variant
 * @author Lucca Kaasgaard Christiansen
 */
@SuppressWarnings("unused")
public interface Option<T> {

    /**
     * Instantiate the None-variant of the class.
     */
    static <T> None<T> none() {
        return new None<>();
    }

    /**
     * Return the Some-variant containing the object passed to the function.
     */
    static <T> Some<T> some(T thing) {
        return new Some<>(thing);
    }

    /**
     * A utility-function to convert a {@link Optional} to an instance of this type.
     */
    static <T> Option<T> from(Optional<T> optional) {
        return optional.isPresent()
            ? some(optional.get())
            : none();
    }

    /**
     * Extraction method to retrive the inner object if present.
     * It is bad practice to call this method unless the client code can guarantee there is an object present.
     * @throws UnwrappedNone on {@link None}
     */
    T unwrap();

    /**
     * Given a function which takes an instance of type T as input, if the object is {@link Some},
     * this method will return the result of applying that method to the inner object.
     * @param <R> The inner type of the new option
   **/
    <R> Option<R> flatMap(Function<? super T, ? extends Option<R>> func);

    /**
     * The {@link None}-counterpart to flatMap. Returns the Option returned by the passed function,
     * in case of the None-variant,
     * otherwise the method returns its own instance.
     */
    Option<T> orElse(Supplier<? extends Option<T>> other);
    default Stream<T> stream() {
        return map(Stream::of)
            .unwrapOr(Stream.empty());
    }

    /**
     * Tries to extract the inner object if there is one, otherwise returns the default
     */
    default T unwrapOr(T defaultValue) {
        try {
            return unwrap();
        } catch (UnwrappedNone ignored) {
            return defaultValue;
        }
    }

    /**
     * Tries to extract the inner object, otherwise it evaluates the
     * passed function and returns the result.
     */
    default T unwrapOrElse(Supplier<? extends T> defaultFunc) {
        return unwrapOr(defaultFunc.get());
    }

    /**
     * Tries to extract the inner object, otherwise it throws an exception containing
     * the error message passed to the parameter.
     * @throws UnwrappedNone containing the reason.
     */
    default T expect(String reason) {
        return unwrapOrElse(() -> { throw new UnwrappedNone(reason); });
    }

    /**
     * On Some-variants, this method passes the inner object to the passed function.
     * Assuming the consumer has no side effects the mutate the Option,
     * this is a noop, that leaves the Option unaffected.
     */
    default Option<T> ifSome(Consumer<? super T> onSome) {
        return map(some -> {
            onSome.accept(some);
            return some;
        });
    }

    /**
     * Evaluates the Action if the Option is None.
     * The Action is a Functional Interface which takes no arguments and returns no values,
     * so it is assumed to have side effects.
     * Like ifSome, it is a noop, disregarding any side effects from running the action.
     */
    @SuppressWarnings("UnusedReturnValue")
    default Option<T> ifNone(Action onNone) {
        return orElse(() -> {
            onNone.run();
            return this;
        });
    }

    /**
     * Converts this instance to a {@link Result} that is {@link Ok} on Some, and an {@link Err} containing the passed Exception on None.
     */
    default <E extends Exception> Result<T, E> okOr(E error) {
        return this.<Result<T, E>>map(Result::ok)
            .unwrapOr(Result.err(error));
    }

    /**
     * Converts this object to a {@link Result} that is {@link Ok} on Some,
     * and an {@link Err} containing the result of evaluating the passed function.
     */
    default <E extends Exception> Result<T, E> okOrElse(Supplier<? extends E> error) {
        return okOr(error.get());
    }

    /**
     * Applies the function to the inner object if present and returns a new
     * Option containing the result.
     */
    default <R> Option<R> map(Function<? super T, ? extends R> func) {
        return flatMap(some -> some(func.apply(some)));
    }

    /**
     * Applies the function to the inner object if present and returns the result,
     * otherwise it returns the default value.
     */
    default <R> R mapOr(R defaultValue, Function<? super T, ? extends R> func) {
        return this.<R>map(func).unwrapOr(defaultValue);
    }

    /**
     * Applies the function to the inner object if present and returns it,
     * otherwise it returns the result of evaluating the default function.
     */
    default <R> R mapOrElse(
        Supplier<? extends R> defaultFunc,
        Function<? super T, ? extends R> presentFunc
    ) {
        return mapOr(defaultFunc.get(), presentFunc);
    }

    /**
     * Returns true on Some and false on None
     */
    default boolean isSome() {
        return map(some -> true)
            .unwrapOr(false);
    }

    /**
     * Returns true on Some if the inner object passes the predicate.
     */
    default boolean isSomeAnd(Predicate<? super T> p) {
        return map(p::test)
            .unwrapOr(false);
    }

    /**
     * Returns true on None and false on Some
     */
    default boolean isNone() {
        return map(some -> false)
            .unwrapOr(true);
    }

    /**
     * Returns the first option if it is Some, and the second Option, if the first is None.
     */
    default Option<T> or(Option<T> other) {
        return orElse(() -> other);
    }

    default boolean contains(T candidate) {
        return isSomeAnd(some -> some.equals(candidate));
    }

    /**
     * Returns the first Option if it is None, otherwise it returns Some
     */
    default <R> Option<R> and(Option<R> other) {
        return flatMap(some -> other);
    }

    /**
     * Alias for flatMap
     */
    default <R> Option<R> andThen(Function<? super T, ? extends Option<R>> func) {
        return flatMap(func);
    }

    /**
     * Returns Some if an object is present and it passes the predicate, otherwise it returns None.
     */
    default Option<T> filter(Predicate<T> p) {
        return flatMap(some -> p.test(some) ? some(some) : none());
    }

    /**
     * If only one Option is Some, it returns Some, otherwise it returns None.
     */
    default Option<T> xor(Option<T> other) {
        return isSome() ^ other.isSome() ? or(other) : none();
    }


    /**
     * Flattens any nested Options. Thus, in case you have a structure such as {@code Option<Option<Option<T>>>} this returns an {@code Option<T>}
     */
    @SuppressWarnings("unchecked")
    default <R> Option<R> flatten() {
        return flatMap(some -> some instanceof Option<?>
            ? ((Option<?>) some).flatten()
            : (Option<R>) Option.some(some));
    }

    /**
     * Applies the some-function on the inner object if it is present, otherwise it evaluates the none-function.
     */
    default <R> R matches(
        Function<? super T, ? extends R> some,
        Supplier<? extends R> none
    ) {
        return this
            .<R>map(some)
            .unwrapOrElse(none);
    }


    /**
     * This collector takes a {@code List<Option<T>>} and converts it to an {@code Option<List<T>>},
     * If any objects are present, this returns a Some with a List containing all objects, otherwise it returns None
     */
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

    /**
     * This collector converts a {@code List<Option<T>>} into an {@code Option<List<T>>}.
     * This collector returns Some if, and only if, all Options in the List are Some, otherwise it returns None.
     */
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
