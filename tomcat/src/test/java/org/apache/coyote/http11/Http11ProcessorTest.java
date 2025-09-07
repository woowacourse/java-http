package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        Path path = Paths.get(getClass().getClassLoader()
                .getResource("static/css/styles.css")
                .getPath());
        int contentLength = Files.readAllBytes(path).length;
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/css;charset=utf-8\r\n" +
                "Content-Length: " + contentLength + "\r\n\r\n"+
                new String(Files.readAllBytes(path));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void js() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        Path path = Paths.get(getClass().getClassLoader()
                .getResource("static/js/scripts.js")
                .getPath());
        int contentLength = Files.readAllBytes(path).length;
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/javascript;charset=utf-8" + "\r\n" +
                "Content-Length: " + contentLength + "\r\n" +
                "\r\n"+
                new String(Files.readAllBytes(path));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
