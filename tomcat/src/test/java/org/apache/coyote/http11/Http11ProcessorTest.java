package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

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
    void method_not_allowed_test() {
        // given
        final String httpRequest= String.join("\r\n",
                "PUT /index HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        // Expect 405 Method Not Allowed without body
        var expectedStart = "HTTP/1.1 405 Method Not Allowed\r\n";
        assertThat(socket.output()).startsWith(expectedStart);
    }

    @Test
    void post_to_index_is_method_not_allowed() {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /index HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 0",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expectedStart = "HTTP/1.1 405 Method Not Allowed\r\n";
        assertThat(socket.output()).startsWith(expectedStart);
    }
}
