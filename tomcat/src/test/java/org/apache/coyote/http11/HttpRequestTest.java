package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void parsesRequestLinePathAndHeaders() throws IOException {
        String rawRequest = String.join("\r\n",
                "GET /search?q=java HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        HttpRequest request = HttpRequest.parse(
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8))
        );

        assertThat(request.requestLine().method()).isEqualTo("GET");
        assertThat(request.requestLine().version()).isEqualTo("HTTP/1.1");
        assertThat(request.requestLine().path()).isEqualTo("/search");
        assertThat(request.headers().get("host")).isEqualTo("localhost:8080");
    }

    @Test
    void parsesRequestWithoutBody() throws IOException {
        String rawRequest = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        HttpRequest request = HttpRequest.parse(
                new ByteArrayInputStream(rawRequest.getBytes(StandardCharsets.UTF_8))
        );

        assertThat(request.body().content()).isEmpty();
    }
}
