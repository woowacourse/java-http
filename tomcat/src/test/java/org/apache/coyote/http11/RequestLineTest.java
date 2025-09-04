package org.apache.coyote.http11;


import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void parse() {
        // given
        final String requestLineString = "GET /index.html HTTP/1.1";

        // when
        final RequestLine requestLine = new RequestLine(requestLineString);

        // then
        assertAll(
                () -> org.assertj.core.api.Assertions.assertThat(requestLine.getMethod()).isEqualTo("GET"),
                () -> org.assertj.core.api.Assertions.assertThat(requestLine.getPath()).isEqualTo("/index.html"),
                () -> org.assertj.core.api.Assertions.assertThat(requestLine.getProtocol()).isEqualTo("HTTP/1.1")
        );
    }
}
