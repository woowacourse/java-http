package org.apache.http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpMethodTest {

    @Test
    void 문자열이_HttpMethod로_시작한다면_true를_반환한다() {
        // given
        final boolean result = HttpMethod.isStartWith("GET /index.html");
        // when, then
        assertThat(result).isTrue();
    }

    @Test
    void 문자열이_HttpMethod로_시작하지_않는다면_false를_반환한다() {
        // given
        final boolean result = HttpMethod.isStartWith("CORINNE /index.html");
        // when, then
        assertThat(result).isFalse();
    }
}
