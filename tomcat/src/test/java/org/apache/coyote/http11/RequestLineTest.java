package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineTest {

    @Test
    void Request_Line을_파싱한다() {
        // given
        final String rawRequestLine =
                "GET /index.html HTTP/1.1";

        // when
        final RequestLine requestLine =
                RequestLine.from(rawRequestLine);

        // then
        assertThat(requestLine.getMethod())
                .isEqualTo("GET");

        assertThat(requestLine.getPath())
                .isEqualTo("/index.html");

        assertThat(requestLine.getProtocolVersion())
                .isEqualTo("HTTP/1.1");
    }

    @Test
    void URI에_Query_String이_있어도_Path만_반환한다() {
        // given
        final String rawRequestLine =
                "GET /login?account=gugu HTTP/1.1";

        // when
        final RequestLine requestLine =
                RequestLine.from(rawRequestLine);

        // then
        assertThat(requestLine.getUri())
                .isEqualTo(
                        "/login?account=gugu"
                );

        assertThat(requestLine.getPath())
                .isEqualTo("/login");
    }
}