package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.Application;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.resource.ResourceHandler;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.controller.StaticResourceController;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new SessionManager(), Application.createRequestMapping());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n"
                        + "Content-Type: text/html;charset=utf-8 \r\n"
                        + "Set-Cookie: JSESSIONID=")
                .contains("\r\nContent-Length: 12 \r\n\r\nHello world!");
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
        final Http11Processor processor = new Http11Processor(socket, new SessionManager(), Application.createRequestMapping());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n"
                        + "Content-Type: text/html;charset=utf-8 \r\n"
                        + "Set-Cookie: JSESSIONID=")
                .contains("\r\nContent-Length: 5564 \r\n\r\n" + responseBody);
    }

    @Test
    void loginSuccessRedirectsToIndex() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager(), Application.createRequestMapping());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n"
                        + "Location: /index.html \r\n"
                        + "Set-Cookie: JSESSIONID=")
                .endsWith("\r\nContent-Length: 0 \r\n\r\n");
    }

    @Test
    void loginFailureRedirectsToUnauthorizedPage() {
        // given
        final String requestBody = "account=gugu&password=wrong";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager(), Application.createRequestMapping());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n"
                        + "Location: /401.html \r\n"
                        + "Set-Cookie: JSESSIONID=")
                .endsWith("\r\nContent-Length: 0 \r\n\r\n");
    }

    @Test
    void registerRedirectsToIndex() {
        // given
        final String requestBody = "account=new-user&password=password&email=new%40example.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + requestBody.length(),
                "",
                requestBody);
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager(), Application.createRequestMapping());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n"
                        + "Location: /index.html \r\n"
                        + "Set-Cookie: JSESSIONID=")
                .endsWith("\r\nContent-Length: 0 \r\n\r\n");
        assertThat(InMemoryUserRepository.findByAccount("new-user")).isPresent();
    }

    @Test
    void doesNotSetCookieWhenRequestAlreadyHasJSessionId() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager(), Application.createRequestMapping());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).doesNotContain("Set-Cookie:");
    }

    @Test
    void redirectsLoggedInUserFromLoginPage() {
        // given
        final String sessionId = "logged-in-session";
        final String loginBody = "account=gugu&password=password";
        final String loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + sessionId,
                "Content-Length: " + loginBody.length(),
                "",
                loginBody);
        final var loginSocket = new StubSocket(loginRequest);
        final SessionManager sessionManager = new SessionManager();
        new Http11Processor(loginSocket, sessionManager, Application.createRequestMapping()).process(loginSocket);

        final String request = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                "");
        final var socket = new StubSocket(request);

        // when
        new Http11Processor(socket, sessionManager, Application.createRequestMapping()).process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n"
                        + "Location: /index.html \r\n")
                .doesNotContain("Set-Cookie:");
    }
    @Test
    void servesLoginPageForAnonymousUser() {
        final var socket = new StubSocket("GET /login?source=test HTTP/1.1\r\n\r\n");
        new Http11Processor(socket, new SessionManager(), Application.createRequestMapping()).process(socket);
        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK").contains("text/html", "<form");
    }

    @Test
    void servesCss() {
        final var socket = new StubSocket("GET /css/styles.css HTTP/1.1\r\n\r\n");
        new Http11Processor(socket, new SessionManager(), Application.createRequestMapping()).process(socket);
        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK").contains("Content-Type: text/css");
    }

    @Test
    void returnsNotFoundForMissingResource() {
        final var socket = new StubSocket("GET /missing.html HTTP/1.1\r\n\r\n");
        new Http11Processor(socket, new SessionManager(), Application.createRequestMapping()).process(socket);
        assertThat(socket.output()).startsWith("HTTP/1.1 404 Not Found");
    }

    @Test
    void addsRouteWithoutChangingProcessor() {
        final Controller hello = (request, response) ->
                response.setBody("Hello " + request.getParameter("name"), "text/plain");
        final var mapping = new RequestMapping(
                Map.of("/hello", hello), new StaticResourceController(new ResourceHandler()));
        final var socket = new StubSocket("GET /hello?name=Kaki HTTP/1.1\r\n\r\n");

        new Http11Processor(socket, new SessionManager(), mapping).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK").endsWith("Hello Kaki");
    }
    @Test
    void respondsWithBadRequestForMalformedRequest() {
        final var socket = new StubSocket("POST /login HTTP/1.1\r\nContent-Length: invalid\r\n\r\n");

        new Http11Processor(socket, new SessionManager(), Application.createRequestMapping()).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 400 Bad Request")
                .endsWith("\r\n\r\nBad Request");
        assertThat(socket.isClosed()).isTrue();
    }

    @Test
    void replacesIncompleteControllerResponseWithServerError() {
        final Controller failing = (request, response) -> {
            response.sendRedirect("/should-not-redirect");
            response.setBody("partial body", "text/html");
            throw new IOException("private failure details");
        };
        final var socket = new StubSocket();
        final var mapping = new RequestMapping(Map.of("/", failing), failing);

        new Http11Processor(socket, new SessionManager(), mapping).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 500 Internal Server Error")
                .endsWith("\r\n\r\nInternal Server Error")
                .doesNotContain("Location:", "partial body", "private failure details");
        assertThat(socket.isClosed()).isTrue();
    }

    @Test
    void preservesApplicationCookiesAlongsideSessionCookie() {
        final Controller controller = (request, response) -> {
            response.setHeader("Set-Cookie", "theme=dark");
            response.addCookie("language", "ko");
        };
        final var socket = new StubSocket();
        final var mapping = new RequestMapping(Map.of("/", controller), controller);

        new Http11Processor(socket, new SessionManager(), mapping).process(socket);

        assertThat(socket.output()).contains("Set-Cookie: theme=dark \r\n",
                "Set-Cookie: language=ko \r\n", "Set-Cookie: JSESSIONID=");
    }

    @Test
    void closedConnectionDoesNotProduceBadRequest() {
        final var socket = new StubSocket("");

        new Http11Processor(socket, new SessionManager(), Application.createRequestMapping()).process(socket);

        assertThat(socket.output()).isEmpty();
    }

    @Test
    void doesNotRetryResponseAfterOutputFailure() {
        final var writes = new java.util.concurrent.atomic.AtomicInteger();
        final var socket = new StubSocket() {
            @Override
            public java.io.OutputStream getOutputStream() {
                return new java.io.OutputStream() {
                    @Override
                    public void write(final int value) throws IOException {
                        writes.incrementAndGet();
                        throw new IOException("Connection lost");
                    }
                };
            }
        };

        new Http11Processor(socket, new SessionManager(), Application.createRequestMapping()).process(socket);

        assertThat(writes.get()).isEqualTo(1);
        assertThat(socket.isClosed()).isTrue();
    }
}
