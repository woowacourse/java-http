package org.apache.coyote.http11;

import com.techcourse.Application;
import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void processesApplicationProvidedController() {
        final Controller controller = (request, response) -> response.body("Hello " + request.getParameter("name"));
        final var mapping = new RequestMapping(Map.of("/custom", controller), controller);
        final var socket = new StubSocket(get("/custom?name=java", ""));

        new Http11Processor(socket, mapping).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK");
        assertThat(body(socket.output())).isEqualTo("Hello java");
        assertThat(header(socket.output(), "Set-Cookie")).startsWith("JSESSIONID=");
    }

    @Nested
    @DisplayName("step1 - HTTP 요청과 정적 파일 응답")
    class Step1 {

        @Test
        void root() {
            // given
            final var socket = new StubSocket();
            final var processor = new Http11Processor(socket, Application.createRequestMapping());

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).startsWith("HTTP/1.1 200 OK");
            assertThat(header(socket.output(), "Content-Type")).isEqualTo("text/html;charset=utf-8");
            assertThat(header(socket.output(), "Content-Length")).isEqualTo("12");
            assertThat(body(socket.output())).isEqualTo("Hello world!");
        }

        @Test
        void index() throws IOException {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket, Application.createRequestMapping());

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).startsWith("HTTP/1.1 200 OK");
            assertThat(body(socket.output())).isEqualTo(resource("index.html"));
            assertThat(header(socket.output(), "Content-Length"))
                    .isEqualTo(String.valueOf(resource("index.html").getBytes(StandardCharsets.UTF_8).length));
        }

        @Test
        void css() throws IOException {
            // when
            final var response = process(get("/css/styles.css", ""));

            // then
            assertThat(response).startsWith("HTTP/1.1 200 OK");
            assertThat(header(response, "Content-Type")).isEqualTo("text/css;charset=utf-8");
            assertThat(body(response)).isEqualTo(resource("css/styles.css"));
        }

        @Test
        void javascript() throws IOException {
            // when
            final var response = process(get("/js/scripts.js", ""));

            // then
            assertThat(response).startsWith("HTTP/1.1 200 OK");
            assertThat(header(response, "Content-Type")).isEqualTo("application/javascript;charset=utf-8");
            assertThat(body(response)).isEqualTo(resource("js/scripts.js"));
        }

        @Test
        void indexWithQueryString() throws IOException {
            // when
            final var response = process(get("/index.html?account=gugu", ""));

            // then
            assertThat(response).startsWith("HTTP/1.1 200 OK");
            assertThat(body(response)).isEqualTo(resource("index.html"));
        }
    }

    @Nested
    @DisplayName("step2 - 로그인과 회원가입, 쿠키와 세션")
    class Step2 {

        @Test
        void loginPage() throws IOException {
            final var response = process(get("/login", ""));

            assertThat(response).startsWith("HTTP/1.1 200 OK");
            assertThat(body(response)).isEqualTo(resource("login.html"));
            assertThat(body(response)).contains("<form method=\"post\" action=\"login\">");
        }

        @Test
        void loginSuccess() {
            final var response = process(post("/login", "account=gugu&password=password", ""));

            assertRedirect(response, "/index.html");
            assertThat(header(response, "Set-Cookie")).startsWith("JSESSIONID=");
        }

        @Test
        void loginFailure() {
            final var response = process(post("/login", "account=gugu&password=wrong", ""));

            assertRedirect(response, "/401.html");
            final var nextResponse = process(get("/login", sessionCookie(response)));
            assertThat(nextResponse).startsWith("HTTP/1.1 200 OK");
        }

        @Test
        void loginWithUnknownAccount() {
            final var response = process(post("/login", "account=unknown&password=password", ""));

            assertRedirect(response, "/401.html");
        }

        @Test
        void loginWithoutPassword() {
            final var response = process(post("/login", "account=gugu", ""));

            assertRedirect(response, "/401.html");
        }

        @Test
        void getDoesNotLogIn() {
            final var response = process(get("/login?account=gugu&password=password", ""));
            final var nextResponse = process(get("/login", sessionCookie(response)));

            assertThat(response).startsWith("HTTP/1.1 200 OK");
            assertThat(nextResponse).startsWith("HTTP/1.1 200 OK");
        }

        @Test
        void unauthorizedPage() throws IOException {
            final var response = process(get("/401.html", ""));

            assertThat(body(response)).isEqualTo(resource("401.html"));
        }

        @Test
        void registerPage() throws IOException {
            final var response = process(get("/register", ""));

            assertThat(response).startsWith("HTTP/1.1 200 OK");
            assertThat(body(response)).isEqualTo(resource("register.html"));
        }

        @Test
        void registerAndLogIn() {
            final var account = "new-user-" + UUID.randomUUID();
            final var response = process(post("/register",
                    "account=" + account + "&password=p%2Bass%3Dword&email=user%40example.com", ""));

            assertRedirect(response, "/index.html");
            final var loginResponse = process(post("/login",
                    "account=" + account + "&password=p%2Bass%3Dword", sessionCookie(response)));
            assertRedirect(loginResponse, "/index.html");
        }

        @Test
        void getDoesNotRegister() {
            final var account = "get-user-" + UUID.randomUUID();
            process(get("/register?account=" + account + "&password=password&email=user%40example.com", ""));

            final var response = process(post("/login", "account=" + account + "&password=password", ""));

            assertRedirect(response, "/401.html");
        }

        @Test
        void createsSessionCookie() {
            final var response = process(get("/index.html", "yummy_cookie=choco; tasty_cookie=strawberry"));

            final var cookie = sessionCookie(response);
            assertThat(cookie).startsWith("JSESSIONID=");
            assertThat(UUID.fromString(cookie.substring("JSESSIONID=".length())).toString())
                    .isEqualTo(cookie.substring("JSESSIONID=".length()));
        }

        @Test
        void reusesSessionCookie() {
            final var firstResponse = process(get("/login", ""));
            final var response = process(get("/index.html", "yummy_cookie=choco; " + sessionCookie(firstResponse)));

            assertThat(header(response, "Set-Cookie")).isNull();
        }

        @Test
        void loggedInUserIsRedirected() {
            final var response = process(post("/login", "account=gugu&password=password", ""));
            final var nextResponse = process(get("/login", "yummy_cookie=choco; " + sessionCookie(response)));

            assertRedirect(nextResponse, "/index.html");
        }

        @Test
        void storesUserInSession() {
            final var response = process(post("/login", "account=gugu&password=password", ""));
            final var sessionId = sessionCookie(response).substring("JSESSIONID=".length());

            final var session = SessionManager.getInstance().findSession(sessionId);

            assertThat(session.getAttribute("user")).isSameAs(InMemoryUserRepository.findByAccount("gugu").orElseThrow());
        }

        @Test
        void invalidatedSessionCannotAccessLoginAsAuthenticatedUser() {
            final var loginResponse = process(post("/login", "account=gugu&password=password", ""));
            final var cookie = sessionCookie(loginResponse);
            SessionManager.getInstance().findSession(cookie.substring("JSESSIONID=".length())).invalidate();

            final var response = process(get("/login", cookie));

            assertThat(response).startsWith("HTTP/1.1 200 OK");
            assertThat(sessionCookie(response)).isNotEqualTo(cookie);
        }

        @Test
        void registerWithoutRequiredFields() {
            final var response = process(post("/register", "", ""));

            assertRedirect(response, "/register");
        }

        @Test
        void registerWithEmptyPassword() {
            final var account = "empty-password-" + UUID.randomUUID();
            final var response = process(post("/register",
                    "account=" + account + "&password=&email=user%40example.com", ""));

            assertRedirect(response, "/register");
            assertThat(InMemoryUserRepository.findByAccount(account)).isEmpty();
        }

        @Test
        void rotatesSessionAfterSuccessfulLogin() {
            final var oldCookie = sessionCookie(process(get("/login", "")));
            final var response = process(post("/login", "account=gugu&password=password", oldCookie));
            final var newCookie = sessionCookie(response);

            assertRedirect(response, "/index.html");
            assertThat(newCookie).isNotEqualTo(oldCookie);
            assertThat(SessionManager.getInstance().findSession(oldCookie.substring("JSESSIONID=".length())))
                    .isNull();
            assertRedirect(process(get("/login", newCookie)), "/index.html");
            assertThat(process(get("/login", oldCookie))).startsWith("HTTP/1.1 200 OK");
        }

        @Test
        void failedLoginKeepsExistingSession() {
            final var cookie = sessionCookie(process(get("/login", "")));
            final var response = process(post("/login", "account=gugu&password=wrong", cookie));

            assertRedirect(response, "/401.html");
            assertThat(header(response, "Set-Cookie")).isNull();
            assertThat(SessionManager.getInstance().findSession(cookie.substring("JSESSIONID=".length()))
                    .getAttribute("user")).isNull();
        }

        @Test
        void sessionsAreIsolated() {
            process(post("/login", "account=gugu&password=password", ""));

            final var response = process(get("/login", ""));

            assertThat(response).startsWith("HTTP/1.1 200 OK");
        }

        @Test
        void unknownSessionIsReplaced() {
            final var response = process(get("/login", "JSESSIONID=unknown-session"));

            assertThat(response).startsWith("HTTP/1.1 200 OK");
            assertThat(sessionCookie(response)).startsWith("JSESSIONID=").isNotEqualTo("JSESSIONID=unknown-session");
        }
    }

    private String process(final String request) {
        final var socket = new StubSocket(request);
        new Http11Processor(socket, Application.createRequestMapping()).process(socket);
        return socket.output();
    }

    private String get(final String path, final String cookie) {
        return "GET " + path + " HTTP/1.1\r\nHost: localhost:8080\r\nCookie: " + cookie + "\r\n\r\n";
    }

    private String post(final String path, final String body, final String cookie) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Cookie: " + cookie,
                "",
                body);
    }

    private String header(final String response, final String name) {
        return response.split("\r\n\r\n", 2)[0].lines()
                .filter(line -> line.startsWith(name + ":"))
                .map(line -> line.substring(name.length() + 1).trim())
                .findFirst()
                .orElse(null);
    }

    private String body(final String response) {
        return response.split("\r\n\r\n", 2)[1];
    }

    private String sessionCookie(final String response) {
        final var cookie = header(response, "Set-Cookie");
        assertThat(cookie).isNotNull();
        return cookie.split(";", 2)[0];
    }

    private void assertRedirect(final String response, final String location) {
        assertThat(response).startsWith("HTTP/1.1 302 Found");
        assertThat(header(response, "Location")).isEqualTo(location);
        assertThat(header(response, "Content-Length")).isEqualTo("0");
        assertThat(body(response)).isEmpty();
    }

    private String resource(final String name) throws IOException {
        try (final var inputStream = getClass().getClassLoader().getResourceAsStream("static/" + name)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
