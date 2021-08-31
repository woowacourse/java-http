package nextstep.jwp.constants;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class HttpMethodTest {

    @DisplayName("http method 는 대소문자를 구분하지 않는다.")
    @ParameterizedTest
    @CsvSource({"Get", "GET", "gEt", "get"})
    void insensitiveCase(String value) {
        HttpMethod httpMethod = HttpMethod.findHttpMethod(value);

        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }
}