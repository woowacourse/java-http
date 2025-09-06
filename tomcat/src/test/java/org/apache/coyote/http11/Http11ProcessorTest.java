package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        String output = socket.output();

        // then
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: 12"),
                () -> assertThat(output).endsWith("Hello world!")
        );
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String output = socket.output();
        byte[] bodyBytes = Files.readAllBytes(new File(resource.getFile()).toPath());

        // then
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + bodyBytes.length),
                () -> assertThat(output).endsWith(body)
        );
    }
}
