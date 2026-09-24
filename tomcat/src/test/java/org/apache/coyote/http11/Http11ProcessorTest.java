package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

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
        var expected = List.of(
                "HTTP/1.1 200 OK ",
                "Content-Type: ",
                "Content-Length: "
        );

        assertThat(socket.output()).contains(expected);
    }

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

    @DisplayName("로그인에 성공하면 응답 헤더에 HTTP Status Code를 302로 반환하고 /index.html로 리다이렉트 한다.")
    @Test
    void return302HTTPStatusCodeAndRedirectToIndexHtmlWhenLoginSucceeds() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        final byte[] resource = readResource("static/index.html");
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: "+ resource.length + " \r\n" +
                "\r\n"+
                new String(resource);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인에 실패하면 401.html로 리다이렉트 한다.")
    @Test
    void redirectTo401HtmlWhenLoginFails() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /login?account=gugu&password=false HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] resource = readResource("static/401.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + resource.length + " \r\n" +
                "\r\n"+
                new String(resource);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("/register으로 접속하면 회원가입 페이지(register.html)를 보여준다.")
    @Test
    void redirectToRegisterHtmlWhenRegisterPageRequested() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        final byte[] resource = readResource("static/register.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: "+ resource.length + " \r\n" +
                "\r\n"+
                new String(resource);

        assertThat(socket.output()).isEqualTo(expected);
    }

    private byte[] readResource(String resourceName) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(resourceName);
        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }
}
