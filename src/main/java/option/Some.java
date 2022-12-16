package option;

import result.*;

import java.util.function.*;
import java.util.stream.*;

import static result.Result.ok;

@SuppressWarnings("unused")
public class Some<T> implements Option<T> {
    private final T something;

    public Some(T something) {
        this.something = something;
    }

    @Override
    public T unwrap() {
        return something;
    }

    @Override
    public T unwrapOr(T defaultValue) {
        return something;
    }

    @Override
    public T unwrapOrElse(Supplier<T> defaultFunc) {
        return something;
    }

    @Override
    public T expect(String reason) {
        return something;
    }

    @Override
    public Option<T> ifSome(Consumer<T> onSome) {
        onSome.accept(something);
        return this;
    }

    @Override
    public Option<T> ifNone(Action onNone) {
        return this;
    }

    @Override
    public <E extends Exception> Result<T, E> okOr(E error) {
        return ok(something);
    }

    @Override
    public <E extends Exception> Result<T, E> okOrElse(Supplier<E> error) {
        return ok(something);
    }

    @Override
    public<R> Option<R> map(Function<T, R> func) {
        return Option.some(func.apply(something));
    }

    @Override
    public <R> Option<R> flatMap(Function<T, Option<R>> func) {
        return func.apply(something);
    }

    @Override
    public <R> R mapOr(R defaultValue, Function<T, R> func) {
        return func.apply(something);
    }

    @Override
    public boolean isSome() {
        return true;
    }

    @Override
    public boolean isSomeAnd(Predicate<T> p) {
        return p.test(something);
    }

    @Override
    public boolean isNone() {
        return false;
    }

    @Override
    public Stream<T> stream() {
        return Stream.of(something);
    }

    @Override
    public Option<T> or(Option<T> some) {
        return this;
    }

    @Override
    public Option<T> orElse(Supplier<Option<T>> other) {
        return this;
    }

    @Override
    public <R> R mapOrElse(Supplier<R> defaultFunc, Function<T, R> presentFunc) {
        return presentFunc.apply(something);
    }

    @Override
    public boolean contains(T candidate) {
        return something.equals(candidate);
    }

    @Override
    public <R> Option<R> and(Option<R> other) {
        return other;
    }

    @Override
    public <R> Option<R> andThen(Function<T, Option<R>> func) {
        return func.apply(something);
    }

    @Override
    public Option<T> filter(Predicate<T> p) {
        return p.test(something)
            ? this
            : Option.none();
    }

    @Override
    public Option<T> xor(Option<T> other) {
        return other.isSome()
            ? Option.none()
            : this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> Option<R> flatten() {
        return something instanceof Option<?>
            ? ((Option<?>) something).flatten()
            : (Option<R>) Option.some(something);
    }

    @Override
    public <R> R matches(Function<T, R> some, Supplier<R> none) {
        return some.apply(something);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Some<?>) {
            return ((Some<?>) other).something.equals(this.something);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return 9 * something.hashCode();
    }
}
