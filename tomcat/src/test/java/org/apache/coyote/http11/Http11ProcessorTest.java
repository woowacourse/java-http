package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;
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

        // then
        String actual = socket.output();
        assertThat(actual).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!"
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

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        byte[] fileBytes = Files.readAllBytes(
                new File(Objects.requireNonNull(resource).getFile())
                        .toPath()
        );

        String actual = socket.output();
        assertThat(actual).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: " + fileBytes.length,
                new String(fileBytes)
        );
    }
}
