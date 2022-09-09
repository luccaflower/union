public class None extends Option<Object> {
    @Override
    public Object unwrap() {
        throw new UnwrappedNone();
    }
    public static class Nothing {}
}
