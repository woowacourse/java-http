package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void getHeader_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                ""
        );

        final BufferedReader reader = new BufferedReader(
                new StringReader(httpRequest)
        );

        // when
        final HttpRequest request = new HttpRequest(reader);

        // then
        assertThat(request.getHeader("Host"))
                .isEqualTo("localhost:8080");
        assertThat(request.getHeader("Connection"))
                .isEqualTo("keep-alive");
    }

    @Test
    void requestLine_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /users?id=1 HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        final BufferedReader reader = new BufferedReader(
                new StringReader(httpRequest)
        );

        // when
        final HttpRequest request = new HttpRequest(reader);

        // then
        assertThat(request.getMethod()).isEqualTo("GET");
        assertThat(request.getUri()).isEqualTo("/users?id=1");
        assertThat(request.getPath()).isEqualTo("/users");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
    }
}