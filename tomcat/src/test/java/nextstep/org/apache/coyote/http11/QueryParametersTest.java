package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.RequestParameters;
import org.junit.jupiter.api.Test;

public class QueryParametersTest {

    @Test
    void queryParameter_값을_찾는다() {
        // given
        final String uri = "/login?account=gugu&password=password";

        // when
        final RequestParameters queryParameters = RequestParameters.of(uri);

        // then
        assertAll(() -> {
                    assertThat(queryParameters.get("account")).isEqualTo("gugu");
                    assertThat(queryParameters.get("password")).isEqualTo("password");
                }
        );
    }
}
