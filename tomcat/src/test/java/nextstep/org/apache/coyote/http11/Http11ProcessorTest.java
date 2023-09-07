package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

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
        var expected = String.join(System.lineSeparator(),
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
        final String httpRequest = String.join(System.lineSeparator(),
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
        var expected = "HTTP/1.1 200 OK " + System.lineSeparator() +
                "Content-Type: text/html;charset=utf-8 " + System.lineSeparator() +
                "Content-Length: 5564 " + System.lineSeparator() +
                System.lineSeparator() +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("CSS 파일을 반환한다.")
    void getCss() throws IOException {
        // given
        String httpRequest = String.join(System.lineSeparator(),
                        "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedResponseBody);
    }

    @Test
    @DisplayName("URL에 확장자가 없을 시 HTML로 반환한다.")
    void getHtmlWhenNotExistUrl() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK " + System.lineSeparator() +
                "Content-Type: text/html;charset=utf-8 " + System.lineSeparator() +
                "Content-Length: 5564 "+ System.lineSeparator() +
                System.lineSeparator() +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 페이지를 반환한다.")
    void getLoginPage() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expectedResponseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(expectedResponseBody);
    }

    @Test
    @DisplayName("로그인이 성공하면 /index.html로 리다이렉트 된다.")
    void redirectIfLoginSuccess() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password");

        String http = "HTTP/1.1 302 FOUND ";
        String location = "Location: /index.html";

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);

        // when, then
        assertThat(socket.output()).contains(http, location);
    }

    @Test
    @DisplayName("회원 account, password가 잘못되었을 경우 401.html로 리다이렉트 한다.")
    void redirectIfLoginFail() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login?account=zz&password=zz HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 62 ",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=hoho&password=password\n");

        String http = "HTTP/1.1 302 FOUND ";
        String location = "Location: /401.html";

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);

        // when, then
        assertThat(socket.output()).contains(http, location);
    }

    @Test
    @DisplayName("회원 가입 완료 시 index.html로 리다이렉트 한다.")
    void register() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com\n");

        String http = "HTTP/1.1 302 FOUND ";
        String location = "Location: /index.html";

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);

        // when, then
        assertThat(socket.output()).contains(http, location);
    }
}
