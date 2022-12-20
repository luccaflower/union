package option;


import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public class None<T> implements Option<T> {
    @Override
    public T unwrap() {
        throw new UnwrappedNone();
    }

    @Override
    public Option<T> ifNone(Action onNone) {
        onNone.run();
        return this;
    }

    @Override
    public <R> Option<R> flatMap(Function<T, Option<R>> func) {
        return Option.none();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof None<?>;
    }

    @Override
    public Stream<T> stream() {
        return Stream.empty();
    }

    @Override
    public Option<T> or(Option<T> other) {
        return other;
    }

    @Override
    public Option<T> orElse(Supplier<Option<T>> other) {
        return other.get();
    }

    @Override
    public Option<T> xor(Option<T> other) {
        return other;
    }

    @Override public int hashCode() {
        return 0;
    }
}
