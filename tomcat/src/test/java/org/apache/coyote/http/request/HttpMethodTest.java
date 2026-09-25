package org.apache.coyote.http.request;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpMethodTest {

    @ParameterizedTest
    @CsvSource({
            "GET, GET",
            "POST, POST",
            "PUT, PUT",
            "PATCH, PATCH",
            "DELETE, DELETE"
    })
    void HTTP_메서드를_생성한다(final String source, final HttpMethod expected) {
        assertThat(HttpMethod.from(source)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "get",      // 소문자
            "HEAD",     // 지원하지 않는 메서드
            " GET",     // 공백 포함
            ""          // 빈 문자열
    })
    void 지원하지_않는_HTTP_메서드는_예외가_발생한다(final String source) {
        assertThatThrownBy(() -> HttpMethod.from(source))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
