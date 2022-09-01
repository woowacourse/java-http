package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("Hello World 응답을 할 수 있다")
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
    @DisplayName("index.html 응답을 할 수 있다")
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        assertThat(socket.output()).isNotNull();
    }

    @Test
    @DisplayName("css 파일 응답을 할 수 있다")
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isNotNull();
    }

    @Test
    @DisplayName("js 파일 응답을 할 수 있다")
    void js() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*;q=0.1 ",
                "Connection: keep-alive ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isNotNull();
    }

    @Test
    @DisplayName("올바르지 않은 확장자 요청일 경우 예외를 발생시킨다.")
    void invalid_url() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /abc/def HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: */*;q=0.1 ",
                "Connection: keep-alive ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        // then
        assertThatThrownBy(() -> processor.process(socket))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 계정으로 로그인할 경우 예외를 발생시킨다.")
    void nonexistent_account() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /abc/def HTTP/1.1 ",
                "Host: localhost:8080/login?account=abc&password=password ",
                "Accept: */*;q=0.1 ",
                "Connection: keep-alive ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        // then
        assertThatThrownBy(() -> processor.process(socket))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("잘못된 계정으로 로그인하는 경우 예외를 발생시킨다.")
    void invalid_account() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /abc/def HTTP/1.1 ",
                "Host: localhost:8080/login?account=gugu&password=password1 ",
                "Accept: */*;q=0.1 ",
                "Connection: keep-alive ",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        // then
        assertThatThrownBy(() -> processor.process(socket))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
