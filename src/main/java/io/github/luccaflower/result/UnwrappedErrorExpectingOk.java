package io.github.luccaflower.result;

public class UnwrappedErrorExpectingOk extends RuntimeException {
    public UnwrappedErrorExpectingOk(Throwable e) {
        super(e);
    }

    public UnwrappedErrorExpectingOk(String reason, Throwable e) {
        super(reason, e);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UnwrappedErrorExpectingOk
            && ((UnwrappedErrorExpectingOk) other).getMessage().equals(getMessage());
    }

    @Override
    public int hashCode() {
        return 31 * (getCause() != null
            ? getCause().hashCode()
            : getMessage().hashCode());
    }
}
