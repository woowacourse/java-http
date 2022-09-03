package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.ArgumentResolver;
import org.junit.jupiter.api.Test;

class ArgumentResolverTest {

    @Test
    void Query_String을_Map으로_바인딩한다() {
        // given
        final var queryString = "account=gugu&password=password";
        final var actual = Map.of("account", "gugu", "password", "password");

        // when
        final Map<String, String> expected = ArgumentResolver.resolve(queryString);

        // then
        assertThat(actual).isEqualTo(expected);
    }

}