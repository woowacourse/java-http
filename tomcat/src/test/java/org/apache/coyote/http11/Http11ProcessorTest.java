package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

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
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("Content-Length: 12\r\n")
                .contains("Set-Cookie: JSESSIONID=")
                .endsWith("\r\n\r\nHello world!");
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
        final byte[] responseBody;
        try (final var resource = getClass().getClassLoader()
                .getResourceAsStream("static/index.html")) {
            responseBody = resource.readAllBytes();
        }

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("Content-Length: " + responseBody.length + "\r\n")
                .contains("Set-Cookie: JSESSIONID=")
                .endsWith(new String(responseBody, StandardCharsets.UTF_8));
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] responseBody;
        try (final var resource = getClass().getClassLoader()
                .getResourceAsStream("static/css/styles.css")) {
            responseBody = resource.readAllBytes();
        }

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/css;charset=utf-8\r\n")
                .contains("Content-Length: " + responseBody.length + "\r\n")
                .contains("Set-Cookie: JSESSIONID=")
                .endsWith(new String(responseBody, StandardCharsets.UTF_8));
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] responseBody;
        try (final var resource = getClass().getClassLoader()
                .getResourceAsStream("static/login.html")) {
            responseBody = resource.readAllBytes();
        }

        assertThat(socket.output())
                .contains("HTTP/1.1 200 OK")
                .endsWith(new String(responseBody, StandardCharsets.UTF_8));
    }

    @Test
    void redirectToIndexWhenLoginSucceeds() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    void redirectToUnauthorizedPageWhenLoginFails() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=wrong HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /401.html");
    }

    @Test
    void registerUserFromPostData() {
        // given
        final String account = "new-user";
        final String requestBody =
                "account=new%2Duser&password=password&email=new-user%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                requestBody);

        assertThat(InMemoryUserRepository.findByAccount(account)).isEmpty();
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final var savedUser = InMemoryUserRepository.findByAccount(account);
        assertThat(savedUser).hasValueSatisfying(user -> {
            assertThat(user.getAccount()).isEqualTo(account);
            assertThat(user.checkPassword("password")).isTrue();
        });
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    void redirectToRegisterWhenRegistrationDataIsBlank() {
        // given
        final String account = "blank-user";
        final String requestBody =
                "account=" + account + "&password=%20%20%20&email=blank-user%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                requestBody);
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(InMemoryUserRepository.findByAccount(account)).isEmpty();
        assertThat(socket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /register");
    }

    @Test
    void doesNotRegisterUserForGetRequest() {
        // given
        final String account = "get-user";
        final String httpRequest = String.join("\r\n",
                "GET /register?account=" + account + "&password=password&email=get-user%40woowahan.com HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(InMemoryUserRepository.findByAccount(account)).isEmpty();
        assertThat(socket.output()).contains("HTTP/1.1 200 OK");
    }

    @Test
    void keepLoginStateWithJsessionId() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);
        final var loginSocket = new StubSocket(loginRequest);

        // when
        new Http11Processor(loginSocket).process(loginSocket);

        // then
        assertThat(loginSocket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html")
                .contains("Set-Cookie: JSESSIONID=");

        final String sessionId = extractSessionId(loginSocket.output());
        final String loginPageRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=" + sessionId,
                "",
                "");
        final var loginPageSocket = new StubSocket(loginPageRequest);

        new Http11Processor(loginPageSocket).process(loginPageSocket);

        assertThat(loginPageSocket.output())
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html")
                .doesNotContain("Set-Cookie: JSESSIONID=");
    }

    @Test
    void manageSessionAttributesAndInvalidation() {
        // given
        final String sessionId = "session-for-manager-test";
        final var session = Http11Processor.SessionManager.getOrCreate(sessionId);

        // when & then
        session.setAttribute("user", "gugu");
        assertThat(session.getId()).isEqualTo(sessionId);
        assertThat(session.getAttribute("user")).isEqualTo("gugu");
        assertThat(Http11Processor.SessionManager.findById(sessionId)).contains(session);

        session.removeAttribute("user");
        assertThat(session.getAttribute("user")).isNull();

        session.invalidate();
        assertThat(Http11Processor.SessionManager.findById(sessionId)).isEmpty();
    }

    private String extractSessionId(final String response) {
        final String prefix = "Set-Cookie: JSESSIONID=";
        final int start = response.indexOf(prefix) + prefix.length();
        final int end = response.indexOf("\r\n", start);
        return response.substring(start, end);
    }

}
