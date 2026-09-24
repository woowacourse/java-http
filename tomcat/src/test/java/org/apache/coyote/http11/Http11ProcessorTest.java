package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    @DisplayName("/ 요청을 index.html 정적 리소스로 응답한다")
    void processTest() throws IOException {
        // when
        String response = process("/");

        // then
        assertThat(response).isEqualTo(expectedResponse("/index.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("index.html 정적 리소스를 응답한다")
    void index() throws IOException {
        // when
        String response = process("/index.html");

        // then
        assertThat(response).isEqualTo(expectedResponse("/index.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("확장자가 없는 HTML 경로는 .html을 붙여 응답한다")
    void extensionlessHtmlPath() throws IOException {
        // when
        String response = process("/login");

        // then
        assertThat(response).isEqualTo(expectedResponse("/login.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("html은 text_html로 응답한다")
    void htmlTest() {
        // when
        String response = process("/index.html");

        // then
        assertThat(response)
                .contains("Content-Type: text/html;charset=utf-8");
    }

    @Test
    @DisplayName("css text_css로 응답한다")
    void css() throws IOException {
        // when
        String response = process("/css/styles.css");

        // then
        assertThat(response).isEqualTo(expectedResponse("/css/styles.css", "text/css;charset=utf-8"));
    }

    @Test
    @DisplayName("쿼리 스트링이 포함된 요청에서 경로에 해당하는 HTML을 응답한다")
    void queryString() throws IOException {
        // when
        String response = process("/login.html?account=gugu&password=password");

        // then
        assertThat(response).isEqualTo(expectedResponse("/login.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("잘못된 query string은 조회하지 않고 요청한 HTML을 응답한다")
    void malformedQueryString() throws IOException {
        // when
        String response = process("/login.html?account");

        // then
        assertThat(response).isEqualTo(expectedResponse("/login.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("정적 리소스가 없으면 404.html을 응답한다")
    void notFound() throws IOException {
        // when
        String response = process("/unknown.html");

        // then
        assertThat(response).isEqualTo(expectedResponse("404 Not Found", "/404.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("로그인에 성공하면 index.html로 리다이렉트한다")
    void postLoginSuccess() {
        // when
        String response = process("POST", "/login", "account=gugu&password=password");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html");
    }

    @Test
    @DisplayName("로그인에 실패하면 401.html로 리다이렉트한다")
    void postLoginFailure() {
        // when
        String response = process("POST", "/login", "account=gugu&password=wrong");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /401.html");
    }

    @Test
    @DisplayName("로그인 폼은 POST로 전송한다")
    void loginFormMethod() {
        // when
        String response = process("/login");

        // then
        assertThat(response)
                .contains("<form method=\"post\" action=\"login\">");
    }

    @Test
    @DisplayName("회원가입을 완료하면 사용자를 저장하고 index.html로 리다이렉트한다")
    void postRegister() {
        // when
        String response = process("POST", "/register", "account=roro&password=java&email=roro%40example.com");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html");
        assertThat(InMemoryUserRepository.findByAccount("roro"))
                .isPresent();
    }

    @Test
    @DisplayName("JSESSIONID 쿠키가 없으면 응답에 Set-Cookie를 추가한다")
    void setCookieWhenJSessionIdDoesNotExist() {
        // given
        final var socket = new StubSocket(httpRequestWithoutCookie("/index.html"));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("Set-Cookie: JSESSIONID=");
        assertThat(SessionManager.findSession(extractJSessionId(socket.output())))
                .isNotNull();
    }

    @Test
    @DisplayName("JSESSIONID 쿠키는 전체 경로와 보안 속성을 포함한다")
    void jSessionIdCookieAttributes() {
        // given
        final var socket = new StubSocket(httpRequestWithoutCookie("/index.html"));
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("Set-Cookie: JSESSIONID=")
                .contains("Path=/")
                .contains("HttpOnly")
                .contains("SameSite=Lax");
    }

    @Test
    @DisplayName("로그인 전 발급한 JSESSIONID는 다음 요청에서 재사용한다")
    void preLoginSessionIsReused() {
        // given
        String response = processWithoutCookie("/index.html");
        String sessionId = extractJSessionId(response);

        // when
        String nextResponse = processWithCookie("/css/styles.css", "JSESSIONID=" + sessionId);

        // then
        assertThat(SessionManager.findSession(sessionId))
                .isNotNull();
        assertThat(nextResponse)
                .doesNotContain("Set-Cookie: JSESSIONID=");
    }

    @Test
    @DisplayName("서버에 없는 JSESSIONID 쿠키가 오면 새 세션을 발급한다")
    void unknownJSessionIdIsReplaced() {
        // when
        String response = processWithCookie("/index.html", "JSESSIONID=unknown-session-id");

        // then
        String sessionId = extractJSessionId(response);
        assertThat(sessionId)
                .isNotEqualTo("unknown-session-id");
        assertThat(SessionManager.findSession(sessionId))
                .isNotNull();
        assertThat(SessionManager.findSession("unknown-session-id"))
                .isNull();
    }

    @Test
    @DisplayName("로그인에 성공하면 세션에 사용자를 저장한다")
    void loginSuccessCreatesSession() {
        // when
        String response = processWithoutCookie("POST", "/login", "account=gugu&password=password");

        // then
        String sessionId = extractJSessionId(response);
        assertThat(SessionManager.findSession(sessionId).getAttribute("user"))
                .isInstanceOf(User.class);
    }

    @Test
    @DisplayName("로그인에 성공하면 기존 세션을 새 세션으로 교체한다")
    void loginSuccessRenewsSessionId() {
        // given
        SessionManager.add(new Session("fixed-session-id"));

        // when
        String response = processWithCookie("POST", "/login", "JSESSIONID=fixed-session-id", "account=gugu&password=password");

        // then
        String sessionId = extractJSessionId(response);
        assertThat(sessionId).isNotEqualTo("fixed-session-id");
        assertThat(SessionManager.findSession(sessionId).getAttribute("user"))
                .isInstanceOf(User.class);
        assertThat(SessionManager.findSession("fixed-session-id"))
                .isNull();
    }

    @Test
    @DisplayName("회원가입 필수 값이 비어 있으면 저장하지 않고 401 페이지로 리다이렉트한다")
    void postRegisterWithBlankValue() {
        // when
        String response = process("POST", "/register", "account=&password=&email=");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /401.html");
        assertThat(InMemoryUserRepository.findByAccount(""))
                .isEmpty();
    }

    @Test
    @DisplayName("로그인된 상태로 로그인 페이지에 접근하면 index.html로 리다이렉트한다")
    void getLoginWithLoggedInSession() {
        // given
        Session session = new Session("logged-in-session");
        session.setAttribute("user", InMemoryUserRepository.findByAccount("gugu").orElseThrow());
        SessionManager.add(session);

        // when
        String response = processWithCookie("/login", "JSESSIONID=logged-in-session");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html");
    }

    @Test
    @DisplayName("HTTP 헤더 이름은 대소문자를 구분하지 않는다")
    void headerNamesAreCaseInsensitive() {
        // when
        String response = processWithLowerCaseHeaders("POST", "/login", "account=gugu&password=password");

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html");
    }

    private String process(String path) {
        final var socket = new StubSocket(httpRequest(path));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String process(String method, String path, String body) {
        final var socket = new StubSocket(httpRequest(method, path, body));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String processWithoutCookie(String method, String path, String body) {
        final var socket = new StubSocket(httpRequestWithoutCookie(method, path, body));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String processWithoutCookie(String path) {
        final var socket = new StubSocket(httpRequestWithoutCookie(path));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String processWithCookie(String path, String cookie) {
        final var socket = new StubSocket(httpRequestWithCookie(path, cookie));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String processWithCookie(String method, String path, String cookie, String body) {
        final var socket = new StubSocket(httpRequestWithCookie(method, path, cookie, body));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String processWithLowerCaseHeaders(String method, String path, String body) {
        final var socket = new StubSocket(httpRequestWithLowerCaseHeaders(method, path, body));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String httpRequest(String path) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=" + createSessionId(),
                "",
                "");
    }

    private String httpRequest(String method, String path, String body) {
        return String.join("\r\n",
                method + " " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Cookie: JSESSIONID=" + createSessionId(),
                "",
                body);
    }

    private String httpRequestWithoutCookie(String method, String path, String body) {
        return String.join("\r\n",
                method + " " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
    }

    private String httpRequestWithCookie(String path, String cookie) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: " + cookie,
                "",
                "");
    }

    private String httpRequestWithCookie(String method, String path, String cookie, String body) {
        return String.join("\r\n",
                method + " " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Cookie: " + cookie,
                "",
                body);
    }

    private String httpRequestWithLowerCaseHeaders(String method, String path, String body) {
        return String.join("\r\n",
                method + " " + path + " HTTP/1.1 ",
                "host: localhost:8080 ",
                "content-type: application/x-www-form-urlencoded",
                "content-length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "cookie: JSESSIONID=" + createSessionId(),
                "",
                body);
    }

    private String httpRequestWithoutCookie(String path) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
    }

    private String extractJSessionId(String response) {
        return response.lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .map(value -> value.split(";", 2)[0])
                .findFirst()
                .orElseThrow();
    }

    private String createSessionId() {
        String sessionId = UUID.randomUUID().toString();
        SessionManager.add(new Session(sessionId));

        return sessionId;
    }

    private String expectedResponse(String path, String contentType) throws IOException {
        return expectedResponse("200 OK", path, contentType);
    }

    private String expectedResponse(String status, String path, String contentType) throws IOException {
        byte[] body = readResource(path);

        return "HTTP/1.1 " + status + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "\r\n"
                + new String(body, StandardCharsets.UTF_8);
    }

    private byte[] readResource(String path) throws IOException {
        URL resource = getClass()
                .getClassLoader()
                .getResource("static" + path);

        return Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
    }

}
