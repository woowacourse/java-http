package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("메세지의 start line 으로부터 RequestLine 을 생성한다.")
    void from_success() {
        // given
        final String startLine = "GET / HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(startLine);

        // then
        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getPath()).isEqualTo("/");
    }

    @Test
    @DisplayName("메세지의 start line 요소가 3개가 아니라면 예외가 발생한다.")
    void from_fail() {
        // given
        final String startLine = "GET /";

        // when
        assertThatThrownBy(() -> RequestLine.from(startLine))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("잘못된 HTTP 요청입니다.");
    }
}
