package option;

import java.util.function.*;
import java.util.stream.*;

@SuppressWarnings("unused")
public interface ForwardingOption<T> extends Option<T> {
    Option<T> inner();
    @Override
    default T unwrap() {
        return inner().unwrap();
    }

    @Override
    default Option<T> ifNone(Action onNone) {
        return inner().ifNone(onNone);
    }

    @Override
    default <R> Option<R> flatMap(Function<T, Option<R>> func) {
        return inner().flatMap(func);
    }

    @Override
    default Stream<T> stream() {
        return inner().stream();
    }

    @Override
    default Option<T> or(Option<T> other) {
        return inner().or(other);
    }

    @Override
    default Option<T> orElse(Supplier<Option<T>> other) {
        return inner().orElse(other);
    }

    @Override
    default Option<T> xor(Option<T> other) {
        return inner().xor(other);
    }

    @Override
    default <R> Option<R> flatten() {
        return inner().flatten();
    }
}
