package org.apache.coyote.http11.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    @DisplayName("전달 받은 메소드로 부터 HTTP 메소드를 찾는다.")
    void from_success() {
        // given
        final String requestedMethod = "GET";

        // when
        final HttpMethod httpMethod = HttpMethod.from(requestedMethod);

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    @DisplayName("전달 받은 메소드가 지원되지 않는 HTTP 메소드면 예외가 발생한다.")
    void from_fail() {
        // given
        final String requestedMethod = "HEAD";

        // when, then
        assertThatThrownBy(() -> HttpMethod.from(requestedMethod))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("지원되지 않는 HTTP 메소드입니다.");
    }
}
