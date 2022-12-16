import dummy.*;
import option.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.concurrent.atomic.*;
import java.util.stream.*;

import static matchers.Matchers.throwsA;
import static option.Option.none;
import static option.Option.some;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class OptionTest {
    @Test
    void throwsWhenUnwrappingNone() {
        Option<?> none = none();
        assertThat(none::unwrap, throwsA(UnwrappedNone.class));
    }

    @Test
    void returnsObjectWhenUnwrappingSome() {
        Option<String> something = some("something");
        assertThat(something.unwrap(), is("something"));
    }

    @Test
    void noneMapsToNone() {
        Option<Dummy> nothing = none();
        assertThat(nothing.map(none -> none), is(none()));
    }

    @Test
    void someMapsToOther() {
        Option<String> something = some("something");
        String other = "or the other";
        assertThat(something.map(some -> other).unwrap(), is("or the other"));
    }

    @Test
    void mapOrReturnsDefaultOnNone() {
        Option<String> none = none();
        String defaultValue = "nothing was found";
        assertThat(
            none.mapOr(defaultValue, n -> "something was found"),
            is(defaultValue)
        );
    }

    @Test
    void mapOrReturnsMappedValueOnSome() {
        Option<String> some = some("something");
        String defaultValue = "not found";
        assertThat(
            some.mapOr(defaultValue, s -> "found!"),
            is("found!")
        );
    }

    @Test
    void noneMapOrElseReturnsDefault() {
        assertThat(
            none().mapOrElse(() -> "thing", v -> "other"),
            is("thing")
        );
    }

    @Test
    void someIsSome() {
        assertThat(some(new Dummy()).isSome(), is(true));
    }

    @Test
    void noneIsNotSome() {
        assertThat(none().isSome(), is(false));
    }

    @Test
    void someIsNotNone() {
        assertThat(some(new Dummy()).isNone(), is(false));
    }

    @Test
    void noneIsNone() {
        assertThat(none().isNone(), is(true));
    }

    @Test
    void noneOrSomeReturnsSome() {
        assertThat(none().or(some("thing")), is(some("thing")));
    }

    @Test
    void someOrNoneReturnsSome() {
        assertThat(some("some").or(none()), is(some("some")));
    }

    @Test
    void noneOrNoneReturnsNone() {
        assertThat(none().or(none()), is(none()));
    }

    @Test
    void someOrSomeIsFirstSome() {
        assertThat(some("one").or(some("other")), is(some("one")));
    }

    @Test
    void someOrElseReturnsSome() {
        assertThat(some("one").orElse(() -> some("other")), is(some("one")));
    }

    @Test
    void noneOrElseCallsElse() {
        assertThat(none().orElse(() -> some("other")), is(some("other")));
    }

    @Test
    void noneXorNoneReturnsNone() {
        assertThat(none().xor(none()), is(none()));
    }

    @Test
    void someXorSomeReturnsNone() {
        assertThat(some("one").xor(some("other")), is(none()));
    }

    @Test
    void someXorNoneReturnsSome() {
        assertThat(some("one").xor(none()), is(some("one")));
    }

    @Test
    void noneXorSomeReturnsSome() {
        assertThat(none().xor(some("other")), is(some("other")));
    }

    @Test
    void flattenOnNoneReturnsNone() {
        assertThat(none().flatten(), is(none()));
    }

    @Test
    void flattenOnNestedNoneReturnsNone() {
        assertThat(some(none()).flatten(), is(none()));
    }

    @Test
    void flattenOnSomeReturnsSome() {
        var thing = new Dummy();
        assertThat(some(thing).flatten(), is(some(thing)));
    }

    @Test
    void flattenOnNestedSomeReturnsTheInnermostSome() {
        var thing = new Dummy();
        assertThat(some(some(some(thing))).flatten(), is(some(thing)));
    }

    @Test
    void noneMatchesToNone() {
        String result = none().matches(
            some -> "some",
            () ->  "none"
        );

        assertThat(result, is("none"));
    }

    @Test
    void someMatchesToSome() {
        String result = some(new Dummy()).matches(
            some -> "some",
            () -> "none"
        );
        assertThat(result, is("some"));
    }

    @Test
    void listReducesToOption() {
        assertThat(
            Stream.of(some(1), some(2), some(3), none())
                //java is silly
                .map(s -> s)
                .reduce(Option::and).get(),
            is(none())
        );
    }

    @Test
    void listOfSomeCollectsToSomeOverList() {
        assertThat(
            Stream.<Option<Integer>>of(some(1), some(2), some(3), none())
                .collect(Option.orCollector())
                .map(List::size),
            is(some(3))
        );
    }

    @Test
    void listWithSingleNoneAndCollectsToNone() {
        assertThat(
            Stream.<Option<Integer>>of(some(1), some(2), some(3), none())
                .collect(Option.andCollector()),
            is(none())
        );
    }

    @Test
    void ifNoneRunsActionOnNone() {
        var wasRun = new AtomicBoolean(false);
        none().ifNone(() -> wasRun.set(true));
        assertThat(wasRun.get(), is(true));
    }

    @Test
    void noneAndNoneIsNone() {
        assertThat(none().and(none()), is(none()));
    }

    @Test
    void someAndNoneIsNone() {
        assertThat(some("thing").and(none()), is(none()));
    }

    @Test
    void someAndSomeIsSecondSome() {
        assertThat(some("one").and(some("other")), is(some("other")));
    }
}
