package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void requestLine을_입력받으면_method_uri_version_으로_구분할_수_있다() {
        // given
        String value = "GET /index.html HTTP/1.1";

        // when
        RequestLine requestLine = RequestLine.of(value);

        // then
        assertThat(requestLine).extracting("method", "uri", "httpVersion")
                .containsExactly(HttpMethod.GET, HttpUri.of("/index.html"), Http11Version.of("HTTP/1.1"));
    }

    @Test
    void 잘못된_requestLine을_입력받으면_예외를_던진다() {
        // given
        String invalidValue = "GET/index.htmlHTTP/1.1";

        // when & then
        assertThatThrownBy(() -> RequestLine.of(invalidValue))
                .isInstanceOf(IndexOutOfBoundsException.class);
    }
}
