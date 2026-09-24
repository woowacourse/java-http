package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
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
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void missingResourceReturnsNotFoundPage() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /favicon.ico HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 404 Not Found\r\n");
        assertThat(socket.output()).contains("Content-Type: text/html;charset=utf-8");
        assertThat(socket.output()).contains("404");
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-session-id",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
        assertThat(socket.output()).doesNotContain("Set-Cookie");
    }

    @Test
    void loginSuccessRedirectsToIndex() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String response = socket.output();
        String sessionId = response.split("Set-Cookie: JSESSIONID=")[1].split("\r\n")[0];

        assertThat(response).startsWith("HTTP/1.1 302 Found\r\nLocation: /index.html\r\nSet-Cookie: JSESSIONID=");
        assertThat(response).endsWith("\r\nContent-Length: 0\r\n\r\n");
        assertThat(sessionId).matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");

        Session session = SessionManager.getInstance().findSession(sessionId);
        User user = (User) session.getAttribute("user");
        assertThat(user.getAccount()).isEqualTo("gugu");
        SessionManager.getInstance().remove(sessionId);
    }

    @Test
    void loggedInUserIsRedirectedFromLoginToIndex() {
        // given
        String sessionId = "logged-in-session-id";
        Session session = new Session(sessionId);
        session.setAttribute("user", InMemoryUserRepository.findByAccount("gugu").orElseThrow());
        SessionManager.getInstance().add(session);
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "Content-Length: 0",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
        SessionManager.getInstance().remove(sessionId);
    }

    @Test
    void loginFailureRedirectsTo401() {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=wrong HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session-id",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /401.html",
                "Content-Length: 0",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
        assertThat(SessionManager.getInstance().findSession("existing-session-id")).isNull();
    }

    @Test
    void register() {
        // given
        String body = "account=newuser&password=password&email=newuser%40example.com";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                "Cookie: JSESSIONID=existing-session-id",
                "",
                body);
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "Content-Length: 0",
                "",
                "");
        assertThat(socket.output()).isEqualTo(expected);
        assertThat(InMemoryUserRepository.findByAccount("newuser").orElseThrow().checkPassword("password"))
                .isTrue();
    }
}
