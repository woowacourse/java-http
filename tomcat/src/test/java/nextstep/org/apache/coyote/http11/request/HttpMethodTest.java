package nextstep.org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpMethodTest {

    @DisplayName("http 메서드를 생성한다")
    @ParameterizedTest
    @CsvSource({"get, GET", "Get, GET", "post, POST", "Post, POST"})
    void createHttpMethod(final String value, final HttpMethod expected) {
        assertThat(HttpMethod.from(value)).isEqualTo(expected);
    }
}
