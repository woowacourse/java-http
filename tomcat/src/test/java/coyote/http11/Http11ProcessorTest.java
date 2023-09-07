package coyote.http11;

import org.junit.jupiter.api.Assertions;
import support.StubSocket;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();
        assertAll(
                () -> assertThat(output).contains("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Length: 12"),
                () -> assertThat(output).contains("Content-Type: text/html; charset=utf-8"),
                () -> assertThat(output).contains("Hello world!")
        );
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

        String output = socket.output();
        assertAll(
                () -> assertThat(output).contains("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Length: 5564"),
                () -> assertThat(output).contains("Content-Type: text/html; charset=utf-8"),
                () -> assertThat(output).contains(new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
                ));
    }
}
