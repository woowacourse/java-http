package org.apache.coyote.http11;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

class Http11ProcessorTest {

    private final Manager sessionManager = new SessionManager();

    @Test
    void javascriptResourcePreservesItsContentTypeAndBody() throws IOException {
        final var socket = new StubSocket("GET /js/scripts.js HTTP/1.1\r\n\r\n");

        new Http11Processor(socket, sessionManager).process(socket);

        try (var resource = getClass().getClassLoader().getResourceAsStream("static/js/scripts.js")) {
            assertThat(resource).isNotNull();
            final byte[] body = resource.readAllBytes();
            final String[] response = socket.output().split("\r\n\r\n", 2);
            assertThat(response).hasSize(2);
            assertThat(response[0]).contains("Content-Type: text/javascript;charset=utf-8");
            assertThat(response[0]).contains("Content-Length: " + body.length);
            assertThat(response[1]).isEqualTo(new String(body, StandardCharsets.UTF_8));
        }
    }

    @Test
    void missingJavascriptDoesNotWriteASuccessResponse() {
        final var socket = new StubSocket("GET /missing.js HTTP/1.1\r\n\r\n");

        new Http11Processor(socket, sessionManager).process(socket);

        assertThat(socket.output()).isEmpty();
    }

    @Test
    void unknownPathUsesHelloWorldFallback() {
        final var socket = new StubSocket("GET /unknown HTTP/1.1\r\n\r\n");

        new Http11Processor(socket, sessionManager).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK");
        assertThat(socket.output().split("\r\n\r\n", 2)[1]).isEqualTo("Hello world!");
    }

    @Test
    void putLoginStillServesPageForAnAuthenticatedSession() throws IOException {
        final var login = new StubSocket(postLoginRequest("account=gugu&password=password"));
        assertRedirect(login, "/index.html");
        final String sessionId = assertNewSessionCookie(login.output());
        final var socket = new StubSocket(withCookie("PUT /login HTTP/1.1\r\n\r\n", sessionId));

        new Http11Processor(socket, sessionManager).process(socket);

        assertHtmlResponseBody(socket.output(), "static/login.html");
        assertThat(responseHeaders(socket.output())).noneMatch(header -> header.startsWith("Location:"));
    }


    @Test
    void stopsWithoutResponseWhenRequestLineIsMissingOrMalformed() {
        for (String request : List.of("", "\r\n", "GET\r\n\r\n", "GET / HTTP/1.1 EXTRA\r\n\r\n")) {
            var socket = new StubSocket(request);

            new Http11Processor(socket, sessionManager).process(socket);

            assertThat(socket.output()).isEmpty();
        }
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertResponseIgnoringSetCookie(socket.output(), expected);
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
        final Http11Processor processor = new Http11Processor(socket, sessionManager);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertResponseIgnoringSetCookie(socket.output(), expected);
    }

    @Test
    void css() throws IOException {
        final var socket = new StubSocket(
                "GET /css/styles.css HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket, sessionManager).process(socket);

        try (var resource = getClass().getClassLoader()
                .getResourceAsStream("static/css/styles.css")) {

            assertThat(resource).isNotNull();
            final byte[] expectedBody = resource.readAllBytes();
            final String[] response = socket.output().split("\r\n\r\n", 2);

            assertThat(response).hasSize(2);
            assertThat(response[0].split("\r\n")).contains(
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/css;charset=utf-8 ",
                    "Content-Length: " + expectedBody.length + " ");
            assertThat(response[1])
                    .isEqualTo(new String(expectedBody, StandardCharsets.UTF_8));
        }
    }

    @Test
    void login() throws IOException {
        assertHtmlResponse("/login", "static/login.html");
    }

    @Test
    void postLoginRedirectsToIndex() {
        final var socket = new StubSocket(
                postLoginRequest("account=gugu&password=password"));

        new Http11Processor(socket, sessionManager).process(socket);

        assertResponseIgnoringSetCookie(socket.output(),
                "HTTP/1.1 302 Found\r\n"
                        + "Location: /index.html\r\n"
                        + "Content-Length: 0\r\n\r\n");
    }

    @Test
    void malformedEncodedLoginBodyIsNotProcessed() {
        final var socket = new StubSocket(
                postLoginRequest("account=gugu&password=%AZ"));

        new Http11Processor(socket, sessionManager).process(socket);

        assertThat(socket.output()).isEmpty();
    }

    @Test
    void postLoginWithWrongPasswordRedirectsToUnauthorized() {
        final var socket = new StubSocket(
                postLoginRequest("account=gugu&password=wrong"));

        new Http11Processor(socket, sessionManager).process(socket);

        assertResponseIgnoringSetCookie(socket.output(),
                "HTTP/1.1 302 Found\r\n"
                        + "Location: /401.html\r\n"
                        + "Content-Length: 0\r\n\r\n");
    }

    @Test
    void unauthorizedPage() throws IOException {
        final var socket = new StubSocket(
                "GET /401.html HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket, sessionManager).process(socket);

        try (var resource = getClass().getClassLoader()
                .getResourceAsStream("static/401.html")) {
            assertThat(resource).isNotNull();
            final byte[] expectedBody = resource.readAllBytes();

            final String expected =
                    "HTTP/1.1 200 OK \r\n"
                            + "Content-Type: text/html;charset=utf-8 \r\n"
                            + "Content-Length: " + expectedBody.length + " \r\n"
                            + "\r\n"
                            + new String(expectedBody, StandardCharsets.UTF_8);

            assertResponseIgnoringSetCookie(socket.output(), expected);
        }
    }

    @Test
    void getLoginWithQueryDoesNotAuthenticate() throws IOException {
        assertHtmlResponse("/login?account=gugu&password=password", "static/login.html");
    }

    @Test
    void postLoginDoesNotUseCredentialsFromQuery() {
        String request = postRequest("/login?account=gugu&password=password", "note=present");

        assertRedirect(new StubSocket(request), "/401.html");
    }

    @Test
    void postLoginReadsHeadersUntilBlankLine() {
        final String request = postLoginRequest("account=gugu&password=password")
                .replace("Content-Length:", "content-length:")
                .replace("\r\n\r\n", "\r\nX-Note: a:b\r\n\r\n");

        assertRedirect(new StubSocket(request), "/index.html");
    }

    @Test
    void postLoginReadsExactlyContentLengthBytes() {
        final String body = "note=가&account=gugu&password=password";
        final var socket = new StubSocket(postLoginRequest(body) + "XX");

        assertRedirect(socket, "/index.html");
    }

    @Test
    void postLoginReadsFragmentedInput() {
        final String request = postLoginRequest("account=gugu&password=password");
        final var socket = new StubSocket(request) {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8)) {
                    @Override
                    public synchronized int read(byte[] bytes, int offset, int length) {
                        return super.read(bytes, offset, Math.min(length, 3));
                    }
                };
            }
        };

        assertRedirect(socket, "/index.html");
    }

    @Test
    void truncatedPostBodyIsNotProcessed() {
        final String request = postLoginRequest("account=gugu&password=password");
        final var socket = new StubSocket(request.substring(0, request.length() - 1));

        new Http11Processor(socket, sessionManager).process(socket);

        assertThat(socket.output()).isEmpty();
    }

    @Test
    void registerPage() throws IOException {
        assertHtmlResponse("/register", "static/register.html");
    }

    @Test
    void registeredUserCanLogIn() {
        final String body =
                "account=register-user&password=pass%26word&email=moa%40example.com";

        assertRedirect(new StubSocket(postRequest("/register", body)), "/index.html");

        assertThat(InMemoryUserRepository.findByAccount("register-user"))
                .hasValueSatisfying(user ->
                        assertThat(user).usingRecursiveComparison()
                                .isEqualTo(new User(
                                        "register-user", "pass&word", "moa@example.com"
                                )));

        assertRedirect(
                new StubSocket(postLoginRequest("account=register-user&password=pass%26word")),
                "/index.html"
        );
    }

    @Test
    void duplicateRegistrationPreservesOriginalUser() {
        final String account = "duplicate-" + UUID.randomUUID();
        assertRedirect(new StubSocket(postRequest("/register",
                "account=" + account + "&password=original&email=original%40example.com")),
                "/index.html");
        final User original = InMemoryUserRepository.findByAccount(account).orElseThrow();
        final var duplicate = new StubSocket(postRequest("/register",
                "account=" + account + "&password=replacement&email=replacement%40example.com"));

        new Http11Processor(duplicate, sessionManager).process(duplicate);

        final String[] response = duplicate.output().split("\\r\\n\\r\\n", 2);
        final String message = "이미 사용 중인 계정입니다.";
        assertThat(response).hasSize(2);
        assertThat(response[0].split("\\r\\n")).contains(
                "HTTP/1.1 409 Conflict",
                "Content-Type: text/plain;charset=utf-8",
                "Content-Length: " + message.getBytes(StandardCharsets.UTF_8).length);
        assertThat(response[1]).isEqualTo(message);
        assertThat(InMemoryUserRepository.findByAccount(account)).containsSame(original);
        assertRedirect(new StubSocket(postLoginRequest(
                "account=" + account + "&password=original")), "/index.html");
        assertRedirect(new StubSocket(postLoginRequest(
                "account=" + account + "&password=replacement")), "/401.html");
    }

    @Test
    void registrationWithoutPasswordIsNotSaved() {
        final var socket = new StubSocket(postRequest(
                "/register",
                "account=incomplete-register-user&email=moa%40example.com"
        ));

        new Http11Processor(socket, sessionManager).process(socket);

        assertThat(InMemoryUserRepository.findByAccount("incomplete-register-user"))
                .isEmpty();
        assertThat(socket.output()).isEmpty();
    }

    @Test
    void issuesCookieWhenSessionIdIsMissing() {
        for (String cookie : List.of(
                "",
                "theme=dark",
                "JSESSIONID_OTHER=abc123",
                "JSESSIONID="
        )) {
            final String response = getLoginResponse(cookie);

            assertNewSessionCookie(response);
        }
    }

    @Test
    void doesNotReissueExistingSessionCookie() throws IOException {
        final var login = new StubSocket(postLoginRequest("account=gugu&password=password"));
        assertRedirect(login, "/index.html");
        final String sessionId = assertNewSessionCookie(login.output());
        final String response = getLoginResponse(
                "theme=dark; JSESSIONID=" + sessionId + "; language=ko"
        );

        assertThat(responseHeaders(response))
                .noneMatch(header -> header.startsWith("Set-Cookie:"));

        assertThat(sessionManager.findSession(sessionId).getAttribute("user"))
                .isSameAs(InMemoryUserRepository.findByAccount("gugu").orElseThrow());
        assertRedirectResponse(response, "/index.html");
    }

    @Test
    void issuesCookieOnRedirect() {
        final var socket = new StubSocket(
                postLoginRequest("account=gugu&password=password")
        );

        assertRedirect(socket, "/index.html");
        assertNewSessionCookie(socket.output());
    }

    @Test
    void issuesDifferentIdsForSeparateRequestsWithoutCookies() {
        final String firstId = assertNewSessionCookie(getLoginResponse(""));
        final String secondId = assertNewSessionCookie(getLoginResponse(""));

        assertThat(firstId).isNotEqualTo(secondId);
    }

    @Test
    void successfulLoginRenewsSessionAndAuthenticatesFollowingRequests() throws IOException {
        final String beforeId = assertNewSessionCookie(getLoginResponse(""));
        final var socket = new StubSocket(withCookie(
                postLoginRequest("account=gugu&password=password"), beforeId));

        assertRedirect(socket, "/index.html");
        final String afterId = assertNewSessionCookie(socket.output());
        assertThat(afterId).isNotEqualTo(beforeId);

        assertThat(sessionManager.findSession(beforeId)).isNull();
        assertThat(sessionManager.findSession(afterId)).isNotNull();
        assertThat(sessionManager.findSession(afterId).getAttribute("user"))
                .isSameAs(InMemoryUserRepository.findByAccount("gugu").orElseThrow());

        assertRedirectResponse(getLoginResponse("JSESSIONID=" + afterId), "/index.html");

        final String oldIdResponse = getLoginResponse("JSESSIONID=" + beforeId);
        assertHtmlResponseBody(oldIdResponse, "static/login.html");
        final String replacementId = assertNewSessionCookie(oldIdResponse);
        assertThat(replacementId).isNotIn(beforeId, afterId);
        assertThat(sessionManager.findSession(beforeId)).isNull();
        assertThat(sessionManager.findSession(replacementId)).isNull();
    }

    @Test
    void unknownSessionIdReceivesNewCookieWithoutSavingASession() throws IOException {
        final String unknownId = UUID.randomUUID().toString();
        final String response = getLoginResponse("JSESSIONID=" + unknownId);

        assertHtmlResponseBody(response, "static/login.html");
        final String newId = assertNewSessionCookie(response);
        assertThat(newId).isNotEqualTo(unknownId);

        assertThat(sessionManager.findSession(unknownId)).isNull();
        assertThat(sessionManager.findSession(newId)).isNull();
    }

    @Test
    void failedLoginDoesNotCreateASession() throws IOException {
        final String sessionId = assertNewSessionCookie(getLoginResponse(""));
        final var socket = new StubSocket(withCookie(
                postLoginRequest("account=gugu&password=wrong"), sessionId));

        assertRedirect(socket, "/401.html");
        assertThat(sessionManager.findSession(sessionId)).isNull();
        final String replacementId = assertNewSessionCookie(socket.output());
        assertThat(sessionManager.findSession(replacementId)).isNull();

        assertHtmlResponseBody(getLoginResponse("JSESSIONID=" + sessionId), "static/login.html");
    }

    @Test
    void loginStateIsNotSharedWithRequestsWithoutCookies() throws IOException {
        final var socket = new StubSocket(postLoginRequest("account=gugu&password=password"));
        assertRedirect(socket, "/index.html");
        final String authenticatedId = assertNewSessionCookie(socket.output());

        final String anonymousResponse = getLoginResponse("");
        assertHtmlResponseBody(anonymousResponse, "static/login.html");
        final String anonymousId = assertNewSessionCookie(anonymousResponse);
        assertThat(anonymousId).isNotEqualTo(authenticatedId);
        assertThat(sessionManager.findSession(anonymousId)).isNull();

        assertRedirectResponse(getLoginResponse("JSESSIONID=" + authenticatedId), "/index.html");
    }

    @Test
    void anonymousRequestsIssueCookiesWithoutSavingSessions() throws IOException {
        final SessionManager manager = spy(new SessionManager());
        final String account = "anonymous-register-" + UUID.randomUUID();
        final List<String> requests = List.of(
                "GET /login HTTP/1.1\r\nHost: localhost\r\n\r\n",
                "GET /register HTTP/1.1\r\nHost: localhost\r\n\r\n",
                "GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n",
                "GET /css/styles.css HTTP/1.1\r\nHost: localhost\r\n\r\n",
                postLoginRequest("account=gugu&password=wrong"),
                postRequest("/register",
                        "account=" + account + "&password=password&email=moa%40example.com"));

        for (String request : requests) {
            final var socket = new StubSocket(request);
            new Http11Processor(socket, manager).process(socket);

            final String id = assertNewSessionCookie(socket.output());
            assertThat(manager.findSession(id)).isNull();
        }
        verify(manager, never()).add(any());
    }

    @Test
    void successfulReloginInvalidatesThePreviousAuthenticatedSession() throws IOException {
        final var firstLogin = new StubSocket(postLoginRequest("account=gugu&password=password"));
        assertRedirect(firstLogin, "/index.html");
        final String oldId = assertNewSessionCookie(firstLogin.output());
        final var previous = sessionManager.findSession(oldId);
        final var nextLogin = new StubSocket(withCookie(
                postLoginRequest("account=gugu&password=password"), oldId));

        assertRedirect(nextLogin, "/index.html");
        final String nextId = assertNewSessionCookie(nextLogin.output());

        assertThat(nextId).isNotEqualTo(oldId);
        assertThat(sessionManager.findSession(oldId)).isNull();
        assertThatIllegalStateException().isThrownBy(() -> previous.getAttribute("user"));
        assertRedirectResponse(getLoginResponse("JSESSIONID=" + nextId), "/index.html");
    }

    @Test
    void expiredLoginCookieShowsLoginPageWithoutCreatingAnotherSession() {
        final Clock clock = mock(Clock.class);
        final SessionManager manager = new SessionManager(clock);
        final var login = new StubSocket(postLoginRequest("account=gugu&password=password"));
        new Http11Processor(login, manager).process(login);
        final String expiredId = assertNewSessionCookie(login.output());
        when(clock.millis()).thenReturn(Duration.ofMinutes(30).toMillis());
        final var revisit = new StubSocket(withCookie(
                "GET /login HTTP/1.1\r\nHost: localhost\r\n\r\n", expiredId));

        new Http11Processor(revisit, manager).process(revisit);

        assertThat(revisit.output()).startsWith("HTTP/1.1 200 OK");
        assertThat(revisit.output()).doesNotContain("Location: /index.html");
        final String replacementId = assertNewSessionCookie(revisit.output());
        assertThat(replacementId).isNotEqualTo(expiredId);
        assertThat(manager.findSession(expiredId)).isNull();
        assertThat(manager.findSession(replacementId)).isNull();
    }

    @Test
    void sessionInvalidatedAfterLookupStillShowsLoginPage() throws IOException {
        final SessionManager manager = managerInvalidatingAfterLookup();
        final var socket = new StubSocket(withCookie(
                "GET /login HTTP/1.1\r\nHost: localhost\r\n\r\n", "expired-during-request"));

        new Http11Processor(socket, manager).process(socket);

        assertHtmlResponseBody(socket.output(), "static/login.html");
        final String id = assertNewSessionCookie(socket.output());
        assertThat(manager.findSession(id)).isNull();
    }

    @Test
    void sessionInvalidatedAfterLookupStillAllowsSuccessfulLogin() {
        final SessionManager manager = managerInvalidatingAfterLookup();
        final var socket = new StubSocket(withCookie(
                postLoginRequest("account=gugu&password=password"), "expired-during-request"));

        new Http11Processor(socket, manager).process(socket);

        assertRedirectResponse(socket.output(), "/index.html");
        final String id = assertNewSessionCookie(socket.output());
        assertThat(manager.findSession("expired-during-request")).isNull();
        assertThat(manager.findSession(id).getAttribute("user"))
                .isSameAs(InMemoryUserRepository.findByAccount("gugu").orElseThrow());
    }

    private SessionManager managerInvalidatingAfterLookup() {
        final SessionManager manager = new SessionManager() {
            @Override
            public HttpSession findSession(String id) {
                final HttpSession session = super.findSession(id);
                if ("expired-during-request".equals(id) && session != null) {
                    session.invalidate();
                }
                return session;
            }
        };
        final Session session = new Session("expired-during-request", manager);
        session.setAttribute("user", InMemoryUserRepository.findByAccount("gugu").orElseThrow());
        manager.add(session);
        return manager;
    }

    private String getLoginResponse(String cookie) {
        final String cookieHeader = cookie.isEmpty() ? "" : "Cookie: " + cookie + "\r\n";
        final var socket = new StubSocket(
                "GET /login HTTP/1.1\r\nHost: localhost\r\n" + cookieHeader + "\r\n"
        );

        new Http11Processor(socket, sessionManager).process(socket);
        return socket.output();
    }

    private String assertNewSessionCookie(String response) {
        final List<String> cookies = responseHeaders(response).stream()
                .filter(header -> header.startsWith("Set-Cookie:"))
                .toList();

        assertThat(cookies).hasSize(1);

        final String cookie = cookies.get(0);
        final String prefix = "Set-Cookie: JSESSIONID=";
        final String suffix = "; Path=/";
        assertThat(cookie).startsWith(prefix).endsWith(suffix);

        final String id = cookie.substring(prefix.length(), cookie.length() - suffix.length());
        assertThat(UUID.fromString(id).toString()).isEqualTo(id);
        return id;
    }

    private List<String> responseHeaders(String response) {
        final String[] parts = response.split("\r\n\r\n", 2);
        assertThat(parts).hasSize(2);
        return List.of(parts[0].split("\r\n"));
    }

    private void assertResponseIgnoringSetCookie(String actual, String expected) {
        final List<String> headers = responseHeaders(actual).stream()
                .filter(header -> !header.startsWith("Set-Cookie:"))
                .toList();
        final String body = actual.split("\r\n\r\n", 2)[1];

        assertThat(String.join("\r\n", headers) + "\r\n\r\n" + body)
                .isEqualTo(expected);
    }

    private String postLoginRequest(String body) {
        return postRequest("/login", body);
    }

    private String withCookie(String request, String sessionId) {
        final int headerEnd = request.indexOf("\r\n\r\n");
        return request.substring(0, headerEnd)
                + "\r\nCookie: JSESSIONID=" + sessionId
                + request.substring(headerEnd);
    }

    private String postRequest(String path, String body) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Host: localhost",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
    }

    private void assertRedirect(StubSocket socket, String location) {
        new Http11Processor(socket, sessionManager).process(socket);
        assertRedirectResponse(socket.output(), location);
    }

    private void assertRedirectResponse(String response, String location) {
        assertResponseIgnoringSetCookie(response,
                "HTTP/1.1 302 Found\r\n"
                        + "Location: " + location + "\r\n"
                        + "Content-Length: 0\r\n\r\n");
    }

    private void assertHtmlResponse(String requestTarget, String resourceName) throws IOException {
        final var socket = new StubSocket(
                "GET " + requestTarget + " HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket, sessionManager).process(socket);
        assertHtmlResponseBody(socket.output(), resourceName);
    }

    private void assertHtmlResponseBody(String actual, String resourceName) throws IOException {
        try (var resource = getClass().getClassLoader()
                .getResourceAsStream(resourceName)) {

            assertThat(resource).isNotNull();
            final byte[] expectedBody = resource.readAllBytes();
            final String[] response = actual.split("\r\n\r\n", 2);

            assertThat(response).hasSize(2);
            assertThat(response[0].split("\r\n")).contains(
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + expectedBody.length + " ");
            assertThat(response[1])
                    .isEqualTo(new String(expectedBody, StandardCharsets.UTF_8));
        }
    }

    private List<String> loginSuccessLogs(String body) {
        final Logger logger =
                (Logger) LoggerFactory.getLogger(Http11Processor.class);
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.setContext(logger.getLoggerContext());
        appender.start();
        logger.addAppender(appender);

        try {
            final var socket = new StubSocket(postLoginRequest(body));

            new Http11Processor(socket, sessionManager).process(socket);

            return appender.list.stream()
                    .map(ILoggingEvent::getFormattedMessage)
                    .filter(message -> message.startsWith("회원 조회 성공: "))
                    .toList();
        } finally {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void matchingCredentialsLogSuccess() {
        assertThat(loginSuccessLogs("account=gugu&password=password"))
                .containsExactly("회원 조회 성공: gugu");
    }

    @Test
    void wrongPasswordDoesNotLogSuccess() {
        assertThat(loginSuccessLogs("account=gugu&password=wrong"))
                .isEmpty();
    }

    @Test
    void encodedBodyInDifferentOrderLogsSuccess() {
        assertThat(loginSuccessLogs("password=pass%77ord&account=%67ugu"))
                .containsExactly("회원 조회 성공: gugu");
    }

    @Test
    void missingPasswordDoesNotLogSuccess() {
        assertThat(loginSuccessLogs("account=gugu"))
                .isEmpty();
    }
}
