package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void process() throws IOException {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 5564",
                "Content-Type: text/html;charset=utf-8");

        assertThat(socket.output()).startsWith(expected);
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
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 5564",
                "Content-Type: text/html;charset=utf-8",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("없는 아이디로 로그인 요청 시 예외를 발생시킨다.")
    void throw_exception_when_login_with_does_not_exist_account() {
        // given
        final var request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: http://localhost:8080",
                "Content-type: application/x-www-form-urlencoded",
                "",
                "account=gugu1&password=password",
                "");

        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expect = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 2426",
                "Content-Type: text/html;charset=utf-8",
                "");

        assertThat(socket.output())
                .startsWith(expect);
    }

    @Test
    @DisplayName("틀린 비밀번호로 로그인 요청 시 예외를 발생시킨다.")
    void throw_exception_when_login_with_incorrect_password() {
        // given
        final var request = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: http://localhost:8080",
                "Content-type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password2",
                "");
        final var socket = new StubSocket(request);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expect = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 2426",
                "Content-Type: text/html;charset=utf-8",
                "");

        assertThat(socket.output()).startsWith(expect);
    }
}
