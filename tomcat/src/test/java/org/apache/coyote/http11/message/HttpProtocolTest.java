package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpProtocolTest {

    @Test
    @DisplayName("전달 받은 프로토콜로 부터 HTTP 프로토콜을 찾는다.")
    void from_success() {
        // given
        final String requestedProtocol = "HTTP/1.1";

        // when
        final HttpProtocol httpProtocol = HttpProtocol.from(requestedProtocol);

        // then
        assertThat(httpProtocol).isEqualTo(HttpProtocol.HTTP_ONE);
    }

    @Test
    @DisplayName("전달 받은 프로토콜이 지원되지 않는 HTTP 프로토콜이면 예외가 발생한다.")
    void from_fail() {
        // given
        final String requestedProtocol = "HTTP/3";

        // when, then
        assertThatThrownBy(() -> HttpProtocol.from(requestedProtocol))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("지원되지 않는 프로토콜입니다.");
    }
}
