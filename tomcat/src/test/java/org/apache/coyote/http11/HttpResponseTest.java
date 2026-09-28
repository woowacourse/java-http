package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void forwardsStaticResourceWithItsContentType() throws IOException {
        final var output = new ByteArrayOutputStream();
        final var response = new HttpResponse(output);

        response.forward("/js/scripts.js");
        response.flush();

        final var result = output.toString(StandardCharsets.UTF_8);
        assertThat(result).startsWith("HTTP/1.1 200 OK\r\nContent-Type: application/javascript;charset=utf-8\r\n");
        try (final var resource = getClass().getClassLoader().getResourceAsStream("static/js/scripts.js")) {
            assertThat(result.split("\r\n\r\n", 2)[1]).isEqualTo(new String(resource.readAllBytes(), StandardCharsets.UTF_8));
        }
    }

    @Test
    void writesStatusLineHeadersAndUtf8Body() throws IOException {
        final var output = new ByteArrayOutputStream();
        final var response = new HttpResponse(output);
        response.body("안녕");

        response.flush();

        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html;charset=utf-8\r\n"
                        + "Content-Length: 6\r\n\r\n안녕");
    }

    @Test
    void writesBinaryBodyWithoutChangingBytes() throws IOException {
        final var output = new ByteArrayOutputStream();
        final var response = new HttpResponse(output);
        final byte[] body = {0, (byte) 0xff, (byte) 0x80, 13, 10};
        response.body(body, "application/octet-stream");

        response.flush();

        assertThat(output.toString(StandardCharsets.ISO_8859_1))
                .startsWith("HTTP/1.1 200 OK\r\nContent-Type: application/octet-stream\r\nContent-Length: 5\r\n\r\n");
        assertThat(output.toByteArray()).endsWith(body);
    }

    @Test
    void redirectsWithAnEmptyBodyAndPreservesHeaders() throws IOException {
        final var output = new ByteArrayOutputStream();
        final var response = new HttpResponse(output);
        response.addHeader("Set-Cookie", "JSESSIONID=session-id; Path=/");
        response.body("previous body");

        response.sendRedirect("/index.html");
        response.flush();

        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 302 Found\r\n"
                        + "Content-Type: text/html;charset=utf-8\r\n"
                        + "Content-Length: 0\r\n"
                        + "Set-Cookie: JSESSIONID=session-id; Path=/\r\n"
                        + "Location: /index.html\r\n\r\n");
    }
}
