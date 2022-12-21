package result;

public class UnwrappedOkExpectingError extends RuntimeException {
    public UnwrappedOkExpectingError() {
        super();
    }

    public UnwrappedOkExpectingError(String reason) {
        super(reason);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UnwrappedOkExpectingError
            && ((UnwrappedOkExpectingError) other).getMessage().equals(getMessage());
    }

    @Override
    public int hashCode() {
        return 47 * getMessage().hashCode();
    }
}
