package org.apache.coyote.http11;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new SessionManager()
        );

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("200 OK");
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join(
                "\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader()
                .getResource("static/index.html");

        assertThat(socket.output()).contains("200 OK");
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join(
                "\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader()
                .getResource("static/css/styles.css");

        assertThat(socket.output()).contains("200 OK");
    }

    @Test
    void http() throws IOException {
        // given
        final String httpRequest = String.join(
                "\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        Assertions.assertDoesNotThrow(() ->
                processor.process(socket));
    }
}
