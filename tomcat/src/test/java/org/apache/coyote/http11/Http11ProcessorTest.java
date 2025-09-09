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
        String result = socket.output();

        assertThat(result).startsWith("HTTP/1.1 200 OK \r\n");
        assertThat(result).contains("Content-Type: text/html;charset=utf-8 \r\n");
        assertThat(result).contains("Content-Length: 12 \r\n");
        assertThat(result).endsWith("Hello world!");
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
        String result = socket.output();
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(result).startsWith("HTTP/1.1 200 OK \r\n");
        assertThat(result).contains("Content-Type: text/html;charset=utf-8 \r\n");
        assertThat(result).contains("Content-Length: 5564 \r\n");
        assertThat(result).endsWith(responseBody);
    }
}
