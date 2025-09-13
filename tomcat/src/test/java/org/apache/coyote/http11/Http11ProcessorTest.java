package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
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
                "Content-Type: text/html; charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void post_login() throws  IOException {
        // given
        final String body = "account=gugu&password=password";
        final int contentLength = body.getBytes(StandardCharsets.UTF_8).length;
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
                "Content-Length: " + contentLength,
                "",
                body
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String output = socket.output();

        // Check status line and headers
        assertThat(output).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(output).contains("Content-Type: text/html; charset=utf-8\r\n");
        assertThat(output).contains("Content-Length: 5564\r\n");
        assertThat(output).contains("Set-Cookie: JSESSIONID=");

        // Check body content
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(output).endsWith(expectedBody);
    }

    @Test
    void post_login_not_found() throws  IOException {
        // given
        final String body = "account=gugu&password=passwordasd";
        final int contentLength = body.getBytes(StandardCharsets.UTF_8).length;

        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Type: application/x-www-form-urlencoded; charset=UTF-8",
                "Content-Length: " + contentLength,
                "",
                body
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        var expected = "HTTP/1.1 302 Found\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: 2426\r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
