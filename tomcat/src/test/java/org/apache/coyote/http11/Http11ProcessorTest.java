package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@DisplayName("HTTP/1.1 요청 처리")
class Http11ProcessorTest {

    @Test
    @DisplayName("존재하지 않는 경로를 요청하면 404 Not Found를 반환한다")
    void respondsWithNotFoundWhenPathDoesNotExist() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /unknown HTTP/1.1",
                "Host: localhost:8080",
                "Cookie: JSESSIONID=existing-id",
                "",
                ""
        );
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 404 Not Found")
                .contains("해당하는 경로가 없습니다.");
    }

    @Test
    @DisplayName("요청 형식이 잘못되면 400 Bad Request를 반환한다")
    void respondsWithBadRequestWhenRequestIsMalformed() {
        // given
        final var socket = new StubSocket("INVALID\r\n\r\n");
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains("HTTP/1.1 400 Bad Request")
                .doesNotContain("Set-Cookie");
    }

    @Nested
    @DisplayName("쿠키")
    class CookieTest {

        @Test
        @DisplayName("요청에 JSESSIONID가 없으면 새 세션을 등록하고 응답 쿠키에 ID를 추가한다")
        void createsSessionAndAddsJSessionIdWhenRequestDoesNotContainOne() throws IOException {
            // given
            final Manager manager = new SessionManager();
            manager.removeAll();
            final String httpRequest = String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, manager);

            // when
            processor.process(socket);

            // then
            final Matcher matcher = Pattern.compile("Set-Cookie: JSESSIONID=([0-9a-f\\-]{36})")
                    .matcher(socket.output());
            assertThat(matcher.find()).isTrue();
            assertThat(manager.findSession(matcher.group(1))).isNotNull();
        }

        @Test
        @DisplayName("요청에 JSESSIONID가 있으면 새로운 JSESSIONID를 추가하지 않는다")
        void doesNotAddJSessionIdWhenRequestContainsOne() {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /index.html HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Cookie: yummy_cookie=choco; JSESSIONID=existing-id ",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output()).doesNotContain("Set-Cookie: JSESSIONID=");
        }
    }

    @Test
    @DisplayName("루트 경로 요청에 기본 응답을 반환한다")
    void respondsToRootRequest() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=existing-id ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("정적 index.html 파일을 반환한다")
    void respondsWithIndexPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-id ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        final String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Nested
    @DisplayName("회원가입")
    class RegisterTest {

        @Test
        @DisplayName("GET /register 요청에 POST 방식의 회원가입 페이지를 반환한다")
        void respondsWithRegisterPage() {
            // given
            final String httpRequest = String.join("\r\n",
                    "GET /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output())
                    .contains("HTTP/1.1 200 OK")
                    .contains("<title>회원가입</title>")
                    .contains("<form method=\"post\" action=\"register\">");
        }

        @Test
        @DisplayName("POST /register 요청으로 회원을 등록하고 index.html로 리다이렉트한다")
        void registersUserAndRedirectsToIndex() {
            // given
            final String account = "testuser";
            final String body = "account=" + account + "&password=password123&email=a@a.com";
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    body);
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output())
                    .contains("HTTP/1.1 302 Found")
                    .contains("Location: /index.html");
            assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
        }
    }

    @Nested
    @DisplayName("로그인")
    class LoginTest {

        private final Manager manager = new SessionManager();

        @BeforeEach
        void setUp() {
            manager.removeAll();
        }

        @Test
        @DisplayName("GET /login 요청에 POST 방식의 로그인 페이지를 반환한다")
        void respondsWithLoginPage() {
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
            assertThat(socket.output())
                    .contains("HTTP/1.1 200 OK")
                    .contains("<title>로그인</title>")
                    .contains("<form method=\"post\" action=\"login\">");
        }

        @Test
        @DisplayName("로그인한 사용자가 GET /login을 요청하면 index.html로 리다이렉트한다")
        void redirectsToIndexWhenLoggedInUserRequestsLoginPage() throws IOException {
            // given
            final User loginUser = InMemoryUserRepository.findByAccount("gugu").orElseThrow();
            final HttpSession session = new Session("logged-in-session-id");
            session.setAttribute("loginUser", loginUser);
            manager.add(session);

            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Cookie: JSESSIONID=logged-in-session-id ",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, manager);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output())
                    .contains("HTTP/1.1 302 Found")
                    .contains("Location: /index.html");
        }

        @Test
        @DisplayName("JSESSIONID가 있어도 로그인 사용자가 없으면 로그인 페이지를 반환한다")
        void respondsWithLoginPageWhenSessionHasNoLoginUser() throws IOException {
            // given
            final HttpSession session = new Session("anonymous-session-id");
            manager.add(session);

            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Cookie: JSESSIONID=anonymous-session-id ",
                    "",
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, manager);

            // when
            processor.process(socket);

            // then
            assertThat(socket.output())
                    .contains("HTTP/1.1 200 OK")
                    .contains("<title>로그인</title>");
        }

        @Test
        @DisplayName("POST /login 요청의 비밀번호가 일치하지 않으면 401 페이지로 리다이렉트한다")
        void redirectsToUnauthorizedPageWhenPasswordDoesNotMatch() {
            // given
            final String body = "account=gugu&password=wrong-password";
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    body,
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
        @DisplayName("POST /login 요청의 인증에 성공하면 index.html로 리다이렉트한다")
        void redirectsToIndexWhenLoginSucceeds() {
            // given
            final String body = "account=gugu&password=password";
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    body,
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
        @DisplayName("로그인에 성공하면 현재 세션에 로그인 사용자를 저장한다")
        void storesLoginUserInCurrentSessionWhenLoginSucceeds() throws IOException {
            // given
            final User loginUser = InMemoryUserRepository.findByAccount("gugu").orElseThrow();
            final HttpSession session = new Session("login-session-id");
            manager.add(session);

            final String body = "account=gugu&password=password";
            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Cookie: JSESSIONID=login-session-id ",
                    "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    body,
                    "");
            final var socket = new StubSocket(httpRequest);
            final var processor = new Http11Processor(socket, manager);

            // when
            processor.process(socket);

            // then
            final HttpSession currentSession = manager.findSession("login-session-id");
            assertThat(currentSession.getAttribute("loginUser")).isSameAs(loginUser);
        }
    }
}
