package nextstep.jwp.framework.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class HttpStatusCodeTest {

    @DisplayName("정수 code 값으로 HttpStatusCode 생성")
    @ParameterizedTest
    @CsvSource({"200, OK", "201, Created", "302, Found", "400, Bad Request"})
    void create(int code, String description) {
        HttpStatusCode httpStatusCode = HttpStatusCode.from(code);
        assertThat(httpStatusCode.getCode()).isEqualTo(code);
        assertThat(httpStatusCode.getDescription()).isEqualTo(description);
    }
}
