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
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 12 ",
                "Content-Type: text/html;charset=utf-8 ",
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
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processer = new Http11Processor(socket);

        // when
        processer.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + getFileSize(resource) + " \r\n" +
                "Content-Type: text/css;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        assert resource != null;
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + getFileSize(resource) + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginSuccessWithQueryString() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginFailWithQueryString() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=gugu HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /401.html \r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void registerPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        assert resource != null;

        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: " + getFileSize(resource) + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 59 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com ");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    private long getFileSize(URL resource) {
        return new File(resource.getFile()).length();
    }
}
