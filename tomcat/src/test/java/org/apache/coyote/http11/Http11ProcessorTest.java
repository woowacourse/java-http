package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.connector.CoyoteAdapter;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var adapter = new CoyoteAdapter();
        final var processor = new Http11Processor(socket, adapter);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
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
        final var adapter = new CoyoteAdapter();
        final Http11Processor processor = new Http11Processor(socket, adapter);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected1 = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n";
        var expected2 = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected1);
        assertThat(socket.output()).contains(expected2);
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var adapter = new CoyoteAdapter();
        final Http11Processor processor = new Http11Processor(socket, adapter);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected1 = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n";
        var expected2 = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected1);
        assertThat(socket.output()).contains(expected2);
    }

    @Test
    void methodNotAllowed() {
        // given
        final String httpRequest = String.join("\r\n",
                "PUT /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var adapter = new CoyoteAdapter();
        final Http11Processor processor = new Http11Processor(socket, adapter);

        // when
        processor.process(socket);

        // then
        var expected1 = "HTTP/1.1 405 Method Not Allowed \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n";
        assertThat(socket.output()).contains(expected1);
    }

    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /notFound HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var adapter = new CoyoteAdapter();
        final Http11Processor processor = new Http11Processor(socket, adapter);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        var expected1 = "HTTP/1.1 404 Not Found \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n";
        var expected2 = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expected1);
        assertThat(socket.output()).contains(expected2);
    }
}
