package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(withoutSetCookie(socket.output())).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.length + " \r\n" +
                "\r\n" +
                new String(responseBody, StandardCharsets.UTF_8);

        assertThat(withoutSetCookie(socket.output())).isEqualTo(expected);

    }

    @Test
    void css() {
        // given
        final String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("Content-Type: text/css;charset=utf-8")
                .contains("@charset \"UTF-8\"")
                .doesNotContain("Hello world!");
    }

    @Test
    void login() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/login.html");
        final byte[] responseBody = Files.readAllBytes(new File(resource.getFile()).toPath());

        final String expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.length + " \r\n" +
                "\r\n" +
                new String(responseBody, StandardCharsets.UTF_8);

        assertThat(withoutSetCookie(socket.output())).isEqualTo(expected);
    }

    @Test
    void doesNotIssueSessionCookieAgain() {
        final String request = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco; JSESSIONID=existing-id",
                "",
                "");

        final var socket = new StubSocket(request);
        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).doesNotContain("Set-Cookie:");
    }

    @Test
    void returnsBadRequestForIncompleteRequestLine() {
        final var socket = new StubSocket("GET\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).isEqualTo("HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\n\r\n");
    }

    @Test
    void returnsBadRequestForInvalidContentLength() {
        final String request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: abc",
                "",
                "");
        final var socket = new StubSocket(request);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).isEqualTo("HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\n\r\n");
    }

    @Test
    void returnsBadRequestForNegativeContentLength() {
        final String request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: -1",
                "",
                "");
        final var socket = new StubSocket(request);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).isEqualTo("HTTP/1.1 400 Bad Request\r\nContent-Length: 0\r\n\r\n");
    }

    private String withoutSetCookie(final String response) {
        final String prefix = "Set-Cookie: JSESSIONID=";
        final int start = response.indexOf(prefix);
        assertThat(start).isGreaterThan(0);

        final int end = response.indexOf("\r\n", start);
        assertThat(end).isGreaterThan(start);

        final String sessionId = response.substring(start + prefix.length(), end);
        assertThat(UUID.fromString(sessionId).toString()).isEqualTo(sessionId);

        return response.substring(0, start) + response.substring(end + 2);
    }
}
