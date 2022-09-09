package option;

import java.util.function.*;
import java.util.stream.*;

public class None<T> extends Option<T> {
    @Override
    public T unwrap() {
        throw new UnwrappedNone();
    }

    @Override
    public T expect(String reason) {
        throw new UnwrappedNone(reason);
    }

    @Override
    public <R> Option<R> map(Function<T, R> func) {
        return new None<>();
    }

    @Override
    public <R> R mapOr(R defaultValue, Function<T, R> func) {
        return defaultValue;
    }

    @Override
    public boolean isSome() {
        return false;
    }

    @Override
    public boolean isNone() {
        return true;
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
    public Option<T> or(Option<T> some) {
        return some;
    }
}
