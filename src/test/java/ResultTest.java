import dummy.*;
import org.junit.jupiter.api.*;
import result.*;
import result.Unit;

import java.util.*;
import java.util.stream.*;

import static matchers.Matchers.throwsAn;
import static option.Option.none;
import static option.Option.some;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static result.Result.err;
import static result.Result.ok;

class ResultTest {
    @Test
    void unwrapOnErrorThrowsRuntimeException() {
        assertThat(
            () -> err(new Exception()).unwrap(),
            throwsAn(UnwrappedErrorExpectingOk.class)
        );
    }

    @Test
    void unwrapErrOnOkThrowsRuntimeException() {
        assertThat(
            () -> ok().unwrapErr(),
            throwsAn(UnwrappedOkExpectingError.class)
        );
    }

    @Test
    void unwrapOnOkReturnsValue() {
        var thing = new Dummy();
        assertThat(ok(thing).unwrap(), is(thing));
    }

    @Test
    void unwrapErrOnErrorReturnsError() {
        var e = new Exception();
        assertThat(err(e).unwrapErr(), is(e));
    }

    @Test
    void unwrapOrReturnsDefaultOnError() {
        var thing = new Dummy();
        assertThat(err(new Exception()).unwrapOr(thing), is(thing));
    }

    @Test
    void unwrapOrReturnsOkValueOnOk() {
        assertThat(ok("one").unwrapOr("other"), is("one"));
    }

    @Test
    void unwrapOrElseCallsDefaultFunctionOnError() {
        var thing = new Dummy();
        assertThat(err().unwrapOrElse(() -> thing), is(thing));
    }

    @Test
    void okToOptionIsNoneOnError() {
        assertThat(err().okToOption(), is(none()));
    }

    @Test
    void okToOptionIsSomeOnOk() {
        assertThat(ok(1).okToOption(), is(some(1)));
    }

    @Test
    void errToOptionIsSomeOnError() {
        assertThat(err().errToOption(), is(some(new Result.ResultException())));
    }

    @Test
    void errToOptionIsNoneOnOk() {
        assertThat(ok().errToOption(), is(none()));
    }

    @Test
    void isOkAndIsAlwaysFalseWhenPredicateFails() {
        assertThat(ok().isOkAnd(v -> false), is(false));
    }

    @Test
    void isOkAndIsAlwaysFalseOnError() {
        assertThat(err().isOkAnd(v -> true), is(false));
    }

    @Test
    void isOkAndIsTrueOnOkAndPredicateSucceeds() {
        assertThat(ok().isOkAnd(v -> true), is(true));
    }

    @Test
    void flattenOnErrorReturnsError() {
        assertThat(err().flatten().isErr(), is(true));
    }

    @Test
    void flattenOnOkReturnsOk() {
        var thing = new Dummy();
        assertThat(ok(thing).flatten(), is(ok(thing)));
    }

    @Test
    void flattenOnNestedErrorReturnsError() {
        assertThat(ok(err()).flatten(), is(err()));
    }

    @Test
    void flattenOnNestedOkReturnsOk() {
        var thing = new Dummy();
        assertThat(ok(ok(ok(thing))).flatten(), is(ok(thing)));
    }

    @Test
    void errorMatchesToError() {
        assertThat(
            err().matches(ok -> "ok", err -> "error"),
            is("error"));
    }

    @Test
    void okMatchesToOk() {
        String result = ok().matches(
            ok -> "ok",
            err -> { throw new RuntimeException(err); }
        );
        assertThat(result, is("ok"));
    }

    @Test
    void listReducesToSingleResult() {
        assertThat(
            Stream.of(ok(1), ok(2), err())
                .reduce(Result::and).get(),
            is(err())
        );
    }

    @Test
    void listsOverDifferentTypesCompose() {
        assertThat(
            Stream.of(ok(1), ok("thing"), err())
                .reduce(Result::and).get(),
            is(err())
        );
    }

    @Test
    void flatmapComposesInnerResults() {
        assertThat(
            ok().andThen(ok -> ok(1))
                .andThen(Result::ok)
                .andThen(Result::ok)
                .<Integer, Exception>flatten()
                .flatMap(ok -> ok(ok + 1)),
            is(ok(2))
        );
    }

    @Test
    void flatmapOnErrReturnsError() {
        assertThat(
            ok().flatMap(Result::ok).flatMap(ok -> err()).flatMap(Result::ok),
            is(err())
        );
    }

    @Test
    void listOfResultsCollectsToResultOverList() {
        assertThat(
            Stream.of(ok(), ok(), ok()).collect(Result.andCollector())
                .map(List::size),
            is(ok(3)));
    }

    @Test
    void listWithErrorAndCollectsToError() {
        assertThat(
            Stream.<Result<Unit, Exception>>of(ok(), ok(), ok(), err())
                .collect(Result.andCollector()),
            is(err())
        );
    }

    @Test
    void listWithOkOrCollectsToOk() {
        Result<Integer, Exception> result = Stream.<Result<Unit, Exception>>of(ok(), err(), err())
            .collect(Result.orCollector()).map(List::size);
        assertThat(
            result,
            is(ok(1))
        );
    }

}
