package nextstep.org.apache.coyote.http11;

import nextstep.jwp.vo.Response;
import nextstep.jwp.vo.ResponseStatus;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import support.StubSocket;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
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
        var expected = Response.from(ResponseStatus.OK)
                        .addHeader("Content-Type", "text/html;charset=utf-8")
                        .addHeader("Content-Length", "12")
                        .addBlankLine()
                        .addBody("Hello world!")
                        .getResponse();

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
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
        var expected = Response.from(ResponseStatus.OK)
                .addHeader("Content-Type", "text/html;charset=utf-8")
                .addHeader("Content-Length", String.valueOf(body.getBytes().length))
                .addBlankLine()
                .addBody(body)
                .getResponse();

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final Path path = new File(resource.getFile()).toPath();
        final List<String> actual = Files.readAllLines(path);
        String responseBody = String.join("\n", actual) + "\n";
        var expected = Response.from(ResponseStatus.OK)
                .addHeader("Content-Type", "text/html;charset=utf-8")
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length))
                .addBlankLine()
                .addBody(responseBody)
                .getResponse();

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginPost_success() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        List<String> expected = List.of("HTTP/1.1 302 Found",
                "Location: /index.html", "Set-Cookie: JSESSIONID=");
        assertThat(isContains(socket.output(), expected)).isTrue();
    }

    private boolean isContains(String actual, List<String> result) {
        System.out.println(actual);
        System.out.println(result.stream()
                .filter(actual::contains)
                .count());
        return result.size() == result.stream()
                .filter(actual::contains)
                .count();
    }

    @Test
    void loginPost_fail() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password123");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = Response.from(ResponseStatus.FOUND)
                        .addHeader("Location", "/401.html")
                        .addBlankLine()
                        .getResponse();
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_success() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=alpha&password=alphakun&email=alpha@naver.com");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = Response.from(ResponseStatus.FOUND)
                .addHeader("Location", "/index.html")
                .addBlankLine()
                .getResponse();
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register_fail() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 80",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=alpha&password=alphakun");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = Response.from(ResponseStatus.FOUND)
                .addHeader("Location", "/401.html")
                .addBlankLine()
                .getResponse();
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginWithCookie() throws IOException {
        // given
        SessionManager sessionManager = new SessionManager();
        sessionManager.add(new Session("123"));
        final String httpRequest= String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=123",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final Path path = new File(resource.getFile()).toPath();
        final List<String> actual = Files.readAllLines(path);
        String responseBody = String.join("\n", actual) + "\n";
        var expected = Response.from(ResponseStatus.OK)
                .addHeader("Content-Type", "text/html;charset=utf-8")
                .addHeader("Content-Length", String.valueOf(responseBody.getBytes().length))
                .addBlankLine()
                .addBody(responseBody)
                .getResponse();

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() throws IOException {
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
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final Path path = new File(resource.getFile()).toPath();
        final List<String> actual = Files.readAllLines(path);
        String responseBody = String.join("\n", actual) + "\n";
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginPost() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com ");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }
}
