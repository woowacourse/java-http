package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    void parsesRequestLineAndHeaders() throws IOException {
        final var reader = requestReader(
                "GET /hello?name=codex HTTP/1.1",
                "Host: localhost:8080",
                "Accept-Language: ko"
        );

        final var request = HttpRequest.from(reader);

        assertThat(request.method()).isEqualTo("GET");
        assertThat(request.path()).isEqualTo("/hello?name=codex");
        assertThat(request.version()).isEqualTo("HTTP/1.1");
        assertThat(request.header("Host")).isEqualTo("localhost:8080");
        assertThat(request.header("accept-language")).isEqualTo("ko");
    }

    @Test
    void rejectsRequestLineWithoutMethodPathAndVersion() {
        final var reader = requestReader("GET /only-path");

        assertThatThrownBy(() -> HttpRequest.from(reader))
                .isInstanceOf(IOException.class)
                .hasMessage("Invalid request line");
    }

    @Test
    void rejectsHeaderWithoutNameValueSeparator() {
        final var reader = requestReader(
                "GET / HTTP/1.1",
                "Host localhost"
        );

        assertThatThrownBy(() -> HttpRequest.from(reader))
                .isInstanceOf(IOException.class)
                .hasMessage("Invalid request header");
    }

    private BufferedReader requestReader(final String... lines) {
        return new BufferedReader(new StringReader(String.join("\r\n", lines) + "\r\n\r\n"));
    }
}
