package org.apache.coyote.http;

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
    @DisplayName("루트 경로 요청 시 Hello world 응답")
    void process() {
        // given
        final String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final StubSocket socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("index.html 파일 요청 시 정적 파일 응답")
    void index() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("CSS 파일 요청 시 Content-Type: text/css 응답")
    void css() {
        // given
        final String request = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertThat(output).contains("Content-Type: text/css");
        assertThat(output).contains("HTTP/1.1 200 OK");
    }

    @Test
    @DisplayName("JS 파일 요청 시 Content-Type: application/javascript 응답")
    void javascript() {
        // given
        final String request = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: application/javascript ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertThat(output).contains("Content-Type: application/javascript");
        assertThat(output).contains("HTTP/1.1 200 OK");
    }

    @Test
    @DisplayName("로그인 페이지 Query String 파싱하여 로그인 처리")
    void loginQueryString() {
        // given
        final String request = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertThat(output).contains("HTTP/1.1 200 OK");
        assertThat(output).contains("Content-Type: text/html;charset=utf-8");
    }

    @Test
    @DisplayName("존재하지 않는 파일 요청 시 Not Found 응답")
    void notFound() {
        // given
        final String request = String.join("\r\n",
                "GET /nonexistent.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertThat(output).contains("HTTP/1.1 200 OK");
        assertThat(output).contains("Not Found");
    }

    @Test
    @DisplayName("확장자 없이 HTML 파일 요청 시 자동으로 .html 확장자 추가")
    void loginWithoutExtension() {
        // given
        final String request = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertThat(output).contains("HTTP/1.1 200 OK");
        assertThat(output).contains("Content-Type: text/html;charset=utf-8");
    }
}
