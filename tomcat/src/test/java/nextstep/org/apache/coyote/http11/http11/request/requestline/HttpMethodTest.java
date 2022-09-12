package nextstep.org.apache.coyote.http11.http11.request.requestline;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.domain.request.requestline.HttpMethod;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class HttpMethodTest {

    @ParameterizedTest
    @CsvSource({"GET,GET", "POST,POST", "PUT,PUT"})
    void getHttpMethodTest(String message, HttpMethod httpMethod) {

        assertThat(HttpMethod.get(message)).isEqualTo(httpMethod);
    }
}
