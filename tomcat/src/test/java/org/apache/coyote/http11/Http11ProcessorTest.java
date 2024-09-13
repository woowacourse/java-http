package org.apache.coyote.http11;

import org.apache.coyote.RequestProcessor;
import org.apache.coyote.util.ResourceFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Http11ProcessorTest {

    @Test
    @DisplayName("Default Path 요청을 받을 수 있다.")
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new RequestProcessor());

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 12 ",
                "Content-Type: text/plain;charset=utf-8 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("메인 페이지 Get 요청을 받을 수 있다.")
    void index() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, new RequestProcessor());

        // when
        processor.process(socket);

        String resource = ResourceFinder.findBy("/index.html");

        // then
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 5564 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                resource;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("login Get 요청을 받을 수 있다.")
    void getLogin() {
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, new RequestProcessor());

        // when
        processor.process(socket);

        // then
        String resource = ResourceFinder.findBy("/login.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 3840 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                resource;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("login post 요청을 받을 수 있다.")
    void postLogin() {
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, new RequestProcessor());

        // when
        processor.process(socket);

        // then
        String expectedLine = "HTTP/1.1 302 Found \r\n";
        String expectedLocation = "Location: /index.html \r\n";

        assertAll(
                () -> assertThat(socket.output()).contains(expectedLine),
                () -> assertThat(socket.output()).contains(expectedLocation),
                () -> assertThat(socket.output()).contains("Set-Cookie:")
        );
    }

    @Test
    @DisplayName("register Get 요청을 보내고 받을 수 있다.")
    void getRegister() {
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, new RequestProcessor());

        // when
        processor.process(socket);

        // then
        String resource = ResourceFinder.findBy("/register.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 4319 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                resource;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("register post 요청을 받을 수 있다.")
    void postRegister() {
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password&email=hkkang@woowahan.com");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket, new RequestProcessor());

        // when
        processor.process(socket);

        // then
        String expectedLine = "HTTP/1.1 302 Found \r\n";
        String expectedLocation = "Location: /index.html \r\n";

        assertAll(
                () -> assertThat(socket.output()).contains(expectedLine),
                () -> assertThat(socket.output()).contains(expectedLocation)
        );
    }
}
