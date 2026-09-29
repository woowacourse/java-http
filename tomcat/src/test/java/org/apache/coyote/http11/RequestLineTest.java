package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void parsesRequestLine() {
        RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo("POST");
        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getProtocolVersion()).isEqualTo("HTTP/1.1");
    }
}
