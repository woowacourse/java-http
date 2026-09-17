package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

//    @Test
//    void process() {
//        // given
//        final var socket = new StubSocket();
//        final var processor = new Http11Processor(socket);
//
//        // when
//        processor.process(socket);
//
//        // then
//        var expected = String.join("\r\n",
//                "HTTP/1.1 200 OK ",
//                "Content-Type: text/html;charset=utf-8 ",
//                "Content-Length: 12 ",
//                "",
//                "Hello world!");
//
//        assertThat(socket.output()).isEqualTo(expected);
//    }

    @Test
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
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("html은 text_html로 응답한다")
    void htmlTest() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("Content-Type: text/html;charset=utf-8 ");
    }

    @Test
    @DisplayName("css text_css로 응답한다")
    void css() throws IOException {
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
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/css/styles.css");

        final byte[] body = Files.readAllBytes(
                new File(resource.getFile()).toPath()
        );

        final String expected = "HTTP/1.1 200 OK \r\n"
                + "Content-Type: text/css;charset=utf-8 \r\n"
                + "Content-Length: " + body.length + " \r\n"
                + "\r\n"
                + new String(body, StandardCharsets.UTF_8);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("쿼리 스트링이 포함된 요청에서 경로에 해당하는 HTML을 응답한다")
    void queryString() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login.html?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK");
    }
}
