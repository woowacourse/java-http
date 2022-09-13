package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import org.apache.coyote.http11.Http11Processor;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("경로를 설정하지 않고 요청을 보내면 기본 지정 응답을 반환한다.")
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        Assertions.assertAll(
                () -> assertThat(socket.output()).contains("HTTP/1.1 200 OK "),
                () -> assertThat(socket.output()).contains("Content-Type: text/html;charset=utf-8 "),
                () -> assertThat(socket.output()).contains("Content-Length: 12 "),
                () -> assertThat(socket.output()).contains(""),
                () -> assertThat(socket.output()).contains("Hello world!")
        );
    }

    @Test
    @DisplayName("index.html 파일에 대해 요청을 받으면 index.html 파일을 반환한다.")
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

        Assertions.assertAll(
                () -> assertThat(socket.output()).contains("HTTP/1.1 200 OK "),
                () -> assertThat(socket.output()).contains("Content-Type: text/html;charset=utf-8 "),
                () -> assertThat(socket.output()).contains("Content-Length: 5670 "),
                () -> assertThat(socket.output()).contains(""),
                () -> assertThat(socket.output()).contains(new String(Files.readAllBytes(new File(resource.getFile()).toPath())))
        );
    }

    @Test
    @DisplayName("css 파일에 대해 요청을 받으면 css 파일을 반환한다.")
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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
        var expected =
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("js 파일에 대해 요청을 받으면 js 파일을 반환한다.")
    void js() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        var expected =
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("로그인 시 유효하지 않은 쿠키값이면 login.html을 보여준다.")
    void invalidLoginRequest() throws IOException {
        // given
        final SessionManager sessionManager = new SessionManager();
        final Session session = new Session("sessionId");
        sessionManager.add(session);
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */*",
                "Cookie: JSESSIONID=invalid ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final byte[] content = Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
        var expected = new String(content);

        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("올바른 /login 요청시 Status 302, index.html 반환한다.")
    void validLoginRequest() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final byte[] content = Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
        var expected = new String(content);
        assertThat(socket.output()).contains(List.of("302", expected));
    }

    @Test
    @DisplayName("/register로 요청이 들어오면 회원가입 페이지 register.html를 반환한다")
    void requestRegister() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: */*",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final byte[] content = Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
        var expected = new String(content);

        assertThat(socket.output()).contains(expected);
    }
}
