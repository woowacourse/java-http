package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
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
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: text/plain ",
            "Content-Length: 12 ",
            "",
            "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 5564 ",
            "",
            "") + new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
            "GET /css/styles.css HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: text/css ",
            "Content-Length: 211991 ");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void js() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
            "GET /js/scripts.js HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: application/javascript ",
            "Content-Length: 976 ");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void notFound() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
            "GET /helloworld HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 404 NOT_FOUND ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 2426 ");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void login() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
            "GET /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Length: 3797 ",
            "Content-Type: text/html;charset=utf-8 ");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void login_with_login_fail() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
            "POST /login?account=glen&password=password HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 302 FOUND ",
            "Location: /401.html");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void login_with_login_success() {
        // given
        String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join(System.lineSeparator(),
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + requestBody.length() + " ",
            "",
            requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 302 FOUND ",
            "Location: /index.html");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void login_with_not_allowed_method() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
            "PATCH /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 405 METHOD_NOT_ALLOWED ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 2417 ",
            "Allow: GET, POST" );

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void register_with_get() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
            "GET /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Length: 4319 ",
            "Content-Type: text/html;charset=utf-8 ");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void register_with_post() {
        // given
        String requestBody = "account=gugu&password=password&email=gugu@naver.com";
        final String httpRequest = String.join(System.lineSeparator(),
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: " + requestBody.length() + " ",
            "",
            requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
            "HTTP/1.1 302 FOUND ",
            "Location: /index.html");

        assertThat(socket.output()).startsWith(expected);
    }
}
