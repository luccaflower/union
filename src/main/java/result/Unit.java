package result;

@SuppressWarnings("InstantiationOfUtilityClass")
public final class Unit {
    private Unit() {}
    private static final Unit unit = new Unit();
    public static Unit unit() {
        return unit;
    }

}
