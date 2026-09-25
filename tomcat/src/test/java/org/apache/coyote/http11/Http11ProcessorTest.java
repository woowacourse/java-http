package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import org.apache.coyote.http11.session.SessionManager;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final String sessionCookie = "JSESSIONID=" + SessionManager.createSession().getId();
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: " + sessionCookie,
                "",
                "");

        final var socket = new StubSocket(httpRequest);
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
        final String sessionCookie = "JSESSIONID=" + SessionManager.createSession().getId();
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: " + sessionCookie,
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

    @Test
    void loginSuccessRedirectsToIndex() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String sessionCookie = "JSESSIONID=" + SessionManager.createSession().getId();
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + requestBody.length(),
                "Cookie: " + sessionCookie,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "Content-Length: 0",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void getLoginWithQueryStringServesLoginPage() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=query-login-test",
                "",
                "");

        final var socket = new StubSocket(httpRequest);

        // when
        new Http11Processor(socket).process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK ")
                .contains("Content-Type: text/html;charset=utf-8 ");
    }

    @Test
    void replacesUnknownSessionIdWithServerGeneratedSessionId() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=client-controlled-session-id",
                "",
                "");

        final var socket = new StubSocket(httpRequest);

        // when
        new Http11Processor(socket).process(socket);

        // then
        assertThat(socket.output())
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]{36} ")
                .doesNotContain("JSESSIONID=client-controlled-session-id");
    }

    @Test
    void setsSessionCookieWhenRequestDoesNotHaveCookie() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]{36} ");
    }

    @Test
    void loginFailureReturnsUnauthorized() {
        // given
        final String requestBody = "account=gugu&password=wrong";
        final String sessionCookie = "JSESSIONID=" + SessionManager.createSession().getId();
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "Cookie: " + sessionCookie,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);

        // when
        new Http11Processor(socket).process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 401 Unauthorized\r\n")
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("<h1 class=\"display-1\">401</h1>");
    }

    @Test
    void blankRegistrationDataIsNotSaved() {
        // given
        final String requestBody = "account=+&password=+&email=+";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);
        final var socket = new StubSocket(httpRequest);

        // when
        new Http11Processor(socket).process(socket);

        // then
        assertThat(InMemoryUserRepository.findByAccount(" ")).isEmpty();
        assertThat(socket.output())
                .startsWith("HTTP/1.1 400 Bad Request\r\n")
                .contains("<form method=\"post\" action=\"/register\">");
    }

    @Test
    void loggedInUserIsRedirectedFromLoginPage() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);

        final var loginSocket = new StubSocket(loginRequest);

        // when
        new Http11Processor(loginSocket).process(loginSocket);

        final String sessionCookie = loginSocket.output().lines()
                .filter(line -> line.startsWith("Set-Cookie: "))
                .findFirst()
                .orElseThrow()
                .substring("Set-Cookie: ".length());

        final String loginPageRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: " + sessionCookie,
                "",
                "");
        final var loginPageSocket = new StubSocket(loginPageRequest);

        new Http11Processor(loginPageSocket).process(loginPageSocket);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "Content-Length: 0",
                "",
                "");

        assertThat(loginPageSocket.output()).isEqualTo(expected);
    }
}
