package nextstep.org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.DisplayName;
import support.StubSocket;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

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
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("html 파일을 요청할 경우 text/html 응답을 반환한다")
    void requestHtml() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: " +body.getBytes(StandardCharsets.UTF_8).length +"\r\n" +
                "\r\n"+
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("css 파일을 요청할 경우 text/cs 응답을 반환한다")
    void requestCss() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/css;charset=utf-8\r\n" +
                "Content-Length: " +body.getBytes(StandardCharsets.UTF_8).length +"\r\n" +
                "\r\n"+
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("js 파일을 요청할 경우 text/js 응답을 반환한다")
    void requestJs() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/css;charset=utf-8\r\n" +
                "Content-Length: " +body.getBytes(StandardCharsets.UTF_8).length +"\r\n" +
                "\r\n"+
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }
}
