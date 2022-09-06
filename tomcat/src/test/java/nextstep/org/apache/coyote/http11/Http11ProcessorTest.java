package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("경로를 설정하지 않고 요청을 보내면 기본 지정 응답을 반환한다.")
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
    @DisplayName("index.html 파일에 대해 요청을 받으면 index.html 파일을 반환한다.")
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
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5670 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("css 파일에 대해 요청을 받으면 css 파일을 반환한다.")
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
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        var expected =
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expected);
    }

    @Test
    @DisplayName("js 파일에 대해 요청을 받으면 js 파일을 반환한다.")
    void js() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
        var expected =
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expected);
    }

//    @Test
//    @DisplayName("HTTP POST 요청을 처리한다 index.html 파일을 반환한다.")
//    void post() throws IOException {
//        // given
//        final String httpRequest = String.join("\r\n",
//                "POST /register HTTP/1.1 ",
//                "Host: localhost:8080 ",
//                "Connection: keep-alive ",
//                "Content-Length: 80 ",
//                "Content-Type: application/x-www-form-urlencoded ",
//                "Accept: */* ",
//                "",
//                "account=gugu&password=password&email=hkkang%40woowahan.com");
//
//        final var socket = new StubSocket(httpRequest);
//        final Http11Processor processor = new Http11Processor(socket);
//
//        // when
//        processor.process(socket);
//
//        // then
//        final URL resource = getClass().getClassLoader().getResource("static/index.html");
//        var expected = "HTTP/1.1 200 OK \r\n" +
//                "Content-Type: text/html;charset=utf-8 \r\n" +
//                "Content-Length: 5670 \r\n" +
//                "\r\n" +
//                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
//
//        assertThat(socket.output()).isEqualTo(expected);
//    }
}
