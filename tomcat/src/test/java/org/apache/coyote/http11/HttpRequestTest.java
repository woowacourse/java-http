package org.apache.coyote.http11;

import java.io.ByteArrayInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpRequestTest {

    @Test
    void readsUtf8BodyWithoutConsumingFollowingBytes() throws IOException {
        String body = "가😀\r\nabc";
        InputStream input = request(body.getBytes(StandardCharsets.UTF_8).length, body + "NEXT");

        HttpRequest request = HttpRequest.from(input);

        assertThat(request.getBody()).isEqualTo(body);
        assertThat(input.readAllBytes()).isEqualTo("NEXT".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void readsBodyWhenInputArrivesInSmallChunks() throws IOException {
        String body = "가나다😀";
        InputStream input = new FilterInputStream(request(body.getBytes(StandardCharsets.UTF_8).length, body)) {
            @Override
            public int read(byte[] buffer, int offset, int length) throws IOException {
                return super.read(buffer, offset, Math.min(length, 2));
            }
        };

        assertThat(HttpRequest.from(input).getBody()).isEqualTo(body);
    }

    @Test
    void rejectsBodyShorterThanContentLength() {
        InputStream input = request(4, "가");

        assertThatThrownBy(() -> HttpRequest.from(input))
                .isInstanceOf(IOException.class)
                .hasMessage("예상보다 짧음");
    }

    @Test
    void readsEmptyBodyWithoutReadingPastRequest() throws IOException {
        InputStream input = request(0, "NEXT");

        assertThat(HttpRequest.from(input).getBody()).isEmpty();
        assertThat(input.readAllBytes()).isEqualTo("NEXT".getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void defaultsToEmptyBodyWithoutContentLength() throws IOException {
        InputStream input = new ByteArrayInputStream("GET / HTTP/1.1\r\n\r\n".getBytes(StandardCharsets.UTF_8));

        assertThat(HttpRequest.from(input).getBody()).isEmpty();
    }

    @Test
    void rejectsNegativeContentLength() {
        assertThatThrownBy(() -> HttpRequest.from(request(-1, "")))
                .isInstanceOf(IOException.class);
    }

    private InputStream request(int contentLength, String body) {
        String request = "POST /login HTTP/1.1\r\nContent-Length: " + contentLength + "\r\n\r\n" + body;
        return new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8));
    }
}
