package result;

public class UnwrappedOkOnError extends RuntimeException {
    public UnwrappedOkOnError(Throwable e) {
        super(e);
    }

    public UnwrappedOkOnError(String reason, Throwable e) {
        super(reason, e);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UnwrappedOkOnError
            && ((UnwrappedOkOnError) other).getMessage().equals(getMessage());
    }
}
