package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("GET / 요청이 오면 Hello world!를 반환한다.")
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

    @DisplayName("GET /index.html 요청이 오면 static/index.html을 반환한다.")
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

    @DisplayName("GET /login과 같이 파일 확장자가 없는 요청이 오면 대응되는 html을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"/login", "/register"})
    void login(String uri) throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET " + uri + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/" + uri + ".html");
        byte[] fileContent = Files.readAllBytes(new File(resource.getFile()).toPath());

        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + fileContent.length + " \r\n" +
                "\r\n" +
                new String(fileContent);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("올바른 회원 정보와 함께 로그인 요청이 오면 /index.html로 리다이렉트한다.")
    @Test
    void loginWithValidQueryString() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/index.html \r\n" +
                "\r\n";
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("올바르지 않은 회원 정보와 함께 로그인 요청이 오면 /401.html로 리다이렉트한다.")
    @Test
    void loginWithInvalidQueryString() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=invalidPassword ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/401.html \r\n" +
                "\r\n";
        assertThat(socket.output()).isEqualTo(expected);
    }
}
