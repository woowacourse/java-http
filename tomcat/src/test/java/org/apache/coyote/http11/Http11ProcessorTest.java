package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void 기본경로로_요청하면_Hello_world로_응답한다() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 12 ",
                "Content-Type: text/plain;charset=utf-8 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void indexhtml경로로_요청하면_html파일을_응답한다() throws IOException {
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
        final byte[] body;
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("static/index.html")) {
            body = resourceAsStream.readAllBytes();
        }
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(body);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login경로로_요청하면_html파일을_응답한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] body;
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("static/login.html")) {
            body = resourceAsStream.readAllBytes();
        }
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 3797 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(body);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 없는경로로_요청하면_404로_응답한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /jjangu HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] body;
        try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("static/404.html")) {
            body = resourceAsStream.readAllBytes();
        }
        var expected = "HTTP/1.1 404 Not Found \r\n" +
                "Content-Length: 2430 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(body);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
