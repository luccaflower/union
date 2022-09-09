package matchers;

import org.hamcrest.*;
import org.junit.jupiter.api.function.*;

import java.util.*;

@SuppressWarnings("Unused")
public abstract class Matchers {

    public static Matcher<List<?>> anyItem(Matcher<?> predicate) {
        return new BaseMatcher<>() {
            @Override
            public boolean matches(Object list) {
                return ((List<?>)list).stream().anyMatch(predicate::matches);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("any item to ");
                predicate.describeTo(description);
            }
        };
    }

    public static Matcher<Executable> throwsA(Class<? extends Throwable> type) {
        return new ThrowMatcher() {
            @Override
            public boolean matches(Object action) {
                try {
                    ((Executable)action).execute();
                    thrown = none();
                    return false;
                } catch (Throwable e) {
                    thrown = new ThrowableDescription(e);
                    return e.getClass() == type;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(type.getSimpleName() + " to be thrown");
            }

        };
    }

    public static Matcher<Executable> throwsAn(Class<? extends Throwable> type) {
        return throwsA(type);
    }

    public static Matcher<Executable> doesNotThrow() {
        return new ThrowMatcher() {
            @Override
            public boolean matches(Object action) {
                try {
                    ((Executable)action).execute();
                    return true;
                } catch (Throwable e) {
                    thrown = new ThrowableDescription(e);
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText(none() + " to be thrown");
            }
        };
    }

    public static Matcher<String> containsAllStrings(String... substrings) {
        return new BaseMatcher<>() {
            @Override
            public boolean matches(Object string) {
                return Arrays.stream(substrings)
                    .allMatch(substring -> ((String)string).contains(substring));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("contain all substrings: " + Arrays.toString(substrings));
            }
        };
    }

    private static class ThrowableDescription implements SelfDescribing {
        private final Throwable e;

        public ThrowableDescription(Throwable e) {
                                               this.e = e;
                                                          }

        @Override
        public void describeTo(Description description) {
            description.appendText(e.getClass().getSimpleName() + " was thrown");
        }
    }

    private static class None extends Throwable {}
    private static abstract class ThrowMatcher extends BaseMatcher<Executable> {
        protected SelfDescribing thrown;

        @Override
        public void describeMismatch(Object ignored, Description description) {
            description.appendDescriptionOf(thrown);
        }

        protected static ThrowableDescription none() {
            return new ThrowableDescription(new None());
        }
    }
}
