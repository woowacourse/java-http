package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestTest {

    @Test
    void GET_요청을_파싱한다() throws Exception {
        final var request = String.join("\r\n",
                "GET /login?account=gugu HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""
        );

        final var httpRequest = new HttpRequest(inputStream(request));

        assertThat(httpRequest.getMethod()).isEqualTo("GET");
        assertThat(httpRequest.getPath()).isEqualTo("/login");
        assertThat(httpRequest.getHeader("Host")).isEqualTo("localhost:8080");
        assertThat(httpRequest.getParameter("account")).isEqualTo("gugu");
    }

    @Test
    void POST_본문을_파싱한다() throws Exception {
        final var body = "account=gugu&password=password";
        final var request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "",
                body
        );

        final var httpRequest = new HttpRequest(inputStream(request));

        assertThat(httpRequest.getParameter("account")).isEqualTo("gugu");
        assertThat(httpRequest.getParameter("password")).isEqualTo("password");
    }

    private ByteArrayInputStream inputStream(final String request) {
        return new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
    }
}
