package org.apache.coyote.http11;

import com.techcourse.controller.ApplicationAdapter;
import com.techcourse.controller.RequestMapping;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class Http11ProcessorTest {

    private Http11Processor processor(StubSocket socket) {
        return new Http11Processor(socket, new ApplicationAdapter(new RequestMapping()));
    }

    @Test
    void successfulLoginRedirectsToIndex() {
        assertLoginRedirect("account=gugu&password=password", "/index.html");
    }

    @Test
    void failedLoginRedirectsToUnauthorizedPage() {
        assertLoginRedirect("account=gugu&password=wrong", "/401.html");
        assertLoginRedirect("account=unknown&password=password", "/401.html");
        assertLoginRedirect("account=gugu", "/401.html");
        assertLoginRedirect("password=password", "/401.html");
        assertLoginRedirect("account=&password=", "/401.html");
    }

    @Test
    void loginWithoutCredentialsServesLoginPage() throws IOException {
        final var socket = new StubSocket(getRequest("/login"));

        processor(socket).process(socket);

        try (var resource = getClass().getClassLoader().getResourceAsStream("static/login.html")) {
            assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n")
                    .doesNotContain("Location:")
                    .endsWith(new String(resource.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    private void assertLoginRedirect(String body, String location) {
        String sessionId = UUID.randomUUID().toString();
        SessionManager.getInstance().add(new Session(sessionId));
        final var socket = new StubSocket(postRequest("/login", body, sessionId));

        processor(socket).process(socket);

        if (location.equals("/index.html")) {
            assertThat(socket.output())
                    .startsWith("HTTP/1.1 302 Found\r\nSet-Cookie: JSESSIONID=")
                    .contains("Location: /index.html\r\n")
                    .endsWith("Content-Length: 0\r\n\r\n");
        } else {
            assertRedirect(socket, location);
        }
    }

    @Test
    void loginWithUnknownSessionIdUsesServerGeneratedSessionId() {
        String unknownSessionId = UUID.randomUUID().toString();
        String body = "account=gugu&password=password";
        final var socket = new StubSocket(postRequest("/login", body, unknownSessionId));

        processor(socket).process(socket);

        String issuedSessionId = socket.output()
                .lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .findFirst()
                .orElseThrow();

        assertThat(issuedSessionId).isNotEqualTo(unknownSessionId);
        assertThatCode(() -> UUID.fromString(issuedSessionId)).doesNotThrowAnyException();
        assertThat(SessionManager.getInstance().findSession(unknownSessionId)).isNull();
        Session session = SessionManager.getInstance().findSession(issuedSessionId);
        assertThat(session).isNotNull();
        assertThat(session.getAttribute("user")).isInstanceOf(User.class);
    }

    @Test
    void loginWithKnownSessionIdReplacesIt() {
        String previousId = UUID.randomUUID().toString();
        Session previousSession = new Session(previousId);
        previousSession.setAttribute("user", new User("attacker", "password", "attacker@example.com"));
        SessionManager.getInstance().add(previousSession);
        final var socket = new StubSocket(postRequest(
                "/login", "account=gugu&password=password", previousId));

        processor(socket).process(socket);

        String newSessionId = issuedSessionId(socket);
        assertThat(newSessionId).isNotEqualTo(previousId);
        assertThat(SessionManager.getInstance().findSession(previousId)).isNull();
        assertThat(previousSession.getAttribute("user")).isNull();
        assertThat(SessionManager.getInstance().findSession(newSessionId).getAttribute("user"))
                .isInstanceOf(User.class)
                .extracting(user -> ((User) user).getAccount())
                .isEqualTo("gugu");
    }

    @Test
    void failedLoginKeepsExistingSession() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        SessionManager.getInstance().add(session);
        final var socket = new StubSocket(postRequest(
                "/login", "account=gugu&password=wrong", sessionId));

        processor(socket).process(socket);

        assertRedirect(socket, "/401.html");
        assertThat(SessionManager.getInstance().findSession(sessionId)).isSameAs(session);
    }

    @Test
    void loginWithoutCookieCreatesSessionAndKeepsRequestCookieAbsent() {
        String body = "account=gugu&password=password";
        final var socket = new StubSocket(String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                "",
                body
        ));

        processor(socket).process(socket);

        String sessionId = socket.output().lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .findFirst().orElseThrow();
        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(SessionManager.getInstance().findSession(sessionId).getAttribute("user"))
                .isInstanceOf(User.class);
    }

    @Test
    void requestCookieContainsOnlyClientValues() {
        final var socket = new StubSocket("GET / HTTP/1.1\r\n\r\n");
        Http11Processor processor = new Http11Processor(socket, (request, response) ->
                assertThat(request.getCookie(HttpCookie.JSESSION_ID)).isEmpty());

        processor.process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\nSet-Cookie: JSESSIONID=");
    }

    @Test
    void sessionEndpointWithoutCookieReturnsLoggedOutState() {
        final var socket = new StubSocket("GET /session HTTP/1.1\r\n\r\n");

        processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n")
                .endsWith("{\"loggedIn\":false}");
    }

    @Test
    void registerPageIsServedForGetRequest() throws IOException {
        final var socket = new StubSocket(getRequest("/register"));

        processor(socket).process(socket);

        try (var resource = getClass().getClassLoader().getResourceAsStream("static/register.html")) {
            assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n")
                    .endsWith(new String(resource.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
        }
    }

    @Test
    void postRegisterSavesUserAndRedirectsToIndex() {
        String body = "account=new-user&password=pass%3Dword&email=user%40example.com";
        final var socket = new StubSocket(postRequest("/register", body));

        processor(socket).process(socket);

        assertRedirect(socket, "/index.html");
        assertThat(InMemoryUserRepository.findByAccount("new-user"))
                .hasValueSatisfying(user -> assertThat(user.checkPassword("pass=word")).isTrue());
    }

    @Test
    void duplicateAccountRegistrationReturnsConflictWithoutOverwritingUser() {
        String body = "account=gugu&password=changed&email=changed%40example.com";
        final var socket = new StubSocket(postRequest("/register", body));

        processor(socket).process(socket);

        assertThat(socket.output()).isEqualTo("HTTP/1.1 409 Conflict\r\n"
                + "Content-Type: text/plain;charset=utf-8\r\n"
                + "Content-Length: 22\r\n"
                + "\r\n"
                + "Account already exists");
        assertThat(InMemoryUserRepository.findByAccount("gugu"))
                .hasValueSatisfying(user -> {
                    assertThat(user.checkPassword("password")).isTrue();
                    assertThat(user.checkPassword("changed")).isFalse();
                });
    }

    @Test
    void registrationRejectsMissingOrBlankRequiredFields() {
        String[] invalidBodies = {
                "password=password&email=user%40example.com",
                "account=+++&password=password&email=user%40example.com",
                "account=missing-password&email=user%40example.com",
                "account=blank-password&password=+++&email=user%40example.com",
                "account=missing-email&password=password",
                "account=blank-email&password=password&email=+++"
        };

        for (String body : invalidBodies) {
            final var socket = new StubSocket(postRequest("/register", body));

            processor(socket).process(socket);

            assertThat(socket.output())
                    .startsWith("HTTP/1.1 400 Bad Request\r\n")
                    .endsWith("Required fields must not be blank");
        }

        assertThat(InMemoryUserRepository.findByAccount("   ")).isEmpty();
        assertThat(InMemoryUserRepository.findByAccount("missing-password")).isEmpty();
        assertThat(InMemoryUserRepository.findByAccount("blank-password")).isEmpty();
        assertThat(InMemoryUserRepository.findByAccount("missing-email")).isEmpty();
        assertThat(InMemoryUserRepository.findByAccount("blank-email")).isEmpty();
    }

    @Test
    void malformedRequestReturnsBadRequest() {
        String request = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Length: invalid",
                "",
                "body"
        );
        final var socket = new StubSocket(request);

        processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 400 Bad Request\r\n")
                .endsWith("Bad Request");
    }

    @Test
    void unsupportedMethodReturnsNotImplemented() {
        final var socket = new StubSocket("PUT / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");

        processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 501 Not Implemented\r\n")
                .endsWith("Not Implemented");
    }

    @Test
    void getOnPostOnlyResourceListsAllowedMethod() {
        final var socket = new StubSocket(getRequest("/logout"));

        processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 405 Method Not Allowed\r\n")
                .contains("Allow: POST\r\n");
    }

    @Test
    void controllerFailureReturnsInternalServerError() {
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("user", new User(null, null, null));
        SessionManager.getInstance().add(session);
        final var socket = new StubSocket(getRequest("/session", sessionId));

        processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 500 Internal Server Error\r\n")
                .endsWith("Internal Server Error");
    }

    @Test
    void controllerFailurePreservesNewSessionCookieAndClearsPartialResponse() {
        final var socket = new StubSocket("GET / HTTP/1.1\r\n\r\n");
        Http11Processor processor = new Http11Processor(socket, (request, response) -> {
            response.setHeader("Location", "/incomplete");
            throw new IllegalStateException("controller failed");
        });

        processor.process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 500 Internal Server Error\r\nSet-Cookie: JSESSIONID=")
                .doesNotContain("Location:")
                .endsWith("Internal Server Error");
    }

    private String postRequest(String path, String body) {
        return postRequest(path, body, UUID.randomUUID().toString());
    }

    private String postRequest(String path, String body, String sessionId) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(java.nio.charset.StandardCharsets.UTF_8).length,
                "Cookie: JSESSIONID=" + sessionId,
                "",
                body
        );
    }

    private String getRequest(String path) {
        return getRequest(path, UUID.randomUUID().toString());
    }

    private String getRequest(String path, String sessionId) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                ""
        );
    }

    private void assertRedirect(StubSocket socket, String location) {
        assertThat(socket.output()).isEqualTo("HTTP/1.1 302 Found\r\n"
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n\r\n");
    }

    private String issuedSessionId(StubSocket socket) {
        return socket.output().lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .findFirst()
                .orElseThrow();
    }

    @Test
    void missingResourceReturnsNotFound() {
        final var socket = new StubSocket(getRequest("/missing.html"));
        final var processor = processor(socket);

        processor.process(socket);

        var expected = String.join("\r\n",
                "HTTP/1.1 404 Not Found",
                "Content-Type: text/plain;charset=utf-8",
                "Content-Length: 9",
                "",
                "Not Found");
        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket(getRequest("/"));
        final var processor = processor(socket);

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
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-session-id",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(
                Files.readAllBytes(new File(resource.getFile()).toPath()),
                java.nio.charset.StandardCharsets.UTF_8
        );
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: " + responseBody.getBytes(java.nio.charset.StandardCharsets.UTF_8).length + "\r\n" +
                "\r\n"+
                responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void servesJavaScriptWithJavaScriptContentType() {
        final var socket = new StubSocket(getRequest("/js/scripts.js"));

        processor(socket).process(socket);

        assertThat(socket.output()).contains("Content-Type: text/javascript;charset=utf-8");
    }

    @Test
    void servesSvgWithSvgContentType() {
        final var socket = new StubSocket(getRequest("/assets/img/error-404-monochrome.svg"));

        processor(socket).process(socket);

        assertThat(socket.output()).contains("Content-Type: image/svg+xml;charset=utf-8");
    }

    @Test
    void responseSetsJSessionIdWhenRequestDoesNotHaveOne() {
        final var socket = new StubSocket("GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n");

        processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\nSet-Cookie: JSESSIONID=");

        String sessionId = socket.output()
                .lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .findFirst()
                .orElseThrow();
        assertThatCode(() -> UUID.fromString(sessionId)).doesNotThrowAnyException();
    }

    @Test
    void responseDoesNotSetJSessionIdWhenRequestAlreadyHasOne() {
        final var socket = new StubSocket(getRequest("/"));

        processor(socket).process(socket);

        assertThat(socket.output()).doesNotContain("Set-Cookie:");
    }

    @Test
    void successfulLoginStoresUserInSession() {
        String sessionId = UUID.randomUUID().toString();
        String body = "account=gugu&password=password";
        SessionManager.getInstance().add(new Session(sessionId));
        final var socket = new StubSocket(postRequest("/login", body, sessionId));

        processor(socket).process(socket);

        String newSessionId = issuedSessionId(socket);
        assertThat(newSessionId).isNotEqualTo(sessionId);
        assertThat(SessionManager.getInstance().findSession(sessionId)).isNull();
        Session session = SessionManager.getInstance().findSession(newSessionId);
        assertThat(session).isNotNull();
        assertThat(session.getAttribute("user"))
                .isInstanceOf(User.class)
                .extracting(user -> ((User) user).getAccount())
                .isEqualTo("gugu");
    }

    @Test
    void loggedInUserIsRedirectedWhenAccessingLoginPage() {
        String sessionId = UUID.randomUUID().toString();
        String body = "account=gugu&password=password";
        SessionManager.getInstance().add(new Session(sessionId));
        final var loginSocket = new StubSocket(postRequest("/login", body, sessionId));
        processor(loginSocket).process(loginSocket);

        final var loginPageSocket = new StubSocket(getRequest("/login", issuedSessionId(loginSocket)));
        processor(loginPageSocket).process(loginPageSocket);

        assertRedirect(loginPageSocket, "/index.html");
    }

    @Test
    void failedLoginDoesNotCreateSession() {
        String sessionId = UUID.randomUUID().toString();
        String body = "account=gugu&password=wrong";
        final var socket = new StubSocket(postRequest("/login", body, sessionId));

        processor(socket).process(socket);

        assertThat(SessionManager.getInstance().findSession(sessionId)).isNull();
    }

    @Test
    void sessionEndpointReturnsLoginState() {
        String sessionId = UUID.randomUUID().toString();
        String body = "account=gugu&password=password";
        SessionManager.getInstance().add(new Session(sessionId));
        final var loginSocket = new StubSocket(postRequest("/login", body, sessionId));
        processor(loginSocket).process(loginSocket);

        final var sessionSocket = new StubSocket(getRequest("/session", issuedSessionId(loginSocket)));
        processor(sessionSocket).process(sessionSocket);

        assertThat(sessionSocket.output()).endsWith("{\"loggedIn\":true,\"account\":\"gugu\"}");
    }

    @Test
    void logoutInvalidatesSession() {
        String sessionId = UUID.randomUUID().toString();
        String body = "account=gugu&password=password";
        SessionManager.getInstance().add(new Session(sessionId));
        final var loginSocket = new StubSocket(postRequest("/login", body, sessionId));
        processor(loginSocket).process(loginSocket);

        String authenticatedSessionId = issuedSessionId(loginSocket);
        final var logoutSocket = new StubSocket(postRequest("/logout", "", authenticatedSessionId));
        processor(logoutSocket).process(logoutSocket);

        assertThat(logoutSocket.output()).isEqualTo("HTTP/1.1 204 No Content\r\n\r\n");
        assertThat(SessionManager.getInstance().findSession(authenticatedSessionId)).isNull();

        final var sessionSocket = new StubSocket(getRequest("/session", authenticatedSessionId));
        processor(sessionSocket).process(sessionSocket);
        assertThat(sessionSocket.output()).endsWith("{\"loggedIn\":false}");
    }
}
