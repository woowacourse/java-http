package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.config.WebConfig;
import org.apache.coyote.http11.message.header.Header;
import org.apache.coyote.http11.message.response.header.StatusCode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @BeforeAll
    static void config() {
        new WebConfig().init();
    }

    @DisplayName("서버에 접속하면 Hello world를 응답한다.")
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

    @DisplayName("index 페이지를 응답한다.")
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

    @DisplayName("css 파일 응답 시 ContentType을 text/css로 응답한다.")
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
        assertThat(socket.output()).contains("text/css");
    }

    @DisplayName("로그인 GET 요청 시 로그인 페이지를 응답한다.")
    @Test
    void login_get() throws IOException {
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
        final String expectedContents = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // then
        assertThat(socket.output()).contains(expectedContents);
    }

    @DisplayName("로그인 POST 요청 시 로그인에 성공하면 상태코드 Found와 세션 id를 응답한다.")
    @Test
    void login_post() {
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
        final String expectedStatus = StatusCode.FOUND.toString();

        // then
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).contains(expectedStatus),
                () -> assertThat(output).contains(Header.SET_COOKIE.getName()),
                () -> assertThat(output).contains("JSESSIONID")
        );
    }

    @DisplayName("회원가입 GET 요청 시 회원가입 페이지를 응답한다.")
    @Test
    void register_get() throws IOException {
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

    @DisplayName("회원가입 POST 요청 시 회원가입에 성공하면 상태코드 See Other를 응답한다.")
    @Test
    void register_post() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 53 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=forky&email=forky%40foo.com&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);
        final String expectedStatus = StatusCode.SEE_OTHER.toString();

        // then
        assertThat(socket.output()).contains(expectedStatus);
    }
}
