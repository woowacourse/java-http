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
    @DisplayName("요청 내용을 전달하지 않으면 기본 메세지를 보여준다")
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = """
                HTTP/1.1 200 OK
                Content-Type: text/html;charset=utf-8
                Content-Length: 12
                
                Hello world!""";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("/ 경로로 요청을 하면, 기본 메세지를 응답한다.")
    void home() {
        // given
        final String httpRequest = """
                GET / HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                """;

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = """
                HTTP/1.1 200 OK
                Content-Type: text/html;charset=utf-8
                Content-Length: 12
                
                Hello world!""";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("/index.html 경로로 요청을 하면, /resource/index.html 페이지를 응답한다.")
    void index() throws IOException {
        // given
        final String httpRequest = """
                GET /index.html HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                """;

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = """
                HTTP/1.1 200 OK
                Content-Type: text/html;charset=utf-8
                Content-Length: 5518
                
                """ + new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("HTTP 함수와 경로에 매칭되는 것이 없으면, 예외 메세지를 보여준다")
    void failedResponse() {
        // given
        final String httpRequest = """
                GET /indexx.html HTTP/1.1
                Host: localhost:8080
                Connection: keep-alive
                """;

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = """
                HTTP/1.1 404 Not Found
                Content-Type: text/html;charset=utf-8
                """;

        assertThat(socket.output()).contains(expected);
    }
}
