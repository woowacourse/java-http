package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class Http11ProcessorTest {

    @Test
    void 기본_페이지_조회() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 인덱스_페이지_조회() throws IOException {
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
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "\r\n" +
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 페이지_조회시_확장자가_없으면_html_확장자로_반영한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1 ",
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
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "\r\n" +
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 로그인_페이지_조회() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
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
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "\r\n" +
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 회원가입_페이지_조회() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
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
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "\r\n" +
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void CSS_조회() throws IOException {
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
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/css \r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "\r\n" +
                body;

        assertThat(socket.output()).isEqualTo(expected);
        socket.close();
    }

    @Test
    void 존재하지_않는_페이지_조회시_404페이지를_반환한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /notexistpage12312.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 404 NOT_FOUND \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "\r\n" +
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 로그인이_성공하면_인덱스_페이지로_이동한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = List.of(
                "HTTP/1.1 302 FOUND \r\n",
                "Location: /index.html \r\n",
                "\r\n"
        );

        assertThat(socket.output()).contains(expected);
    }

    @Test
    void 로그인이_실패하면_401페이지로_이동한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: 38",
                "",
                "account=gugu&password=wrongpassword");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 401 UNAUTHORIZED \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "\r\n" +
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 회원가입후_인덱스_페이지로_이동한다() {
        // given
        final String requestBody = "account=royce&email=roro@gmail.com&password=newpassword";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: " + requestBody.getBytes().length + " ",
                "",
                requestBody);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 FOUND \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }
}
