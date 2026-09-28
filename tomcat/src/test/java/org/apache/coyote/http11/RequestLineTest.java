package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestLineTest {

    @Test
    void separatesMethodPathQueryStringAndVersion() {
        final var requestLine = new RequestLine("GET /login?account=gugu&password=p%3F HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo("GET");
        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getQueryString()).isEqualTo("account=gugu&password=p%3F");
        assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void handlesRequestWithoutQueryString() {
        final var requestLine = new RequestLine("POST /register HTTP/1.1 ");

        assertThat(requestLine.getMethod()).isEqualTo("POST");
        assertThat(requestLine.getPath()).isEqualTo("/register");
        assertThat(requestLine.getQueryString()).isEmpty();
        assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1");
    }
}
