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
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]+\\r\\n")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("Content-Length: 12")
                .endsWith("Hello world!");
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
        final String expectedBody = new String(
                Files.readAllBytes(new File(resource.getFile()).toPath())
        );

        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]+\\r\\n")
                .contains("Content-Type: text/html; charset=utf-8")
                .contains("Content-Length: 5564")
                .endsWith(expectedBody);
    }
}
