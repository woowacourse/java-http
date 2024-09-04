package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @Disabled
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var expected = String.join("\r\n",
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
        final var expected = "HTTP/1.1 200 OK \r\n" +
                             "Content-Type: text/html;charset=utf-8 \r\n" +
                             "Content-Length: 5670 \r\n" +
                             "\r\n" +
                             new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("존재하지 않는 account로 로그인 시 예외가 발생한다.")
    void invalidAccount() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=zeze&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when && then
        // TODO 예외 캐치하지 말고 에외 페이지 반환확인
        assertThatThrownBy(() -> processor.process(socket))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("일치하지 않는 password 로그인 시 예외가 발생한다.")
    void invalidPassword() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=redddy&password=486 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when && then
        // TODO 예외 캐치하지 말고 에외 페이지 반환확인
        assertThatThrownBy(() -> processor.process(socket))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }
}
