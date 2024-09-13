package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
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
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
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
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/login에 GET 요청을 보내면 /login.html을 응답한다.")
    @Test
    void loginGet() throws IOException {
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

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expectedHeader = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 ";

        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedHeader, expectedBody);
    }


    @DisplayName("/register에 GET 요청을 보내면 /register.html을 응답한다.")
    @Test
    void registerGet() throws IOException {
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

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expectedHeader = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 ";

        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedHeader, expectedBody);
    }

    @DisplayName("유효한 회원 정보로 /login에 POST 요청을 보내면 /index.html로 리다이렉트하고, 쿠키로 JSESSIONID를 응답한다.")
    @Test
    void loginPost() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        List<String> expectedHeader = List.of("HTTP/1.1 302 Found \r\n", "Location: /index.html \r\n", "JSESSIONID=");

        assertThat(socket.output()).contains(expectedHeader);
    }

    @DisplayName("잘못된 비밀번호로 /login에 POST 요청을 보내면 401 코드와 함께 /401.html을 응답한다.")
    @Test
    void loginPostInvalidUserInfo() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 37 ",
                "",
                "account=gugu&password=invalidPassword");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expectedHeader = "HTTP/1.1 401 Unauthorized \r\n";
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(expectedHeader, expectedBody);
    }

    @DisplayName("유효한 회원 정보로 /register에 POST 요청을 보내면 /index.html로 리다이렉트하고, 쿠키로 JSESSIONID를 응답한다.")
    @Test
    void registerPost() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 59 ",
                "",
                "account=newUser&password=password&email=newUser@email.com");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        List<String> expectedHeader = List.of("HTTP/1.1 302 Found \r\n", "Location: /index.html \r\n", "JSESSIONID=");

        assertThat(socket.output()).contains(expectedHeader);
    }
}
