package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
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
        assertThat(socket.output()).isEqualTo(staticFileResponse("static/index.html", "text/html"));
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticFileResponse("static/css/styles.css", "text/css"));
    }

    @Test
    void loginPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticFileResponse("static/login.html", "text/html"));
    }

    @Test
    void login() {
        // given
        final String httpRequest = postRequest("/login", "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html\r\n")
                .contains("Set-Cookie: JSESSIONID=")
                .endsWith("\r\n\r\n");
    }

    @Test
    void loginIssuesUuidSessionId() {
        // given
        final String httpRequest = postRequest("/login", "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String sessionId = extractJSessionId(socket.output());

        assertThat(sessionId).isNotBlank();
        assertThatCode(() -> UUID.fromString(sessionId)).doesNotThrowAnyException();
    }

    @Test
    void issuesDifferentSessionIdForEachLogin() {
        // given
        final String httpRequest = postRequest("/login", "account=gugu&password=password");

        // when
        final String first = extractJSessionId(processAndGetOutput(httpRequest));
        final String second = extractJSessionId(processAndGetOutput(httpRequest));

        // then
        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void loginFail() {
        // given
        final String httpRequest = postRequest("/login", "account=gugu&password=wrong");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found\r\n" +
                "Location: /401.html\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";

        assertThat(socket.output())
                .isEqualTo(expected)
                .doesNotContain("Set-Cookie");
    }

    @Test
    void registerPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticFileResponse("static/register.html", "text/html"));
    }

    @Test
    void register() {
        // given
        final String httpRequest = postRequest(
                "/register", "account=lie&password=password&email=lie%40woowahan.com");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found\r\n" +
                "Location: /index.html\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);

        final Optional<User> saved = InMemoryUserRepository.findByAccount("lie");
        assertThat(saved).isPresent();
        assertThat(saved.get().checkPassword("password")).isTrue();
    }

    @Test
    void registerDecodesUrlEncodedValue() {
        // given
        final String httpRequest = postRequest(
                "/register", "account=decoded&password=password&email=hkkang%40woowahan.com");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final Optional<User> saved = InMemoryUserRepository.findByAccount("decoded");
        assertThat(saved).isPresent();
        assertThat(saved.get().toString()).contains("hkkang@woowahan.com");
    }

    @Test
    void loginStoresUserInSession() {
        // given
        final String httpRequest = postRequest("/login", "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String sessionId = extractJSessionId(socket.output());
        final Session session = SessionManager.INSTANCE.findSession(sessionId);

        assertThat(session).isNotNull();
        assertThat((User) session.getAttribute("user"))
                .isNotNull()
                .extracting(User::getAccount)
                .isEqualTo("gugu");
    }

    @Test
    void loginFailDoesNotCreateSession() {
        // given
        final String httpRequest = postRequest("/login", "account=gugu&password=wrong");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).doesNotContain("Set-Cookie");
    }

    @Test
    void loggedInUserIsRedirectedFromLoginPage() {
        // given
        final String sessionId = extractJSessionId(
                processAndGetOutput(postRequest("/login", "account=gugu&password=password")));

        final String httpRequest = getRequestWithCookie("/login", "JSESSIONID=" + sessionId);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        var expected = "HTTP/1.1 302 Found\r\n" +
                "Location: /index.html\r\n" +
                "Content-Length: 0\r\n" +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loggedInUserIsFoundAmongOtherCookies() {
        // given
        final String sessionId = extractJSessionId(
                processAndGetOutput(postRequest("/login", "account=gugu&password=password")));

        final String httpRequest = getRequestWithCookie(
                "/login", "yummy_cookie=choco; JSESSIONID=" + sessionId + "; tasty_cookie=strawberry");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
    }

    @Test
    void unknownSessionIdSeesLoginPage() throws IOException {
        // given
        final String httpRequest = getRequestWithCookie(
                "/login", "JSESSIONID=" + UUID.randomUUID());

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticFileResponse("static/login.html", "text/html"));
    }

    @Test
    void cookieWithoutJSessionIdSeesLoginPage() throws IOException {
        // given
        final String httpRequest = getRequestWithCookie(
                "/login", "yummy_cookie=choco; tasty_cookie=strawberry");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticFileResponse("static/login.html", "text/html"));
    }

    @Test
    void sessionWithoutUserSeesLoginPage() throws IOException {
        // given
        final Session emptySession = new Session(UUID.randomUUID().toString());
        SessionManager.INSTANCE.add(emptySession);

        final String httpRequest = getRequestWithCookie("/login", "JSESSIONID=" + emptySession.getId());

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticFileResponse("static/login.html", "text/html"));
    }

    @Test
    void loggedInUserStillSeesOtherPages() throws IOException {
        // given
        final String sessionId = extractJSessionId(
                processAndGetOutput(postRequest("/login", "account=gugu&password=password")));

        final String httpRequest = getRequestWithCookie("/register", "JSESSIONID=" + sessionId);

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticFileResponse("static/register.html", "text/html"));
    }

    private String getRequestWithCookie(final String path, final String cookie) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: " + cookie + " ",
                "",
                "");
    }

    private String processAndGetOutput(final String httpRequest) {
        final var socket = new StubSocket(httpRequest);
        new Http11Processor(socket).process(socket);
        return socket.output();
    }

    private String extractJSessionId(final String response) {
        final String prefix = "Set-Cookie: JSESSIONID=";
        return Arrays.stream(response.split("\r\n"))
                .filter(line -> line.startsWith(prefix))
                .map(line -> line.substring(prefix.length()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("응답에 JSESSIONID 쿠키가 없다:\n" + response));
    }

    private String postRequest(final String path, final String body) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);
    }

    private String staticFileResponse(final String resourceName, final String contentType) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(resourceName);
        final String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        return "HTTP/1.1 200 OK \r\n" +
                "Content-Type: " + contentType + ";charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " \r\n" +
                "\r\n" +
                body;
    }
}
