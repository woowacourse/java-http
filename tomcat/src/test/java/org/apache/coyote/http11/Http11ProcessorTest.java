package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HTTP/1.1 요청 처리")
class Http11ProcessorTest {

    @Test
    @DisplayName("요청 경로가 /이면 기본 응답을 내려준다")
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket, new SessionManager());

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
    @DisplayName("정적 리소스를 응답한다")
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("확장자에 맞는 Content-Type으로 응답한다")
    void css() throws IOException {
        // given
        final var socket = new StubSocket(getRequest("/css/styles.css"));
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticResponse("css/styles.css", "text/css;charset=utf-8"));
    }

    @Test
    @DisplayName("존재하지 않는 리소스는 404로 응답한다")
    void notFound() {
        // given
        final var socket = new StubSocket(getRequest("/nothing.html"));
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(errorResponse(HttpStatus.NOT_FOUND));
    }

    @Test
    @DisplayName("로그인 페이지에 GET으로 접근하면 로그인 폼을 보여준다")
    void showLoginPageOnGet() throws IOException {
        // given
        final var socket = new StubSocket(getRequest("/login"));
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticResponse("login.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("로그인 파라미터가 누락되면 400으로 응답한다")
    void loginWithMissingParameterRespondsBadRequest() {
        // given
        final var socket = new StubSocket(postRequest("/login", "account=gugu"));
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(errorResponse(HttpStatus.BAD_REQUEST));
    }

    @Test
    @DisplayName("로그인에 성공하면 세션 쿠키를 내려주고 index.html로 리다이렉트한다")
    void loginSuccessRedirectsToIndex() {
        // given
        final var socket = new StubSocket(postRequest("/login", "account=gugu&password=password"));
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n" +
                        "Location: /index.html \r\n" +
                        "Content-Length: 0 \r\n")
                .containsPattern("Set-Cookie: JSESSIONID=[0-9a-fA-F-]{36} \r\n")
                .endsWith("\r\n\r\n");
    }

    @Test
    @DisplayName("로그인에 실패하면 401.html로 리다이렉트한다")
    void loginFailureRedirectsToUnauthorized() {
        // given
        final var socket = new StubSocket(postRequest("/login", "account=gugu&password=wrong"));
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(redirectResponse("/401.html"));
    }

    @Test
    @DisplayName("GET 요청이면 회원가입 페이지를 보여준다")
    void registerPage() throws IOException {
        // given
        final var socket = new StubSocket(getRequest("/register"));
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticResponse("register.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("회원가입에 성공하면 사용자를 저장하고 index.html로 리다이렉트한다")
    void registerSavesUserAndRedirectsToIndex() {
        // given
        final String body = "account=tester&password=secret&email=tester%40woowahan.com";
        final var socket = new StubSocket(postRequest("/register", body));
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(redirectResponse("/index.html"));

        final Optional<User> saved = InMemoryUserRepository.findByAccount("tester");
        assertThat(saved).isPresent();
        assertThat(saved.get().checkPassword("secret")).isTrue();
        assertThat(saved.get().toString()).contains("tester@woowahan.com");
    }

    @Test
    @DisplayName("회원가입 파라미터가 누락되면 400으로 응답한다")
    void registerWithMissingParameterRespondsBadRequest() {
        // given
        final String body = "account=noemail&password=secret";
        final var socket = new StubSocket(postRequest("/register", body));
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(errorResponse(HttpStatus.BAD_REQUEST));
        assertThat(InMemoryUserRepository.findByAccount("noemail")).isEmpty();
    }

    @Test
    @DisplayName("헤더 이름이 소문자여도 회원가입을 처리한다")
    void registerWithLowerCaseHeaderNames() {
        // given
        final String body = "account=lower&password=secret&email=lower%40woowahan.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "host: localhost:8080 ",
                "content-type: application/x-www-form-urlencoded ",
                "content-length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket, new SessionManager());

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).isEqualTo(redirectResponse("/index.html"));
        assertThat(InMemoryUserRepository.findByAccount("lower")).isPresent();
    }

    @Test
    @DisplayName("로그인하면 세션에 사용자를 저장한다")
    void storeUserInSession() {
        // given
        final var manager = new SessionManager();
        final var socket = new StubSocket(postRequest("/login", "account=gugu&password=password"));

        // when
        new Http11Processor(socket, manager).process(socket);

        // then
        final HttpSession session = manager.findSession(extractSessionId(socket.output()));
        assertThat(session).isNotNull();
        assertThat(session.getAttribute("user")).isInstanceOf(User.class);
        assertThat(((User) session.getAttribute("user")).getAccount()).isEqualTo("gugu");
    }

    @Test
    @DisplayName("로그인된 상태로 로그인 페이지에 접근하면 index.html로 리다이렉트한다")
    void redirectWhenAlreadyLoggedIn() {
        // given
        final var manager = new SessionManager();
        final var loginSocket = new StubSocket(postRequest("/login", "account=gugu&password=password"));
        new Http11Processor(loginSocket, manager).process(loginSocket);
        final String sessionId = extractSessionId(loginSocket.output());

        final var socket = new StubSocket(getRequestWithCookie("/login", "JSESSIONID=" + sessionId));

        // when
        new Http11Processor(socket, manager).process(socket);

        // then
        assertThat(socket.output()).isEqualTo(redirectResponse("/index.html"));
    }

    @Test
    @DisplayName("알 수 없는 세션 아이디로 접근하면 로그인 페이지를 보여준다")
    void showLoginPageWhenSessionIsUnknown() throws IOException {
        // given
        final var socket = new StubSocket(getRequestWithCookie("/login", "JSESSIONID=unknown"));

        // when
        new Http11Processor(socket, new SessionManager()).process(socket);

        // then
        assertThat(socket.output()).isEqualTo(staticResponse("login.html", "text/html;charset=utf-8"));
    }

    private String extractSessionId(String response) {
        final Matcher matcher = Pattern.compile("Set-Cookie: JSESSIONID=(\\S+) ").matcher(response);
        assertThat(matcher.find()).isTrue();
        return matcher.group(1);
    }

    private String getRequestWithCookie(String path, String cookie) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: " + cookie + " ",
                "",
                "");
    }

    private String getRequest(String path) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }

    private String postRequest(String path, String body) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                "",
                body);
    }

    private String staticResponse(String resourceName, String contentType) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + resourceName);
        final byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());

        return "HTTP/1.1 200 OK \r\n" +
                "Content-Type: " + contentType + " \r\n" +
                "Content-Length: " + body.length + " \r\n" +
                "\r\n" +
                new String(body, StandardCharsets.UTF_8);
    }

    private String redirectResponse(String location) {
        return "HTTP/1.1 302 Found \r\n" +
                "Location: " + location + " \r\n" +
                "Content-Length: 0 \r\n" +
                "\r\n";
    }

    private String errorResponse(HttpStatus status) {
        return "HTTP/1.1 " + status.getCode() + " " + status.getMessage() + " \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 0 \r\n" +
                "\r\n";
    }
}
