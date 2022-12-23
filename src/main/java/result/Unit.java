package result;

/**
 * The Unit class represents the void return-type in cases where an object still
 * needs to be returned. It is primarily used with the Ok-variant of the {@link Result}-type
 * to indicate a successful operation in which nothing is returned.
 */
@SuppressWarnings("InstantiationOfUtilityClass")
public final class Unit {
    private Unit() {}
    private static final Unit unit = new Unit();
    public static Unit unit() {
        return unit;
    }

}
