package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void GET_메서드를_파싱한다() {
        final HttpRequest request = HttpRequest.from("GET /index.html HTTP/1.1");

        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getPath()).isEqualTo("/index.html");
    }

    @Test
    void POST_메서드를_파싱한다() {
        final HttpRequest request = HttpRequest.from("POST /register HTTP/1.1");

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/register");
    }
}
