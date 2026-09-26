package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.StaticResourceController;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private final RequestMapping requestMapping = new RequestMapping(
            Map.of(
                    "/login", new LoginController(new SessionManager()),
                    "/register", new RegisterController()
            ),
            new StaticResourceController()
    );

    @Test
    void redirectsFailedLogin() {
        StubSocket socket = new StubSocket("POST /login HTTP/1.1\r\nContent-Length: 0\r\n\r\n");

        new Http11Processor(socket, requestMapping).process(socket);

        assertThat(socket.output()).isEqualTo(
                "HTTP/1.1 302 Found\r\nLocation: /401.html\r\nContent-Length: 0\r\n\r\n"
        );
    }

    @Test
    void redirectsSuccessfulLoginWithSessionCookie() {
        String body = "account=gugu&password=password";
        StubSocket socket = new StubSocket("POST /login HTTP/1.1\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n\r\n"
                + body);

        new Http11Processor(socket, requestMapping).process(socket);

        assertThat(socket.output()).matches(
                "HTTP/1\\.1 302 Found\r\nLocation: /index\\.html\r\n"
                        + "Set-Cookie: JSESSIONID=[0-9a-f-]{36}\r\nContent-Length: 0\r\n\r\n"
        );
    }

    @Test
    void returnsEmptyMethodNotAllowedResponse() {
        StubSocket socket = new StubSocket("PUT / HTTP/1.1\r\n\r\n");

        new Http11Processor(socket, requestMapping).process(socket);

        assertThat(socket.output()).isEqualTo(
                "HTTP/1.1 405 Method Not Allowed\r\nContent-Length: 0\r\n\r\n"
        );
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket(String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=test-session-id",
                "",
                ""));
        final var processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=test-session-id",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
