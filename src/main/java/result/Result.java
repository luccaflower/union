package result;

import option.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import static option.Option.some;
import static result.Unit.unit;

/**
 * <p>
 *     Result is a type that represents either the success or failure of a given operation,
 *     similar to the Either and Result types seen in Scala.
 * </p>
 * <p>
 *     The interface provides a few abstract methods along with a number of default
 *     methods implemented with respect to these methods.
 * </p>
 * <p>
 *     Furthermore, the {@link ForwardingResult} interface is provided for easy implementation
 *     of wrapper-classes around Result-types.
 * </p>
 * @param <T> The object-type contained within the Ok-variant
 * @param <E> The exception-type contained within the Error-variant
 * @author Lucca Kaasgaard Christiansen
 */
@SuppressWarnings("unused")
public interface Result<T, E extends Exception> {

    /**
     * Instantiate an Ok-variant containing the passed parameter.
     */
    static<T, E extends Exception> Ok<T, E> ok(T value) {
        return new Ok<>(value);
    }

    /**
     * Instantiate an Error-variant containing the passed Exception.
     */
    static<T, E extends Exception> Err<T, E> err(E error) {
        return new Err<>(error);
    }

    /**
     * Utility-function used to instantiate an Ok-variant containing a Unit-instance,
     * representing a successful operation which returns nothing.
     */
    static<E extends Exception> Ok<Unit, E> ok() {
        return ok(unit());
    }

    /**
     * Utility-function used to instantiate an Error-variant containing a default
     * Exception
     */
    static<T> Err<T, Exception> err() {
        return err(new ResultException());
    }

    /**
     * Tries to extract the object contained within an Ok-variant. It is
     * considered bad practice to call this method directly unless you can
     * guarantee that you are calling it on an Ok-variant
     * @throws UnwrappedErrorExpectingOk if called on an Error-variant
     */
    T unwrap();

    /**
     * Tries to extract the Exception contained within an Error-variant.
     * It is considered bad practice to call this method directly unless you can
     * guarantee that you are calling it on an Error-variant*
     * @throws UnwrappedOkExpectingError if called on an Ok-variant
     */
    E unwrapErr();
    /**
     * On an Ok-variant, this method applies the passed function to the inner
     * object and returns the result.
     */
    <R, F extends Exception> Result<R, F> flatMap(
        Function<? super T, ? extends Result<R, F>> func
    );

    /**
     * On an error variant, this method applies the passed function to the inner
     * Exception and returns the result.
     */
    <R, F extends Exception> Result<R, F> flatMapErr(
        Function<? super E, ? extends Result<R, F>> func
    );

    /**
     * Return a stream of the maybe-contained object of an Ok-variant. Returns an
     * empty stream on an Error-variant.
     */
    default Stream<T> stream() {
        return map(Stream::of)
            .unwrapOr(Stream.empty());
    }

    /**
     * Tries to extract the object contained within an Ok-variant, and returns the
     * default value if called on an Error.
     */
    default T unwrapOr(T defaultValue) {
        try {
            return unwrap();
        } catch (UnwrappedErrorExpectingOk ignored) {
            return defaultValue;
        }
    }
    /**
     * Tries to extract the object contained within an Ok-variant, and returns the
     * resulting value of evaluating the default function if called on an Error.
     */

    default T unwrapOrElse(Supplier<? extends T> defaultFunc) {
        return unwrapOr(defaultFunc.get());
    }

    /**
     * Tries to extract the object contained within an Ok-variant.
     * @throws UnwrappedErrorExpectingOk containing the reason-parameter when
     * called on an Error.
     */
    default T expect(String reason) {
        try {
            return unwrap();
        } catch (UnwrappedErrorExpectingOk ignored) {
            throw new UnwrappedErrorExpectingOk(reason, unwrapErr());
        }
    }
    /**
     * Tries to extract the Exception contained within an Error-variant.
     * @throws UnwrappedErrorExpectingOk containing the reason-parameter when
     * called on an Ok.
     */
    default E expectErr(String message) {
        try {
            return unwrapErr();
        } catch (UnwrappedOkExpectingError ignored) {
            throw new UnwrappedOkExpectingError(message);
        }
    }
    /**
     * Passes the object contained within an Ok-variant to the passed function.
     * Assuming the consumer has no side effects that mutate the Result,
     * this is a noop, that leaves the Result unaffected.
     * This method does nothing on Error-variants.
     */
    default Result<T, E> ifOk(Consumer<? super T> onOk) {
        return this.map(ok -> {
            onOk.accept(ok);
            return ok;
        });
    }
    /**
     * Passes the object contained within an Error-variant to the passed function.
     * Assuming the consumer has no side effects that mutate the Result,
     * this is a noop, that leaves the Result unaffected.
     * This method does nothing on Ok-variants.
     */
    default Result<T, E> ifErr(Consumer<? super E> onErr) {
        return mapErr(err -> {
            onErr.accept(err);
            return err;
        });
    }

    /**
     * Returns true on Ok and false on Error
     */
    default boolean isOk() {
        return map(ok -> true)
            .unwrapOr(false);
    }

    /**
     * Returns the result of applying the predicate to the inner object on Ok,
     * and false on Error.
     */
    default boolean isOkAnd(
        Predicate<? super T> p
    ) {
        return map(p::test).unwrapOr(false);
    }

    /**
     * Returns true on Error and false on Ok
     */
    default boolean isErr() {
        return map(ok -> false)
            .flatMapErr(err -> ok(true))
            .unwrap();
    }

    /**
     * Returns the result of applying the predicate to the inner Exception on
     * Error and false on Ok
     */
    default boolean isErrAnd(Predicate<? super E> p) {
        return map(ok -> false)
            .flatMapErr(err -> ok(p.test(err)))
            .unwrap();
    }

    /**
     * Converts the Result to an instance of {@link Option}. Returns Some containing
     * the inner object on Ok and None on Error.
     */
    default Option<T> okToOption() {
        return map(Option::some)
            .flatMapErr(err -> ok(Option.<T>none()))
            .unwrap();
    }

    /**
     * Converts the Result to an instance of {@link Option}. Returns Some containing
     * the inner Exception on Error and None on Ok.
     */
    default Option<E> errToOption() {
        return map(ok -> Option.<E>none())
            .flatMapErr(err -> ok(some(err)))
            .unwrap();
    }

    /**
     * Applies the function to the inner object and returns an Ok containing the
     * result, if the called on Ok. Does nothing on Error.
     */
    default <R> Result<R, E> map(Function<? super T, ? extends R> func) {
        return flatMap(ok -> ok(func.apply(ok)));
    }

    /**
     * Applies the function to the inner Exception and returns an Error containing the
     * result, if the called on Error. Does nothing on Ok.
     */
    default <F extends Exception> Result<T, F> mapErr(
        Function<? super E, ? extends F> func
    ) {
        return flatMapErr(err -> err(func.apply(err)));
    }

    /**
     * Applies the function to the inner object contained in an Ok-variant and
     * returns the result, and returns the default value on Error.
     */
    default <R> R mapOr(
        R defaultValue,
        Function<? super T, ? extends R> func) {
        return this
            .<R>map(func).unwrapOr(defaultValue);
    }
    /**
     * Applies the function to the inner object contained in an Ok-variant and
     * returns the result, and returns the result of evaluating default function
     * on Error.
     */
    default <R> R mapOrElse(
        Function<? super E,? extends R> onErr,
        Function<? super T,? extends R> onOk
    ) {
        return matches(onOk, onErr);
    }

    /**
     * Returns the second Result if both are Ok, otherwise returns the first Error.
     */
    default <R, F extends Exception> Result<R, F> and(Result<R, F> other) {
        return flatMap(ok -> other);
    }

    /**
     * Alias for map
     */
    default <R> Result<R, E> andThen(Function<? super T, ? extends R> func) {
        return map(func);
    }

    /**
     * Returns the first Ok-value. If both Results are Errors, it returns the second Error.
     */
    default <F extends Exception> Result<T, F> or(Result<T, F> res) {
        return flatMapErr(err -> res);
    }

    /**
     * If the result is Ok, this method checks for equality between the candidate
     * and the object contained within the Ok.
     */
    default boolean contains(T candidate) {
        return isOkAnd(ok -> ok.equals(candidate));
    }
    /**
     * If the result is Err, this method checks for equality between the candidate
     * and the Exception contained within the Error.
     */
    default boolean containsErr(E candidate) {
        return isErrAnd(err -> err.equals(candidate));
    }

    /**
     * Flattens any nested Results so that, for instance a {@code Result<Result<Result<T, E>>>>}
     * becomes a {@code Result<T, E>}
     */
    @SuppressWarnings("unchecked")
    default <R, F extends Exception> Result<R, F> flatten() {
        return flatMap(ok -> ok instanceof Result<?,?>
            ? ((Result<?, ?>) ok).flatten()
            : (Result<R, F>) Result.ok(ok));

    }

    /**
     * Evalutates the Ok-function on Ok, and the Error-function on Error.
     */
    default <R> R matches(
        Function<? super T, ? extends R> ok,
        Function<? super E, ? extends R> err
    ) {
        return map(ok)
            .flatMapErr(e -> ok(err.apply(e)))
            .unwrap();
    }

    /**
     * Converts a {@code List<Result<T, E>} to a {@code Result<List<T>, E>}.
     * If any Result contained within the List is an Error, it returns the first
     * Error
     */
    static <
        T,
        E extends Exception> Collector<Result<T, E>,
        List<Result<T, E>>,
        Result<List<T>, E>>
    andCollector() {
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

    /**
     * Converts a {@code List<Result<T, E>} to a {@code Result<List<T>, E>}.
     * If any Results within the List are Ok, then it returns an Ok containing
     * all objects contained within Ok-variants. Otherwise, it returns the last
     * encountered Error.
     */
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
