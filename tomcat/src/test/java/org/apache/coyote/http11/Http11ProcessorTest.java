package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
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
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
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
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_get() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
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
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3797 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_post_success() {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "\r\n" +
                        "account=gugu&password=password",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/index.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 0 \r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 요청에_쿠키가_없으면_JSESSIONID를_발급한다() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        var socket = new StubSocket(httpRequest);
        var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String cookieHeader = socket.output().lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .findFirst()
                .orElseThrow();
        String sessionId = cookieHeader
                .substring("Set-Cookie: JSESSIONID=".length())
                .strip();

        assertThat(UUID.fromString(sessionId).toString()).isEqualTo(sessionId);
    }

    @Test
    void 요청에_이미_JSession_쿠키_헤더가_있다면_응답에_포함하지_않는다() {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "\r\n" +
                        "account=gugu&password=password",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/index.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 0 \r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_post_fail() {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "Content-Length: 41",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "\r\n" +
                        "account=failAccount&password=failPassword",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/401.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 0 \r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_get() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 4319 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_post_success() {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "Content-Length: 56",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "\r\n" +
                        "account=rude&email=rudevi@woowahan.com&password=password",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/index.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 0 \r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_post_fail() {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46",
                "Connection: keep-alive ",
                "Content-Length: 56",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "\r\n" +
                        "account=gugu&email=hkkang@woowahan.com&password=password",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: http://localhost:8080/login.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 0 \r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }
}
