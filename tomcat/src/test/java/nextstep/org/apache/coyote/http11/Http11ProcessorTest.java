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
    @DisplayName("GET / 요청에 hello world 문자가 담긴 html 응답이 와야한다.")
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
    @DisplayName("GET /index.html 세션ID 없이 요청에 login.html로 redirect 가 와야한다.")
    void indexCreateCookie() {
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
        var expected = "HTTP/1.1 302 Found \r\n"
                + "Location: /login.html \r\n"
                + "\r\n";

        final String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /login 요청에 login.html 응답이 와야한다.")
    void loginPage() throws IOException {
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
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3797 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /login 요청이 올바르면 index.html 페이지로 redirect 응답이 와야한다.")
    void login(){
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String cookie = getCookie(socket.output());
        var expected = "HTTP/1.1 302 Found \r\n"
                + "Location: /index.html \r\n"
                + "Set-Cookie: " + cookie + " \r\n"
                + "\r\n";

        final String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /login 요청이 올바르지 않으면 401.html 페이지로 redirect 응답이 와야한다.")
    void loginFail()  {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 26 ",
                "",
                "account=gugu&password=pass");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n"
                + "Location: /401.html \r\n"
                + "\r\n";

        final String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /login 으로 접속했을때 로그인상태이면 index.html로 redirect 시킨다.")
    void loginPageWithSessionCookie() {
        // given
        final StubSocket loginSocket = 구구_로그인();
        final String output = loginSocket.output();
        final String cookie = getCookie(output);

        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: " + cookie + " ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n"
                + "Location: /index.html \r\n"
                + "\r\n";

        final String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /login 으로 접속했을때 로그인상태가 아니면 login.html로 이동 시킨다.")
    void loginPageWithoutSessionCookie() throws IOException {
        // given
        final StubSocket loginSocket = 구구_로그인();
        final String output = loginSocket.output();

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
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3797 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET /401.html 요청에 401.html 응답이 와야한다.")
    void redirectLoginFail()  {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /401.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 200 OK \r\n"
                + "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: 2426 \r\n"
                + "\r\n";

        final String actual = socket.output();
        assertThat(actual).contains(expected);
    }

    @Test
    @DisplayName("GET /register 요청에 register.html 응답이 와야한다.")
    void registerPage() throws IOException {
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
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 4319 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /register 요청에 정보가 올바르면 index.html redirect 응답이 와야한다.")
    void register()  {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 61 ",
                "",
                "account=dongho108&email=dongho108@naver.com&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n"
                + "Location: /index.html \r\n"
                + "\r\n";

        final String actual = socket.output();
        assertThat(actual).isEqualTo(expected);
    }

    private String getCookie(final String output) {
        final String[] response = output.split("\r\n");
        final String header = response[2];
        final String[] cookie = header.split(":");
        return cookie[1].replaceAll(" ", "");
    }

    private StubSocket 구구_로그인() {
        final String loginHttpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password");

        final var loginSocket = new StubSocket(loginHttpRequest);
        final Http11Processor loginProcessor = new Http11Processor(loginSocket);
        loginProcessor.process(loginSocket);
        return loginSocket;
    }
}
