package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void writesEmptyOkResponseByDefault() throws IOException {
        HttpResponse response = new HttpResponse();
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .isEqualTo("HTTP/1.1 200 OK\r\nContent-Length: 0\r\n\r\n");
    }

    @Test
    void writesTextWithByteContentLength() throws IOException {
        HttpResponse response = new HttpResponse();
        response.header("Content-Type", "text/plain;charset=utf-8");
        response.body("가".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .isEqualTo("HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/plain;charset=utf-8\r\n"
                        + "Content-Length: 3\r\n\r\n가");
    }

    @Test
    void redirectPreservesCookieAndClearsPreviousBody() throws IOException {
        HttpResponse response = new HttpResponse();
        response.header("Set-Cookie", "JSESSIONID=test-id; Path=/");
        response.header("content-type", "text/plain;charset=utf-8");
        response.body("previous".getBytes(StandardCharsets.UTF_8));
        response.redirect("/index.html");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .isEqualTo("HTTP/1.1 302 Found\r\n"
                        + "Set-Cookie: JSESSIONID=test-id; Path=/\r\n"
                        + "Location: /index.html\r\n"
                        + "Content-Length: 0\r\n\r\n");
    }

    @Test
    void writesConfiguredStatusAndBody() throws IOException {
        HttpResponse response = new HttpResponse();
        response.status(409, "Conflict");
        response.header("Content-Type", "text/plain;charset=utf-8");
        response.body("duplicate".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .isEqualTo("HTTP/1.1 409 Conflict\r\n"
                        + "Content-Type: text/plain;charset=utf-8\r\n"
                        + "Content-Length: 9\r\n\r\nduplicate");
    }

    @Test
    void preservesBinaryBodyDespiteCallerMutation() throws IOException {
        byte[] body = new byte[] {0, (byte) 0xff, 10};
        HttpResponse response = new HttpResponse();
        response.body(body);
        body[0] = 42;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.writeTo(output);

        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        expected.writeBytes("HTTP/1.1 200 OK\r\nContent-Length: 3\r\n\r\n"
                .getBytes(StandardCharsets.UTF_8));
        expected.writeBytes(new byte[] {0, (byte) 0xff, 10});
        assertThat(output.toByteArray()).containsExactly(expected.toByteArray());
    }

    @Test
    void replacesHeaderRegardlessOfCase() throws IOException {
        HttpResponse response = new HttpResponse();
        response.header("Content-Type", "text/html");
        response.header("content-type", "text/plain");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .isEqualTo("HTTP/1.1 200 OK\r\n"
                        + "content-type: text/plain\r\nContent-Length: 0\r\n\r\n");
    }

    @Test
    void rejectsCallerProvidedContentLength() {
        HttpResponse response = new HttpResponse();

        assertThatIllegalArgumentException()
                .isThrownBy(() -> response.header("content-length", "999"));
    }

    @Test
    void flushesOutputWithoutClosingIt() throws IOException {
        TrackingOutputStream output = new TrackingOutputStream();
        HttpResponse response = new HttpResponse();

        response.writeTo(output);

        assertThat(output.flushed).isTrue();
        assertThat(output.closed).isFalse();
    }

    private static class TrackingOutputStream extends ByteArrayOutputStream {
        private boolean flushed;
        private boolean closed;

        @Override
        public void flush() {
            flushed = true;
        }

        @Override
        public void close() {
            closed = true;
        }
    }
}
