package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {

    @Test
    void HTTP_요청의_첫_줄을_파싱한다() {
        final RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo("GET");
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
        assertThat(requestLine.getRequestTarget()).isEqualTo("/index.html");
        assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void Query_String을_제외한_Path를_반환한다() {
        final RequestLine requestLine = new RequestLine("GET /login?account=gugu HTTP/1.1");

        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getRequestTarget())
                .isEqualTo("/login?account=gugu");
    }

    @Test
    void Request_Target에서_Query_String을_추출한다() {
        final RequestLine requestLine = new RequestLine(
                "GET /search?keyword=java&page=1 HTTP/1.1"
        );

        assertThat(requestLine.getPath()).isEqualTo("/search");
        assertThat(requestLine.getQueryString())
                .isEqualTo("keyword=java&page=1");
    }

    @Test
    void Query_String이_없으면_빈_문자열을_반환한다() {
        final RequestLine requestLine = new RequestLine("GET /index.html HTTP/1.1");

        assertThat(requestLine.getQueryString()).isEmpty();
    }

    @Test
    void 요청_라인이_세_부분이_아니면_예외가_발생한다() {
        assertThatThrownBy(() -> new RequestLine("POST /login"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 HTTP Request Line");
    }
}
