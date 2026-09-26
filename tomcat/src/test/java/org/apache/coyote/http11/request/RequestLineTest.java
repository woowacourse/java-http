package org.apache.coyote.http11.request;

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
                .isEqualTo(HttpMethod.GET);

        assertThat(requestLine.getPath())
                .isEqualTo("/index.html");

        assertThat(requestLine.getProtocolVersion())
                .isEqualTo("HTTP/1.1");
    }

    @Test
    void URI에서_Path와_Query_String을_분리한다() {
        // given
        final String rawRequestLine = "GET /search?keyword=moca&page=2 HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(rawRequestLine);

        // then
        assertThat(requestLine.getUri()).isEqualTo("/search?keyword=moca&page=2");
        assertThat(requestLine.getPath()).isEqualTo("/search");
        assertThat(requestLine.getQueryString()).isEqualTo("keyword=moca&page=2");
    }

    @Test
    void 지원하지_않는_HTTP_Method는_UNKNOWN으로_파싱한다() {

        // given
        final String rawRequestLine = "PUT /index.html HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(rawRequestLine);

        // then
        assertThat(requestLine.getMethod())
                .isEqualTo(HttpMethod.UNKNOWN);
    }
}
