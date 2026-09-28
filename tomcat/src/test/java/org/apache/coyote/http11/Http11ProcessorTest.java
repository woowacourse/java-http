package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
        var expected = "HTTP/1.1 200 OK\r\n" +
            "Content-Type: text/html;charset=utf-8\r\n" +
            "Content-Length: 5564\r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginRequestIsDispatchedToLoginController() {
        // given
        String body = "account=gugu&password=password";
        String httpRequest = String.format("POST /login HTTP/1.1\r\n"
            + "Host: localhost:8080\r\n"
            + "Content-Type: application/x-www-form-urlencoded\r\n"
            + "Content-Length: %d\r\n\r\n%s", body.length(), body);
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
            .contains("HTTP/1.1 302 Found")
            .contains("Location: /index.html")
            .contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    void staticResourceRequestIsDispatchedToStaticResourceController() {
        // given
        StubSocket socket = new StubSocket("GET /401.html HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
            .contains("HTTP/1.1 200 OK")
            .contains("Content-Type: text/html;charset=utf-8");
    }
}
