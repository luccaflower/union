package option;

import java.util.function.*;
import java.util.stream.*;

public abstract class Option<T> {

    public static<T> None<T> none() {
        return new None<>();
    }

    public static<T> Some<T> some(T thing) {
        return new Some<>(thing);
    }

    public abstract T unwrap();

    public abstract T expect(String reason);

    public abstract<R> Option<R> map(Function<T, R> func);

    public abstract<R> R mapOr(R defaultValue, Function<T, R> func);

    public abstract boolean isSome();

    public abstract boolean isNone();

    public abstract Stream<T> stream();

    public abstract Option<T> or(Option<T> some);
}
