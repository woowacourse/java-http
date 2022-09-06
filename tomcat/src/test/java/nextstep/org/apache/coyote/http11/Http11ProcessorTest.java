package nextstep.org.apache.coyote.http11;

import static org.apache.coyote.Constants.CRLF;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("웰컴 페이지를 응답한다.")
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join(CRLF,
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("메인 페이지를 응답한다.")
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
    @DisplayName("CSS 파일을 응답한다.")
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
    @DisplayName("로그인 성공 시 메인 페이지를 응답한다.")
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
        var expected = "HTTP/1.1 302 Found " + CRLF +
                "Location: /index.html " + CRLF +
                CRLF +
                "";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 실패 시 401 페이지를 응답한다.")
    void login_401() {
        // given
        final String httpRequest = String.join(CRLF,
                "GET /login?account=gugu2&password=nono HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found " + CRLF +
                "Location: /401.html " + CRLF +
                CRLF +
                "";

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
