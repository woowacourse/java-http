package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.Http11QueryParams;
import org.junit.jupiter.api.Test;

class Http11QueryParamsTest {

    @Test
    void of() {
        // given
        final String urlQueryParams = "id=abc&password=1234";

        // when
        final Http11QueryParams http11QueryParams = Http11QueryParams.of(urlQueryParams);

        // then
        assertAll(
                () -> assertThat(http11QueryParams.get("id")).isEqualTo("abc"),
                () -> assertThat(http11QueryParams.get("password")).isEqualTo("1234")
        );
    }
}
