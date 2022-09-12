import dummy.*;
import org.junit.jupiter.api.*;
import result.*;

import static matchers.Matchers.throwsAn;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static result.Result.err;
import static result.Result.ok;

public class ResultTest {
    @Test
    public void unwrapOnErrorThrowsRuntimeException() {
        assertThat(
            () -> err(new Exception()).unwrap(),
            throwsAn(UnwrappedErrorExpectingOk.class)
        );
    }

    @Test
    public void unwrapErrOnOkThrowsRuntimeException() {
        assertThat(
            () -> ok().unwrapErr(),
            throwsAn(UnwrappedOkExpectingError.class)
        );
    }

    @Test
    public void unwrapOnOkReturnsValue() {
        var thing = new Dummy();
        assertThat(ok(thing).unwrap(), is(thing));
    }

    @Test
    public void unwrapErrOnErrorReturnsError() {
        var e = new Exception();
        assertThat(err(e).unwrapErr(), is(e));
    }

    @Test
    public void unwrapOrReturnsDefaultOnError() {
        var thing = new Dummy();
        assertThat(err(new Exception()).unwrapOr(thing), is(thing));
    }

    @Test
    public void unwrapOrReturnsOkValueOnOk() {
        var thing = new Dummy();
        var other = new Dummy();
        assertThat(ok(thing).unwrapOr(other), is(thing));
    }

    @Test
    public void unwrapOrElseCallsDefaultFunctionOnError() {
        var thing = new Dummy();
        assertThat(err().unwrapOrElse(() -> thing), is(thing));
    }

    @Test
    public void okToOptionIsNoneOnError() {
        assertThat(err().okToOption().isNone(), is(true));
    }

    @Test
    public void okToOptionIsSomeOnOk() {
        assertThat(ok().okToOption().isSome(), is(true));
    }

    @Test
    public void errToOptionIsSomeOnError() {
        assertThat(err().errToOption().isSome(), is(true));
    }

    @Test
    public void errToOptionIsNoneOnOk() {
        assertThat(ok().errToOption().isNone(), is(true));
    }

    @Test
    public void isOkAndIsAlwaysFalseWhenPredicateFails() {
        assertThat(ok().isOkAnd(v -> false), is(false));
    }

    @Test
    public void isOkAndIsAlwaysFalseOnError() {
        assertThat(err().isOkAnd(v -> true), is(false));
    }

    @Test
    public void isOkAndIsTrueOnOkAndPredicateSucceeds() {
        assertThat(ok().isOkAnd(v -> true), is(true));
    }

    @Test
    public void flattenOnErrorReturnsError() {
        assertThat(err().flatten().isErr(), is(true));
    }

    @Test
    public void flattenOnOkReturnsOk() {
        var thing = new Dummy();
        assertThat(ok(thing).flatten().unwrap(), is(thing));
    }

    @Test
    public void flattenOnNestedErrorReturnsError() {
        assertThat(ok(err()).flatten().isErr(), is(true));
    }

    @Test
    public void flattenOnNestedOkReturnsOk() {
        var thing = new Dummy();
        assertThat(ok(ok(ok(thing))).flatten().unwrap(), is(thing));
    }
}
