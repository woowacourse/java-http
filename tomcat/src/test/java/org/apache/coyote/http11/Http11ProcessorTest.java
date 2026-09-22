package org.apache.coyote.http11;

import com.techcourse.web.RequestMapping;
import com.techcourse.service.ApplicationService;
import com.techcourse.web.ApplicationAdapter;
import com.techcourse.web.StaticResourceHandler;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;
import org.apache.coyote.HttpResponse;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import com.techcourse.model.User;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void emptyRequest() {
        // given
        final var socket = new StubSocket("");
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEmpty();
    }

    @Test
    void badRequest() {
        // given
        final var socket = new StubSocket("GET /index.html\r\n\r\n");
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        var expected = createResponse(
                "400 Bad Request",
                "text/html;charset=utf-8",
                "400 Bad Request"
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void process() {
        // given
        final var socket = new StubSocket("GET / HTTP/1.1\r\nCookie: JSESSIONID=existing-session\r\n\r\n");
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        var expected = createResponse("text/html;charset=utf-8", "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=existing-session",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        String responseBody = readResource("static/index.html");
        var expected = createResponse("text/html;charset=utf-8", responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginFail() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=wrong-password HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        var expected = createRedirectResponse("/401.html");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(readResource("static/index.html"))
                .contains("<link href=\"css/styles.css\" rel=\"stylesheet\" />");

        String responseBody = readResource("static/css/styles.css");
        var expected = createResponse("text/css;charset=utf-8", responseBody);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void login() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        var expected = createRedirectResponse("/index.html");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /not-found.css HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-session",
                "Connection: keep-alive",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = createProcessor(socket);

        // when
        processor.process(socket);

        // then
        String responseBody = readResource("static/404.html");
        var expected = createResponse(
                "404 Not Found",
                "text/html;charset=utf-8",
                responseBody
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginPage() throws IOException {
        final var socket = new StubSocket("GET /login HTTP/1.1\r\nCookie: JSESSIONID=existing-session\r\n\r\n");
        final var processor = createProcessor(socket);

        processor.process(socket);

        assertThat(socket.output()).isEqualTo(createResponse(
                "text/html;charset=utf-8", readResource("static/login.html")));
    }

    @Test
    void issuesSessionCookieWhenCookieHeaderIsMissing() {
        final var socket = new StubSocket();

        createProcessor(socket).process(socket);

        assertSessionCookie(socket.output());
    }

    @Test
    void issuesSessionCookieWhenOnlyOtherCookiesExistAndPreservesRedirect() {
        final var socket = new StubSocket(String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry",
                "", ""));

        createProcessor(socket).process(socket);

        assertSessionCookie(socket.output());
        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n")
                .contains("\r\nLocation: /index.html\r\n");
    }

    @Test
    void parsesCookiesAndKeepsExistingSession() {
        final var socket = new StubSocket(String.join("\r\n",
                "POST /login HTTP/1.1",
                "Cookie: yummy_cookie=choco; JSESSIONID=existing-session; token=abc==",
                "content-length: 12",
                "Content-Type: application/x-www-form-urlencoded",
                "", "account=gugu"));
        final var processor = getHttp11Processor(socket);

        processor.process(socket);

        assertThat(socket.output()).isEqualTo(createRedirectResponse("/index.html"));
    }

    @Test
    void loginSessionIsCreatedBeforeControllerAndReusedOnNextRequest() {
        final var manager = new SessionManager();
        final var login = new StubSocket(String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "", "account=gugu&password=password"));

        createProcessor(login, manager).process(login);

        String sessionId = assertSessionCookie(login.output());
        Session session = manager.findSession(sessionId);
        assertThat(session).isNotNull();
        assertThat(((User) session.getAttribute("user")).getAccount()).isEqualTo("gugu");
        assertThat(login.output()).contains("Location: /index.html\r\n");

        final var next = new StubSocket("GET /login HTTP/1.1\r\nCookie: JSESSIONID="
                + sessionId + "\r\n\r\n");
        createProcessor(next, manager).process(next);

        assertThat(next.output()).isEqualTo(createRedirectResponse("/index.html"));
        assertThat(manager.findSession(sessionId)).isSameAs(session);
    }

    @Test
    void unknownSessionIdIsReplaced() {
        final var manager = new SessionManager();
        final var socket = new StubSocket("GET /login HTTP/1.1\r\nCookie: JSESSIONID=unknown\r\n\r\n");

        createProcessor(socket, manager).process(socket);

        String sessionId = assertSessionCookie(socket.output());
        assertThat(sessionId).isNotEqualTo("unknown");
        assertThat(manager.findSession(sessionId).getAttribute("user")).isNull();
        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
    }

    @Test
    void invalidatedSessionIsReplaced() {
        final var manager = new SessionManager();
        final var previous = new Session("invalid-session");
        manager.add(previous);
        previous.invalidate();
        final var socket = new StubSocket("GET /login HTTP/1.1\r\nCookie: JSESSIONID=invalid-session\r\n\r\n");

        createProcessor(socket, manager).process(socket);

        String sessionId = assertSessionCookie(socket.output());
        assertThat(manager.findSession("invalid-session")).isNull();
        assertThat(manager.findSession(sessionId).isValid()).isTrue();
        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
    }

    private String assertSessionCookie(String response) {
        var cookies = response.split("\r\n\r\n", 2)[0].lines()
                .filter(line -> line.startsWith("Set-Cookie: "))
                .toList();
        assertThat(cookies).hasSize(1);
        assertThat(cookies.getFirst()).startsWith("Set-Cookie: JSESSIONID=");
        String sessionId = cookies.getFirst().substring("Set-Cookie: JSESSIONID=".length());
        assertThat(UUID.fromString(sessionId).toString()).isEqualTo(sessionId);
        return sessionId;
    }

    @Test
    void formBodyWithCharsetIsParsedAlongsideQuery() {
        final var socket = new StubSocket(String.join("\r\n",
                "POST /login?account=query HTTP/1.1",
                "Content-Type: Application/X-WWW-Form-Urlencoded; charset=UTF-8",
                "Content-Length: 12", "", "account=gugu"));
        final var processor = new Http11Processor(socket, (request, session) -> {
            assertThat(request.version()).isEqualTo("HTTP/1.1");
            assertThat(request.headers()).containsEntry("content-length", "12");
            assertThat(request.body()).isEqualTo("account=gugu");
            assertThat(request.queryParameters()).hasSize(1).containsEntry("account", "query");
            assertThat(request.formParameters()).hasSize(1).containsEntry("account", "gugu");
            return HttpResponse.redirect("/index.html");
        }, new SessionManager());

        processor.process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
    }

    @Test
    void nonFormBodyIsNotParsedButQueryIsPreserved() {
        for (String contentType : new String[]{"", "application/json", "text/plain",
                "application/x-www-form-urlencoded-other"}) {
            String header = contentType.isEmpty() ? "" : "Content-Type: " + contentType + "\r\n";
            final var socket = new StubSocket("POST /login?source=query HTTP/1.1\r\n"
                    + header + "Content-Length: 12\r\n\r\naccount=gugu");
            final var processor = new Http11Processor(socket, (request, session) -> {
                assertThat(request.queryParameters()).as("Content-Type: %s", contentType)
                        .hasSize(1).containsEntry("source", "query");
                assertThat(request.formParameters()).isEmpty();
                assertThat(request.body()).isEqualTo("account=gugu");
                return HttpResponse.redirect("/index.html");
            }, new SessionManager());

            processor.process(socket);

            assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
        }
    }

    @Test
    void registrationAuthenticatesNewAndExistingSessions() {
        for (boolean existingSession : new boolean[]{false, true}) {
            final var manager = new SessionManager();
            if (existingSession) {
                manager.add(new Session("registration-session"));
            }
            String account = "registration-" + UUID.randomUUID();
            String body = "account=" + account + "&password=password&email=test%40example.com";
            String cookie = existingSession ? "Cookie: JSESSIONID=registration-session\r\n" : "";
            final var socket = new StubSocket("POST /register HTTP/1.1\r\n"
                    + cookie + "Content-Type: application/x-www-form-urlencoded\r\n"
                    + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length
                    + "\r\n\r\n" + body);

            createProcessor(socket, manager).process(socket);

            String sessionId = existingSession ? "registration-session" : assertSessionCookie(socket.output());
            var user = (User) manager.findSession(sessionId).getAttribute("user");
            assertThat(user).isNotNull();
            assertThat(user.getAccount()).isEqualTo(account);
            assertThat(socket.output()).contains("Location: /index.html\r\n");
            if (existingSession) {
                assertThat(socket.output()).doesNotContain("Set-Cookie:");
            }

            final var next = new StubSocket("GET /login HTTP/1.1\r\nCookie: JSESSIONID="
                    + sessionId + "\r\n\r\n");
            createProcessor(next, manager).process(next);
            assertThat(next.output()).isEqualTo(createRedirectResponse("/index.html"));
        }
    }

    @Test
    void duplicateRegistrationReturnsFormAndPreservesExistingAccount() throws IOException {
        final var service = new ApplicationService();
        String account = "registration-" + UUID.randomUUID();
        String originalBody = "account=" + account + "&password=original&email=original%40example.com";
        final var first = new StubSocket("POST /register HTTP/1.1\r\n"
                + "Cookie: JSESSIONID=existing-session\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: " + originalBody.getBytes(StandardCharsets.UTF_8).length
                + "\r\n\r\n" + originalBody);

        createProcessor(first).process(first);

        assertThat(first.output()).isEqualTo(createRedirectResponse("/index.html"));

        String duplicateBody = "account=" + account + "&password=replacement&email=other%40example.com";
        final var duplicate = new StubSocket("POST /register HTTP/1.1\r\n"
                + "Cookie: JSESSIONID=existing-session\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: " + duplicateBody.getBytes(StandardCharsets.UTF_8).length
                + "\r\n\r\n" + duplicateBody);

        final var duplicateManager = new SessionManager();
        final var anonymousSession = new Session("existing-session");
        duplicateManager.add(anonymousSession);
        createProcessor(duplicate, duplicateManager).process(duplicate);

        assertThat(duplicate.output()).isEqualTo(createResponse(
                "text/html;charset=utf-8", readResource("static/register.html")));
        assertThat(service.login(account, "original")).isPresent();
        assertThat(service.login(account, "replacement")).isEmpty();
        assertThat(anonymousSession.getAttribute("user")).isNull();
    }

    @Test
    void malformedFormEncodingReturnsBadRequest() {
        for (String body : new String[]{"account=%ZZ", "%ZZ=gugu", "account=%", "account=%4"}) {
            final var socket = new StubSocket("POST /login HTTP/1.1\r\n"
                    + "Content-Type: application/x-www-form-urlencoded\r\n"
                    + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length
                    + "\r\n\r\n" + body);

            createProcessor(socket).process(socket);

            assertThat(socket.output()).as("body: %s", body).isEqualTo(
                    createResponse("400 Bad Request", "text/html;charset=utf-8", "400 Bad Request"));
        }
    }

    @Test
    void validEncodedFormParametersAreDecoded() {
        String body = "acc%6Funt=gu%67u&note=hello+world%40%ED%95%9C";
        final var socket = new StubSocket("POST /login HTTP/1.1\r\n"
                + "Content-Type: application/x-www-form-urlencoded\r\n"
                + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length
                + "\r\n\r\n" + body);
        final var processor = new Http11Processor(socket, (request, session) -> {
            assertThat(request.formParameters()).containsEntry("account", "gugu")
                    .containsEntry("note", "hello world@한");
            return HttpResponse.redirect("/index.html");
        }, new SessionManager());

        processor.process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
    }

    @Test
    void unsupportedMethodsReturn405WithAllowedMethods() {
        for (String path : new String[]{"/", "/login", "/register", "/index.html"}) {
            for (String method : new String[]{"PUT", "DELETE", "PATCH", "OPTIONS"}) {
                var socket = new StubSocket(method + " " + path + " HTTP/1.1\r\n"
                        + "Cookie: JSESSIONID=existing-session\r\n\r\n");

                createProcessor(socket).process(socket);

                assertThat(socket.output()).as("%s %s", method, path)
                        .startsWith("HTTP/1.1 405 Method Not Allowed\r\n")
                        .contains("\r\nAllow: GET, POST\r\n")
                        .contains("\r\nContent-Length: 0\r\n")
                        .endsWith("\r\n\r\n");
            }
        }
    }

    private Http11Processor createProcessor(StubSocket socket) {
        final var manager = new SessionManager();
        manager.add(new Session("existing-session"));
        return createProcessor(socket, manager);
    }

    private Http11Processor createProcessor(StubSocket socket, SessionManager manager) {
        final var service = new ApplicationService();
        final var resourceHandler = new StaticResourceHandler();

        final var adapter = new ApplicationAdapter(new RequestMapping(service, resourceHandler));

        return new Http11Processor(socket, adapter, manager);
    }

    private String readResource(String path) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(path)) {
            return new String(
                    Objects.requireNonNull(resource).readAllBytes(),
                    StandardCharsets.UTF_8
            );
        }
    }

    private String createRedirectResponse(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 0",
                "Location: " + location,
                "",
                ""
        );
    }

    private String createResponse(String contentType, String responseBody) {
        return createResponse("200 OK", contentType, responseBody);
    }

    private String createResponse(String status, String contentType, String responseBody) {
        int contentLength = responseBody.getBytes(StandardCharsets.UTF_8).length;

        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + contentType,
                "Content-Length: " + contentLength,
                "",
                responseBody
        );
    }

    private static Http11Processor getHttp11Processor(StubSocket socket) {
        final var manager = new SessionManager();
        final var existing = new Session("existing-session");
        manager.add(existing);
        return new Http11Processor(socket, (request, session) -> {
            assertThat(session).isSameAs(existing);
            assertThat(request.cookies().getCookie("yummy_cookie")).isEqualTo("choco");
            assertThat(request.cookies().getCookie("JSESSIONID")).isEqualTo("existing-session");
            assertThat(request.cookies().getCookie("token")).isEqualTo("abc==");
            assertThat(request.formParameters()).containsEntry("account", "gugu");
            return HttpResponse.redirect("/index.html");
        }, manager);
    }
}
