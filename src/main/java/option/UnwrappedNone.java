package option;

public class UnwrappedNone extends RuntimeException {
    public UnwrappedNone() {
        super();
    }

    public UnwrappedNone(String reason) {
        super(reason);
    }
}
