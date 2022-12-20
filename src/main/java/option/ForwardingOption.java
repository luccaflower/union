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
    default <R> Option<R> flatMap(Function<T, Option<R>> func) {
        return inner().flatMap(func);
    }

    @Override
    default Stream<T> stream() {
        return inner().stream();
    }

    @Override
    default Option<T> orElse(Supplier<Option<T>> other) {
        return inner().orElse(other);
    }

    @Override
    default <R> Option<R> flatten() {
        return inner().flatten();
    }
}
