package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("RequestLine 클래스의")
class RequestLineTest {

    @Nested
    @DisplayName("생성자는")
    class Constructor {

        @Test
        @DisplayName("주어진 문자열로 객체를 생성한다.")
        void success() {
            // given
            final String line = "GET /index.html HTTP/1.1";

            // when
            final RequestLine requestLine = RequestLine.from(line);

            // then
            assertAll(
                    () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET),
                    () -> assertThat(requestLine.getRequestUri().getPath()).isEqualTo("/index.html"),
                    () -> assertThat(requestLine.getRequestUri().getParams()).isEmpty(),
                    () -> assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1")
            );
        }

        @Test
        @DisplayName("올바르지 않은 RequestLine 형식인 경우 예외를 던진다.")
        void invalidLength_ExceptionThrown() {
            // given
            final String line = "/index.html HTTP/1.1";

            // when & then
            assertThatThrownBy(() -> RequestLine.from(line))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("올바른 RequestLine 형식이 아닙니다.");
        }
    }
}
