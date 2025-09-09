package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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
                "Content-Type: text/html;charset=utf-8",
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
        final String body = Files.readString(new File(resource.getFile()).toPath())
                .replace("\r\n", "\n").replace("\n", "\r\n");

        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n" +
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void malformed_request_line_results_in_400() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET",
                "Host: localhost:8080",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/400.html");
        final String body = Files.readString(new File(resource.getFile()).toPath())
                .replace("\r\n", "\n").replace("\n", "\r\n");

        String expected = String.join("\r\n",
                "HTTP/1.1 400 Bad Request",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
