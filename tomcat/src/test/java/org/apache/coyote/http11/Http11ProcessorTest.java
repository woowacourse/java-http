package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("기본 요청이 들어오면 Hello world!를 응답한다.")
    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: 12"),
                () -> assertThat(output).endsWith("Hello world!")
        );
    }

    @DisplayName("index.html을 요청하면 index.html을 응답한다.")
    @Test
    void index() throws IOException {
        // given
        final var httpRequest = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] body = readFileBytes("static/index.html");
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + body.length),
                () -> assertThat(output).endsWith(new String(body))
        );
    }

    @DisplayName("styles.css를 요청하면 styles.css를 응답한다.")
    @Test
    void css() throws IOException {
        // given
        final var httpRequest = """
                GET /css/styles.css HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] body = readFileBytes("static/css/styles.css");
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: text/css;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + body.length),
                () -> assertThat(output).endsWith(new String(body))
        );
    }

    @DisplayName("scripts.js를 요청하면 scripts.js를 응답한다.")
    @Test
    void js() throws IOException {
        // given
        final var httpRequest = """        
                GET /js/scripts.js HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] body = readFileBytes("static/js/scripts.js");
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: application/javascript;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + body.length),
                () -> assertThat(output).endsWith(new String(body))
        );
    }

    @DisplayName("없는 페이지를 요청하면 404.html을 응답한다.")
    @Test
    void notFound() throws IOException {
        // given
        final var httpRequest = """
                GET /notfound HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] body = readFileBytes("static/404.html");
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 404 Not Found"),
                () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + body.length),
                () -> assertThat(output).endsWith(new String(body))
        );
    }

    @DisplayName("로그인 페이지를 요청하면 login.html을 응답한다.")
    @Test
    void login_page() throws IOException {
        // given
        final var httpRequest = """
                GET /login HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] body = readFileBytes("static/login.html");
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + body.length),
                () -> assertThat(output).endsWith(new String(body))
        );
    }

    @DisplayName("로그인 성공 시 /index.html로 리다이렉트한다.")
    @Test
    void login_success() {
        // given
        final String body = "account=gugu&password=password";
        final var httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.getBytes().length,
                "",
                body
        );

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 302 Found"),
                () -> assertThat(output).contains("Location: /index.html")
        );
    }

    @DisplayName("로그인 실패 시 /401.html로 리다이렉트한다.")
    @Test
    void login_fail() {
        // given
        final String body = "account=invalid&password=password";
        final var httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.getBytes().length,
                "",
                body
        );

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 302 Found"),
                () -> assertThat(output).contains("Location: /401.html")
        );
    }

    private byte[] readFileBytes(final String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }
}
