package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void parsesMethodPathAndProtocol() {
        final RequestLine requestLine = new RequestLine("GET /login?account=gugu HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo("GET");
        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getProtocol()).isEqualTo("HTTP/1.1");
    }

    @Test
    void rejectsInvalidRequestLine() {
        assertThatThrownBy(() -> new RequestLine("GET /login"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
