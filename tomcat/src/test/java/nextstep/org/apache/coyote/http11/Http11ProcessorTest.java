package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import support.StubSocket;

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
        var expected = "HTTP/1.1 200 OK \r\n" +
            "Content-Type: text/html;charset=utf-8 \r\n" +
            "Content-Length: 5564 \r\n" +
            "\r\n" +
            new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_페이지_로드() {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 200 OK", "Content-Type: text/html;charset=utf-8");
    }

    @Test
    void login_페이지_요청() {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /login HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 30",
            "",
            "account=gugu" + "&" + "password=password");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 FOUND", "Location", "Set-Cookie");
    }

    @Test
    void register_페이지_로드() {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 200 OK", "Content-Type: text/html;charset=utf-8");
    }

    @Test
    void register_페이지_요청() {
        // given
        final String httpRequest = String.join("\r\n",
            "POST /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Type: application/x-www-form-urlencoded",
            "Content-Length: 59",
            "",
            "account=Pooh" + "&" + "email=tmdwns9913@gmail.com" + "&" + "password=password");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 302 FOUND", "Location", "Set-Cookie");
    }

    @Test
    void 존재하지_않는_메서드에_대한_요청이면_405_응답() {
        // given
        String notExistMethod = "DELETE";
        final String httpRequest = String.join("\r\n",
            notExistMethod + " /register HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains("HTTP/1.1 405 METHOD_NOT_ALLOWED");
    }
}
