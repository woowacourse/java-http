package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void resourceNotFound() {
        final var httpRequest = String.join("\r\n",
                "GET /missing.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final var expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 13 ",
                "",
                "404 Not Found");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void staticResourceIsSentWithoutTextConversion() throws IOException {
        final var httpRequest = String.join("\r\n",
                "GET /assets/img/error-404-monochrome.svg HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);
        final var resource = getClass().getClassLoader()
                .getResource("static/assets/img/error-404-monochrome.svg");
        final var body = Files.readAllBytes(new File(resource.getFile()).toPath());

        processor.process(socket);

        final var responseHeader = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.length + " ",
                "",
                "");
        final var expectedOutput = new ByteArrayOutputStream();
        expectedOutput.writeBytes(responseHeader.getBytes(StandardCharsets.UTF_8));
        expectedOutput.writeBytes(body);

        assertThat(socket.outputBytes()).isEqualTo(expectedOutput.toByteArray());
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
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        final var httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final var resource = getClass()
                .getClassLoader()
                .getResource("static/css/styles.css");
        final var body = Files.readString(
                new File(resource.getFile()).toPath(),
                StandardCharsets.UTF_8
        );

        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
        final var httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        final var resource = getClass().getClassLoader().getResource("static/login.html");
        final var body = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);

        assertThat(socket.output()).isEqualTo(expected);
    }

}
