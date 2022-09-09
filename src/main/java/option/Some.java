package option;

import java.util.function.*;
import java.util.stream.*;

public class Some<T> extends Option<T> {
    private final T something;

    public Some(T something) {
        this.something = something;
    }

    @Override
    public T unwrap() {
        return something;
    }

    @Override
    public T expect(String reason) {
        return unwrap();
    }

    @Override
    public<R> Option<R> map(Function<T, R> func) {
        return new Some<>(func.apply(something));
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
    public boolean equals(Object other) {
        if (other instanceof Some<?>) {
            return ((Some<?>) other).something.equals(this.something);
        } else {
            return false;
        }
    }
}
