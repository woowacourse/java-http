package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {

    @Test
    void 요청_라인을_파싱한다() {
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo("GET");
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
        assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void URI에서_쿼리_파라미터를_분리한다() {
        final RequestLine requestLine = RequestLine.from(
                "GET /login?account=gugu&password=password HTTP/1.1"
        );

        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getQueryParams())
                .containsEntry("account", "gugu")
                .containsEntry("password", "password");
    }

    @Test
    void 쿼리_파라미터가_없으면_빈_맵을_반환한다() {
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

        assertThat(requestLine.getQueryParams()).isEmpty();
    }

    @Test
    void 잘못된_요청_라인이면_예외가_발생한다() {
        assertThatThrownBy(() -> RequestLine.from("GET /index.html"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
