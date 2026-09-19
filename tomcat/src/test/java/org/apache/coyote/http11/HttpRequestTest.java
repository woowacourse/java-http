package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

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

    @Test
    void POST_요청의_헤더를_파싱한다() throws IOException {
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 65",
                "",
                "");
        final BufferedReader reader = new BufferedReader(new StringReader(httpRequest));

        final HttpRequest request = HttpRequest.from(reader);

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/register");
        assertThat(request.getHeader("Content-Type")).isEqualTo("application/x-www-form-urlencoded");
        assertThat(request.getHeader("Content-Length")).isEqualTo("65");
    }
}
