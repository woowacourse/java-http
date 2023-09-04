package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.response.header.Header;
import org.apache.coyote.http11.response.header.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Http11ProcessorTest {

    @DisplayName("웰컴 페이지를 내려준다")
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
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("index 페이지를 내려준다")
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
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("css 파일을 반환한다")
    @Test
    void css() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: text/css,*/*;q=0.1 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("text/css;");
    }

    @DisplayName("js 파일을 반환한다")
    @Test
    void js() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Accept: text/javascript,*/*;q=0.1 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("text/javascript;");
    }

    @DisplayName("로그인 페이지를 내려준다.")
    @Test
    void login() throws IOException {
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
        final URL resource = getClass().getClassLoader().getResource("static/login.html");

        final String expectedContents = new String(Files.readAllBytes(
                new File(Objects.requireNonNull(resource).getFile()).toPath())
        );

        // then
        assertThat(socket.output()).contains(expectedContents);
    }

    @DisplayName("로그인 POST 요청 시 로그인에 성공하면 상태코드 Found와 세션 id를 반환한다.")
    @Test
    void returns_sessionId_and_status_when_login_success() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        final String expectedStatus = Status.FOUND.toString();

        // then
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).contains(expectedStatus),
                () -> assertThat(output).contains(Header.SET_COOKIE.getName()),
                () -> assertThat(output).contains("JSESSIONID")
        );
    }

    @DisplayName("회원가입 GET 요청 시 회원가입 페이지를 내려준다.")
    @Test
    void returns_register_form_when_request_get_register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String expectedContents = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // then
        assertThat(socket.output()).contains(expectedContents);
    }

    @DisplayName("회원가입에 성공하면 상태코드 FOUND를 응답한다.")
    @Test
    void returns_found_status_when_success_post_register() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 53 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=test&email=test%40test.com&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        final String expectedStatus = Status.FOUND.toString();

        // then
        assertThat(socket.output()).contains(expectedStatus);
    }
}
