package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
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
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
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
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);


        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String cssFile = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + cssFile.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                cssFile);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void js() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);


        // then
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        final String cssFile = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/javascript;charset=utf-8 ",
                "Content-Length: " + cssFile.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                cssFile);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
        // given
        final String body = "account=gugu&password=password";
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Connection: keep-alive ",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n")
                .contains("Set-Cookie: JSESSIONID=")
                .endsWith("Content-Length: 0 \r\n\r\n");
    }

    @Test
    void getLoginReturnsLoginView() throws IOException {
        assertGetReturnsView("/login", "static/login.html");
    }

    @Test
    void getRegisterReturnsRegisterView() throws IOException {
        assertGetReturnsView("/register", "static/register.html");
    }

    @Test
    void postRegisterCreatesUserAndRedirects() {
        // given
        final String account = "register-" + UUID.randomUUID();
        final String body = "account=" + account + "&password=password&email=test@example.com";
        final var socket = new StubSocket(String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n", "Set-Cookie: JSESSIONID=");
        assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
    }

    @Test
    void unsupportedMethodDoesNotExecuteLogin() {
        // given
        final String body = "account=gugu&password=password";
        final var socket = new StubSocket(String.join("\r\n",
                "PUT /login HTTP/1.1",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 400 Bad Request \r\n")
                .doesNotContain("Location:", "Set-Cookie:");
    }

    private void assertGetReturnsView(String path, String resourcePath) throws IOException {
        // given
        final var socket = new StubSocket("GET " + path + " HTTP/1.1\r\n\r\n");
        final var processor = new Http11Processor(socket);
        final String expectedBody;
        try (var resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            expectedBody = new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .contains("Content-Type: text/html;charset=utf-8 \r\n")
                .doesNotContain("Location:", "Set-Cookie:")
                .endsWith("\r\n\r\n" + expectedBody);
    }
}
