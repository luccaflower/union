package option;

import java.util.function.*;

@SuppressWarnings("unused")
public interface ForwardingOption<T> extends Option<T> {
    Option<T> inner();
    @Override
    default T unwrap() {
        return inner().unwrap();
    }

    @Override
    default <R> Option<R> flatMap(Function<? super T,? extends Option<R>> func) {
        return inner().flatMap(func);
    }

    @Override
    default Option<T> orElse(Supplier<? extends Option<T>> other) {
        return inner().orElse(other);
    }

}
