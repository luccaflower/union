import org.junit.jupiter.api.*;

import static matchers.Matchers.throwsA;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UnionTest {
    @Test
    public void throwsWhenUnwrappingNone() {
        Option<?> none = new None();
        assertThat(none::unwrap, throwsA(UnwrappedNone.class));
    }

    @Test
    public void returnsObjectWhenUnwrappingSome() {
        Option<String> something = new Some<>("something");
        assertThat(something.unwrap(), is("something"));
    }

    @Test
    public void noneStreamReturnsEmptyStream() {

    }
}
