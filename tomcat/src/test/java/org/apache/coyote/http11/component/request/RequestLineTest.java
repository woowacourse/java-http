package org.apache.coyote.http11.component.request;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("요청 라인을 생성한다.")
    void generate_request_line() {
        // given
        final var plaintext = "GET /index.html HTTP/1.1";

        // when & then
        assertThatCode(() -> new RequestLine(plaintext))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("올바르지 않는 요청 라인 생성 시 예외를 발생한다.")
    void throw_exception_when_generate_invalid_request_line() {
        // given
        final var plaintext = "GET /index.html HTTP/1.1 Cheese";

        // when & then
        assertThatThrownBy(() -> new RequestLine(plaintext))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
