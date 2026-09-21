package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
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
        final Http11Processor processor = new Http11Processor(socket);

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
        final Http11Processor processor = new Http11Processor(socket);

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
        final Http11Processor processor = new Http11Processor(socket);

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
        final Http11Processor processor = new Http11Processor(socket);

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
        final Http11Processor processor = new Http11Processor(socket);

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
        new Http11Processor(loginSocket).process(loginSocket);

        final String request = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                "");
        final var socket = new StubSocket(request);

        // when
        new Http11Processor(socket).process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n"
                        + "Location: /index.html \r\n")
                .doesNotContain("Set-Cookie:");
    }
}
