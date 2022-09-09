package option;

public class UnwrappedNone extends RuntimeException {
    public UnwrappedNone() {
        super();
    }

    public UnwrappedNone(String reason) {
        super(reason);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UnwrappedNone
            && ((UnwrappedNone) other).getMessage().equals(getMessage());
    }
}
