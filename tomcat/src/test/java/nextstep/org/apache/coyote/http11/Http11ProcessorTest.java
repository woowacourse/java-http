package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.common.Session;
import org.apache.coyote.http11.common.SessionManger;
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
        var expected = String.join(System.lineSeparator(), "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ", "Content-Length: 12 ", "", "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join(System.lineSeparator(), "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ", "Connection: keep-alive ", "", "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = String.join(System.lineSeparator(), "HTTP/1.1 200 OK ", "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 5564 ", "", new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
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
        var expected = String.join(System.lineSeparator(), "HTTP/1.1 200 OK ", "Content-Type: text/css ",
                "Content-Length: 211991 ", "");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void js() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: application/javascript ",
                "Content-Length: 976 ", "");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void get_login_without_cookie() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
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
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 3797 ", "");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void get_login_with_cookie() {
        // given
        Session session = new Session("oingsession");
        session.setAttribute("user", new User("gugu", "password", "hkkang@woowahan.com"));
        SessionManger manager = new SessionManger();
        manager.add(session);

        final String httpRequest = String.join(System.lineSeparator(),
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=oingsession",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 302 Found ",
                "Location: /index.html ", "");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void post_login_success() {
        // given
        String body = "account=gugu&password=password";
        final String httpRequest = String.join(System.lineSeparator(),
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Set-Cookie: ");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void post_login_failure() {
        // given
        String body = "account=oing&password=password";
        final String httpRequest = String.join(System.lineSeparator(),
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 302 Found ",
                "Location: /401.html ");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void get_register() {
        // given
        final String httpRequest = String.join(System.lineSeparator(),
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ", "Content-Length: 4319 ", "");

        assertThat(socket.output()).startsWith(expected);
    }

    @Test
    void post_register() {
        // given
        String body = "account=oing&password=pwpw&email=oing@naver.com";
        final String httpRequest = String.join(System.lineSeparator(),
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length,
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join(System.lineSeparator(),
                "HTTP/1.1 302 Found ",
                "Location: /index.html ", "", "");

        assertThat(socket.output()).startsWith(expected);
    }
}
