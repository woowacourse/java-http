package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    void readsUtf8BodyWithoutConsumingNextRequest() throws IOException {
        final String body = "name=카키&email=test%40example.com";
        final String raw = "POST /register?source=study HTTP/1.1\r\n"
                + "content-length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n"
                + "content-type: application/x-www-form-urlencoded;charset=UTF-8\r\n\r\n"
                + body + "GET /next HTTP/1.1\r\n\r\n";
        final var input = input(raw);

        final HttpRequest request = HttpRequest.read(input).orElseThrow();

        assertThat(request.getMethod()).isEqualTo("POST");
        assertThat(request.getPath()).isEqualTo("/register");
        assertThat(request.getParameter("source")).isEqualTo("study");
        assertThat(request.getParameter("name")).isEqualTo("카키");
        assertThat(request.getParameter("email")).isEqualTo("test@example.com");
        assertThat(request.getHeader("CONTENT-LENGTH")).isEqualTo("36");
        assertThat(HttpRequest.read(input).orElseThrow().getPath()).isEqualTo("/next");
    }

    @Test
    void rejectsTruncatedBody() {
        assertThatThrownBy(() -> HttpRequest.read(input("POST / HTTP/1.1\r\nContent-Length: 10\r\n\r\nabc")))
                .isInstanceOf(IOException.class).hasMessage("Incomplete request body");
    }

    @Test
    void returnsEmptyForClosedConnection() throws IOException {
        assertThat(HttpRequest.read(input(""))).isEmpty();
    }

    @Test
    void doesNotInterpretJsonAsFormParameters() throws IOException {
        final String body = "{\"value\":\"a=b&c=d\"}";
        final HttpRequest request = HttpRequest.read(input("POST / HTTP/1.1\r\n"
                + "Content-Type: application/json\r\nContent-Length: " + body.length()
                + "\r\n\r\n" + body)).orElseThrow();
        assertThat(request.getBody()).isEqualTo(body);
        assertThat(request.getParameter("c")).isNull();
    }

    @Test
    void parsesEncodedAndEmptyQueryValues() throws IOException {
        final HttpRequest request = HttpRequest.read(input(
                "GET /search?q=hello+world&token=a%3Db&empty= HTTP/1.1\r\n\r\n")).orElseThrow();
        assertThat(request.getParameter("q")).isEqualTo("hello world");
        assertThat(request.getParameter("token")).isEqualTo("a=b");
        assertThat(request.getParameter("empty")).isEmpty();
    }

    private ByteArrayInputStream input(final String raw) {
        return new ByteArrayInputStream(raw.getBytes(StandardCharsets.UTF_8));
    }
}
