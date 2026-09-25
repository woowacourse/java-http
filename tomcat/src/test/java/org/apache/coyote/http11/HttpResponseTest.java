package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void calculatesContentLengthInBytes() throws IOException {
        final var response = new HttpResponse();
        response.setBody("안녕", "text/plain");
        final var output = new ByteArrayOutputStream();

        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .contains("Content-Length: 6 \r\n").endsWith("\r\n\r\n안녕");
    }

    @Test
    void preservesBinaryBody() throws IOException {
        final byte[] body = {(byte) 0xff, 0, (byte) 0x80};
        final var response = new HttpResponse();
        response.setBody(body, "application/octet-stream");
        final var output = new ByteArrayOutputStream();

        response.writeTo(output);

        final byte[] result = output.toByteArray();
        assertThat(Arrays.copyOfRange(result, result.length - body.length, result.length)).isEqualTo(body);
    }

    @Test
    void redirectClearsExistingBody() throws IOException {
        final var response = new HttpResponse();
        response.setBody("old body", "text/plain");
        response.sendRedirect("/index.html");
        final var output = new ByteArrayOutputStream();

        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n")
                .endsWith("Content-Length: 0 \r\n\r\n")
                .doesNotContain("old body", "Content-Type");
    }
    @Test
    void replacesCookiesAndPreservesAdditionalCookies() throws IOException {
        final var response = new HttpResponse();
        response.addCookie("old", "1");
        response.addCookie("old", "2");
        response.setHeader("SET-COOKIE", "theme=dark");
        response.addCookie("language", "ko");
        final var output = new ByteArrayOutputStream();

        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .contains("Set-Cookie: theme=dark \r\nSet-Cookie: language=ko \r\n")
                .doesNotContain("old=");
    }
    @Test
    void replacesHeaderRegardlessOfCaseAndCalculatesActualLength() throws IOException {
        final var response = new HttpResponse();
        response.setHeader("content-type", "text/html");
        response.setBody("hello", "text/plain");
        response.setHeader("content-length", "999");
        final var output = new ByteArrayOutputStream();

        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8))
                .contains("content-type: text/plain;charset=utf-8", "Content-Length: 5")
                .doesNotContain("text/html", "999");
    }
}
