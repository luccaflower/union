import dummy.*;
import option.*;
import org.junit.jupiter.api.*;

import java.util.*;
import java.util.stream.*;

import static matchers.Matchers.throwsA;
import static option.Option.none;
import static option.Option.some;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class OptionTest {
    @Test
    public void throwsWhenUnwrappingNone() {
        Option<?> none = none();
        assertThat(none::unwrap, throwsA(UnwrappedNone.class));
    }

    @Test
    public void returnsObjectWhenUnwrappingSome() {
        Option<String> something = some("something");
        assertThat(something.unwrap(), is("something"));
    }

    @Test
    public void noneMapsToNone() {
        Option<Dummy> nothing = none();
        assertThat(nothing.map(none -> none), is(none()));
    }

    @Test
    public void someMapsToOther() {
        Option<String> something = some("something");
        String other = "or the other";
        assertThat(something.map(some -> other).unwrap(), is("or the other"));
    }

    @Test
    public void mapOrReturnsDefaultOnNone() {
        Option<String> none = none();
        String defaultValue = "nothing was found";
        assertThat(
            none.mapOr(defaultValue, n -> "something was found"),
            is(defaultValue)
        );
    }

    @Test
    public void mapOrReturnsMappedValueOnSome() {
        Option<String> some = some("something");
        String defaultValue = "not found";
        assertThat(
            some.mapOr(defaultValue, s -> "found!"),
            is("found!")
        );
    }

    @Test
    public void noneMapOrElseReturnsDefault() {
        assertThat(
            none().mapOrElse(() -> "thing", v -> "other"),
            is("thing")
        );
    }

    @Test
    public void someIsSome() {
        assertThat(some(new Dummy()).isSome(), is(true));
    }

    @Test
    public void noneIsNotSome() {
        assertThat(none().isSome(), is(false));
    }

    @Test
    public void someIsNotNone() {
        assertThat(some(new Dummy()).isNone(), is(false));
    }

    @Test
    public void noneIsNone() {
        assertThat(none().isNone(), is(true));
    }

    @Test
    public void noneOrSomeReturnsSome() {
        assertThat(none().or(new Some<>("some")), is(some("some")));
    }

    @Test
    public void someOrNoneReturnsSome() {
        assertThat(some("some").or(none()), is(some("some")));
    }

    @Test
    public void noneOrNoneReturnsNone() {
        assertThat(none().or(none()), is(none()));
    }

    @Test
    public void someOrSomeIsFirstSome() {
        assertThat(some("one").or(some("other")), is(some("one")));
    }

    @Test
    public void someOrElseReturnsSome() {
        assertThat(some("one").orElse(() -> some("other")), is(some("one")));
    }

    @Test
    public void noneOrElseCallsElse() {
        assertThat(none().orElse(() -> some("other")), is(some("other")));
    }

    @Test
    public void noneXorNoneReturnsNone() {
        assertThat(none().xor(none()), is(none()));
    }

    @Test
    public void someXorSomeReturnsNone() {
        assertThat(some("one").xor(some("other")), is(none()));
    }

    @Test
    public void someXorNoneReturnsSome() {
        assertThat(some("one").xor(none()), is(some("one")));
    }

    @Test
    public void noneXorSomeReturnsSome() {
        assertThat(none().xor(some("other")), is(some("other")));
    }

    @Test
    public void flattenOnNoneReturnsNone() {
        assertThat(none().flatten(), is(none()));
    }

    @Test
    public void flattenOnNestedNoneReturnsNone() {
        assertThat(some(none()).flatten(), is(none()));
    }

    @Test
    public void flattenOnSomeReturnsSome() {
        var thing = new Dummy();
        assertThat(some(thing).flatten(), is(some(thing)));
    }

    @Test
    public void flattenOnNestedSomeReturnsTheInnermostSome() {
        var thing = new Dummy();
        assertThat(some(some(some(thing))).flatten(), is(some(thing)));
    }

    @Test
    public void noneMatchesToNone() {
        String result = none().matches(
            some -> "some",
            () ->  "none"
        );

        assertThat(result, is("none"));
    }

    @Test
    public void someMatchesToSome() {
        String result = some(new Dummy()).matches(
            some -> "some",
            () -> "none"
        );
        assertThat(result, is("some"));
    }

    @Test
    public void listReducesToOption() {
        assertThat(
            Stream.of(some(1), some(2), some(3), none())
                //java is silly
                .map(s -> s)
                .reduce(Option::and).get(),
            is(none())
        );
    }

    @Test
    public void listOfSomeCollectsToSomeOverList() {
        assertThat(
            Stream.<Option<Integer>>of(some(1), some(2), some(3), none())
                .collect(Option.orCollector())
                .map(List::size),
            is(some(3))
        );
    }

    @Test
    public void listWithSingleNoneAndCollectsToNone() {
        assertThat(
            Stream.<Option<Integer>>of(some(1), some(2), some(3), none())
                .collect(Option.andCollector())
                .isNone(),
            is(true)
        );
    }
}
