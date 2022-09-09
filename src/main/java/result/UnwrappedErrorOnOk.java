package result;

public class UnwrappedErrorOnOk extends RuntimeException {
    public UnwrappedErrorOnOk() {
        super();
    }

    public UnwrappedErrorOnOk(String reason) {
        super(reason);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof UnwrappedErrorOnOk
            && ((UnwrappedErrorOnOk) other).getMessage().equals(getMessage());
    }
}
