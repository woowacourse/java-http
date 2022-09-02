package nextstep.org.apache.coyote.http11;

import static org.apache.coyote.Constants.CRLF;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
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
        var expected = String.join(CRLF,
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() {
        // given
        final String httpRequest = String.join(CRLF,
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
        var expected = "HTTP/1.1 200 OK " + CRLF +
                "Content-Type: text/html;charset=utf-8 " + CRLF +
                "Content-Length: 5564 " + CRLF +
                CRLF +
                getBody(resource);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() {
        // given
        final String httpRequest = String.join(CRLF,
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        var expected = "HTTP/1.1 200 OK " + CRLF +
                "Content-Type: text/css;charset=utf-8 " + CRLF +
                "Content-Length: 211991 " + CRLF +
                CRLF +
                getBody(resource);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() {
        // given
        final String httpRequest = String.join(CRLF,
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = "HTTP/1.1 200 OK " + CRLF +
                "Content-Type: text/html;charset=utf-8 " + CRLF +
                "Content-Length: 3796 " + CRLF +
                CRLF +
                getBody(resource);

        assertThat(socket.output()).isEqualTo(expected);
    }

    private String getBody(URL resource) {
        try {
            if (resource == null) {
                throw new RuntimeException();
            }
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
}
