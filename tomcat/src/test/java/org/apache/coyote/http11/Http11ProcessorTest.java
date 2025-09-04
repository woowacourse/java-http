package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() throws IOException {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        if (resource == null) {
            throw new RuntimeException("Could not find static/index.html resource");
        }
        final String fileContent;
        try (var inputStream = resource.openStream()) {
            fileContent = new String(inputStream.readAllBytes());
        }
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + fileContent.length(),
                "",
                fileContent);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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
        if (resource == null) {
            throw new RuntimeException("Could not find static/index.html resource");
        }
        final String fileContent;
        try (var inputStream = resource.openStream()) {
            fileContent = new String(inputStream.readAllBytes());
        }
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: " + fileContent.length() + "\r\n" +
                "\r\n" +
                fileContent;

        assertThat(socket.output()).isEqualTo(expected);
    }
}
