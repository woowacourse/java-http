package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIOException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void readsRequestLineAndHeaders() throws IOException {
        String message = "GET /login?next=index HTTP/1.1\r\n"
                + "Host: localhost:8080\r\n"
                + "cOoKiE: JSESSIONID=test-id\r\n\r\n";

        HttpRequest request = HttpRequest.readFrom(input(message));

        assertThat(request.method()).isEqualTo("GET");
        assertThat(request.path()).isEqualTo("/login");
        assertThat(request.httpVersion()).isEqualTo("HTTP/1.1");
        assertThat(request.header("HOST")).isEqualTo("localhost:8080");
        assertThat(request.header("Cookie")).isEqualTo("JSESSIONID=test-id");
        assertThat(request.header("missing")).isNull();
        assertThat(request.body()).isEmpty();
    }

    @Test
    void readsBodyUsingByteLength() throws IOException {
        String body = "note=가&account=gugu";
        String message = "POST /login HTTP/1.1\r\n"
                + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length
                + "\r\n\r\n" + body + "EXTRA";

        HttpRequest request = HttpRequest.readFrom(input(message));

        assertThat(request.method()).isEqualTo("POST");
        assertThat(request.body()).isEqualTo(body);
    }

    @Test
    void returnsNullWhenNoRequestArrives() throws IOException {
        assertThat(HttpRequest.readFrom(input(""))).isNull();
    }

    @Test
    void rejectsMalformedRequestLine() {
        for (String message : List.of("\r\n", "GET\r\n\r\n")) {
            assertThatIllegalArgumentException()
                    .isThrownBy(() -> HttpRequest.readFrom(input(message)));
        }
    }

    @Test
    void rejectsMalformedOrIncompleteHeaders() {
        for (String message : List.of(
                "GET / HTTP/1.1\r\nHost: localhost\r\n",
                "GET / HTTP/1.1\r\nBrokenHeader\r\n\r\n")) {
            assertThatIOException()
                    .isThrownBy(() -> HttpRequest.readFrom(input(message)));
        }
    }

    @Test
    void rejectsInvalidContentLength() {
        for (String length : List.of("-1", "invalid")) {
            String message = "POST /login HTTP/1.1\r\nContent-Length: "
                    + length + "\r\n\r\n";
            assertThatIOException()
                    .isThrownBy(() -> HttpRequest.readFrom(input(message)));
        }
    }

    @Test
    void rejectsTruncatedBody() {
        String message = "POST /login HTTP/1.1\r\nContent-Length: 5\r\n\r\nabc";

        assertThatIOException()
                .isThrownBy(() -> HttpRequest.readFrom(input(message)));
    }

    private ByteArrayInputStream input(String message) {
        return new ByteArrayInputStream(message.getBytes(StandardCharsets.UTF_8));
    }
}
