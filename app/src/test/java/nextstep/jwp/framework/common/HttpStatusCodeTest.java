package nextstep.jwp.framework.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpStatusCodeTest {

    @DisplayName("정수 code 값으로 HttpStatusCode 생성")
    @ParameterizedTest
    @CsvSource({"200, OK", "201, Created", "302, Found", "400, Bad Request"})
    void create(int code, String description) {
        HttpStatusCode httpStatusCode = HttpStatusCode.from(code);
        assertThat(httpStatusCode.getCode()).isEqualTo(code);
        assertThat(httpStatusCode.getDescription()).isEqualTo(description);
    }

    @DisplayName("존재하지 않는 정수 code 로 HttpStatusCode 생성")
    @Test
    void createWithInvalidCode() {
        int undefinedCode = 199;
        assertThatThrownBy(() -> HttpStatusCode.from(undefinedCode))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessageContaining("서버에서 지원하는 HttpStatusCode 가 아닙니다");
    }
}
