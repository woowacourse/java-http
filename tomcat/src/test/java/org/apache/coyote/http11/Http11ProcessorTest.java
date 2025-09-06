package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import support.StubSocket;

@ExtendWith(SoftAssertionsExtension.class)
class Http11ProcessorTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expectedBody = "Hello world!";

        softly.assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("Content-Length: " + expectedBody.length() + "\r\n")
                .contains("\r\n\r\n")
                .endsWith(expectedBody);
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
        final String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String actualResponse = socket.output();

        softly.assertThat(actualResponse)
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("Content-Length: " + expectedBody.getBytes().length + "\r\n")
                .contains("\r\n\r\n")
                .endsWith(expectedBody);
    }
}
