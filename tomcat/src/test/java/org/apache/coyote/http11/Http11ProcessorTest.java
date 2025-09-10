package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.stream.Collectors;
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
        String resourcsString = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourcsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourcsString;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void get_register() throws IOException {
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
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String resourcsString = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resourcsString.getBytes().length + " \r\n" +
                "\r\n" +
                resourcsString;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login_success_redirect(){
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 " +
                HttpStatusCode.FOUND.getCode() + " " +
                HttpStatusCode.FOUND.getMessage() + " \r\n" +
                "Location: /index.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + 0 + " \r\n" +
                "\r\n";

        var actual = Arrays.stream(socket.output().split("\r\n"))
                .filter(line -> !line.startsWith("Set-Cookie")) // 쿠키 제외
                .collect(Collectors.joining("\r\n"))+
                "\r\n" +
                "\r\n";

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void post_register(){
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 46 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu2&password=password&email=hkkang%40woowahan.com");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 " +
                HttpStatusCode.FOUND.getCode() + " " +
                HttpStatusCode.FOUND.getMessage() + " \r\n" +
                "Location: /index.html \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + 0 + " \r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }
}
