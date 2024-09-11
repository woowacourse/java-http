package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.Test;
import org.apache.coyote.http.HttpMethod;

class RequestLineTest {

    @Test
    void RequestLine_객체를_생성한다() {
        // given
        String requestLine = "GET /index.html HTTP/1.1";

        // when
        RequestLine actual = new RequestLine(requestLine);

        // then
        assertSoftly(softly -> {
            softly.assertThat(actual.getHttpMethod()).isEqualTo(HttpMethod.GET);
            softly.assertThat(actual.getPath()).isEqualTo("/index.html");
            softly.assertThat(actual.getHttpVersion()).isEqualTo("HTTP/1.1");
        });
    }

    @Test
    void RequestLine이_비어있으면_예외가_발생한다() {
        // given
        String requestLine = null;

        // when & then
        assertThatThrownBy(() -> new RequestLine(requestLine))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Request line은 필수입니다.");
    }

    @Test
    void split된_RequestLine의_길이가_올바르지_않을_경우_예외가_발생한다() {
        // given
        String requestLine = "GET /index.html";

        // when & then
        assertThatThrownBy(() -> new RequestLine(requestLine))
            .isExactlyInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("잘못된 Request line입니다. request line: 'GET /index.html'");
    }
}
