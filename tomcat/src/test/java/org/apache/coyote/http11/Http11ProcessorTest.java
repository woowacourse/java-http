package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.controller.HomeController;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("기본 요청이 들어오면 Hello world!를 응답한다.")
    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: 12"),
                () -> assertThat(output).endsWith("Hello world!")
        );
    }

    @DisplayName("index.html을 요청하면 index.html을 응답한다.")
    @Test
    void index() throws IOException {
        // given
        final var httpRequest = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final byte[] body = readFileBytes("static/index.html");
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + body.length),
                () -> assertThat(output).endsWith(new String(body))
        );
    }

    @DisplayName("styles.css를 요청하면 styles.css를 응답한다.")
    @Test
    void css() throws IOException {
        // given
        final var httpRequest = """
                GET /css/styles.css HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final byte[] body = readFileBytes("static/css/styles.css");
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: text/css;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + body.length),
                () -> assertThat(output).endsWith(new String(body))
        );
    }

    @DisplayName("scripts.js를 요청하면 scripts.js를 응답한다.")
    @Test
    void js() throws IOException {
        // given
        final var httpRequest = """
                GET /js/scripts.js HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final byte[] body = readFileBytes("static/js/scripts.js");
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: application/javascript;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + body.length),
                () -> assertThat(output).endsWith(new String(body))
        );
    }

    @DisplayName("없는 페이지를 요청하면 404.html을 응답한다.")
    @Test
    void notFound() throws IOException {
        // given
        final var httpRequest = """
                GET /notfound HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final byte[] body = readFileBytes("static/404.html");
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 404 Not Found"),
                () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + body.length),
                () -> assertThat(output).endsWith(new String(body))
        );
    }

    @DisplayName("로그인 페이지를 요청하면 login.html을 응답한다.")
    @Test
    void login_page() throws IOException {
        // given
        final var httpRequest = """
                GET /login HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final byte[] body = readFileBytes("static/login.html");
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 200 OK"),
                () -> assertThat(output).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(output).contains("Content-Length: " + body.length),
                () -> assertThat(output).endsWith(new String(body))
        );
    }

    @DisplayName("로그인 성공 시 /index.html로 리다이렉트하고, Set-Cookie 헤더를 포함하여 응답한다.")
    @Test
    void login_success() {
        // given
        final String body = "account=gugu&password=password";
        final var httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.getBytes().length,
                "",
                body
        );

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 302 Found"),
                () -> assertThat(output).contains("Location: /index.html"),
                () -> assertThat(output).contains("Set-Cookie: JSESSIONID=")
        );
    }

    @DisplayName("로그인 실패 시 /401.html로 리다이렉트한다.")
    @Test
    void login_fail() {
        // given
        final String body = "account=invalid&password=password";
        final var httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.getBytes().length,
                "",
                body
        );

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 302 Found"),
                () -> assertThat(output).contains("Location: /401.html")
        );
    }

    @DisplayName("JSESSIONID 쿠키가 없으면 Set-Cookie 헤더를 포함하지 않고 응답한다.")
    @Test
    void jSessionId() {
        // given
        final var httpRequest = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertThat(output).doesNotContain("Set-Cookie: JSESSIONID=");
    }

    @DisplayName("JSESSIONID 쿠키가 있으면 Set-Cookie 헤더를 포함하지 않고 응답한다.")
    @Test
    void hasJSessionId() {
        // given
        final var httpRequest = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Cookie: JSESSIONID=1234\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertThat(output).doesNotContain("Set-Cookie: JSESSIONID=");
    }

    @DisplayName("로그인한 사용자가 /login 요청 시 index.html로 리다이렉트한다.")
    @Test
    void login_with_jSessionId() {
        // given
        final var user = com.techcourse.db.InMemoryUserRepository.findByAccount("gugu").get();
        final var session = new org.apache.catalina.session.Session("1234");
        session.setAttribute("user", user);
        final var sessionManager = SessionManager.getInstance();
        sessionManager.add(session);

        final var httpRequest = """
                GET /login HTTP/1.1\r
                Host: localhost:8080\r
                Cookie: JSESSIONID=1234\r
                \r
                """;
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, createRequestMapping());

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        assertAll(
                () -> assertThat(output).startsWith("HTTP/1.1 302 Found"),
                () -> assertThat(output).contains("Location: /index.html")
        );

        sessionManager.remove(session);
    }

    private byte[] readFileBytes(final String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(path);
        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }

    private RequestMapping createRequestMapping() {
        final var staticResourceController = new org.apache.catalina.controller.resource.StaticResourceController();
        return new RequestMapping(
                Map.of(
                        "/", new HomeController(),
                        "/login", new LoginController(staticResourceController),
                        "/register", new RegisterController(staticResourceController)
                ),
                staticResourceController
        );
    }
}
