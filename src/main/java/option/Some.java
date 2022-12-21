package option;


import java.util.function.*;

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
    public <R> Option<R> flatMap(Function<? super T, ? extends Option<R>> func) {
        return func.apply(something);
    }


    @Override
    public Option<T> orElse(Supplier<? extends Option<T>> other) {
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

    @Override
    public int hashCode() {
        return 9 * something.hashCode();
    }
}
