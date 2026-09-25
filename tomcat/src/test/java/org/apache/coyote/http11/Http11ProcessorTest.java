package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    private static final Pattern SESSION_COOKIE_PATTERN =
            Pattern.compile("Set-Cookie: JSESSIONID=([^\\r\\n]+)\\r\\n");

    @Test
    void process() {
        // given
        final StubSocket socket = new StubSocket();
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(removeAndValidateSessionCookie(socket.output())).isEqualTo(expected);
    }

    @Test
    void 잘못된_request_line이면_400을_응답한다() {
        final String malformedRequest = "GET /index.html\r\n\r\n";

        final String response = execute(malformedRequest);

        final String expected = String.join("\r\n",
                "HTTP/1.1 400 Bad Request ",
                "Content-Type: text/plain;charset=utf-8 ",
                "Content-Length: 11 ",
                "",
                "Bad Request");

        assertThat(response).isEqualTo(expected);
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

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final byte[] bodyBytes;
        try (InputStream resourceInputStream = getClass().getClassLoader()
                .getResourceAsStream("static/index.html")) {
            assertThat(resourceInputStream).isNotNull();
            bodyBytes = resourceInputStream.readAllBytes();
        }

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + bodyBytes.length + " ",
                "",
                new String(bodyBytes, StandardCharsets.UTF_8));

        assertThat(removeAndValidateSessionCookie(socket.output())).isEqualTo(expected);
    }

    @Test
    void 로그인에_실패하면_401_페이지로_리다이렉트한다() {
        final String requestBody = "account=gugu&password=wrong";
        final String httpRequest = formRequest("/login", requestBody, null);

        final String response = execute(httpRequest);
        final String sessionId = extractSessionId(response);

        try {
            final String expected = redirectResponse("/401.html");
            assertThat(removeAndValidateSessionCookie(response)).isEqualTo(expected);
        } finally {
            SessionManager.getInstance().remove(sessionId);
        }
    }

    @Test
    void 로그인에_성공하면_세션에_로그인_상태를_유지한다() {
        final String firstResponse = execute(String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "",
                ""));
        final String sessionId = extractSessionId(firstResponse);

        try {
            final String requestBody = "account=gugu&password=password";
            final String loginResponse = execute(formRequest("/login", requestBody, sessionId));

            assertThat(loginResponse).isEqualTo(redirectResponse("/index.html"));
            assertThat(loginResponse).doesNotContain("Set-Cookie");

            final String loggedInRequest = String.join("\r\n",
                    "GET /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Cookie: JSESSIONID=" + sessionId,
                    "",
                    "");

            assertThat(execute(loggedInRequest)).isEqualTo(redirectResponse("/index.html"));
        } finally {
            SessionManager.getInstance().remove(sessionId);
        }
    }

    @Test
    void login_html_조회는_로그인_처리를_실행하지_않는다() {
        final String httpRequest = String.join("\r\n",
                "GET /login.html?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        final String response = execute(httpRequest);
        final String sessionId = extractSessionId(response);

        try {
            final Session session = SessionManager.getInstance().findSession(sessionId);
            assertThat(removeAndValidateSessionCookie(response)).startsWith("HTTP/1.1 200 OK ");
            assertThat(session).isNotNull();
            assertThat(session.getAttribute("user")).isNull();
        } finally {
            SessionManager.getInstance().remove(sessionId);
        }
    }

    @Test
    void 회원가입에_성공하면_사용자를_저장하고_인덱스로_리다이렉트한다() {
        final String account = "register-test-user";
        final String password = "password";
        final String requestBody = "account=" + account
                + "&password=" + password
                + "&email=register%40example.com";

        final String response = execute(formRequest("/register", requestBody, null));
        final String sessionId = extractSessionId(response);

        try {
            assertThat(removeAndValidateSessionCookie(response))
                    .isEqualTo(redirectResponse("/index.html"));
            assertThat(InMemoryUserRepository.findByAccount(account))
                    .hasValueSatisfying(user -> assertThat(user.checkPassword(password)).isTrue());
        } finally {
            SessionManager.getInstance().remove(sessionId);
        }
    }

    private String execute(final String httpRequest) {
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);
        return socket.output();
    }

    private String formRequest(final String path, final String requestBody,
                               final String sessionId) {
        final StringBuilder request = new StringBuilder()
                .append("POST ").append(path).append(" HTTP/1.1\r\n")
                .append("Host: localhost:8080\r\n")
                .append("Content-Type: application/x-www-form-urlencoded\r\n")
                .append("Content-Length: ")
                .append(requestBody.getBytes(StandardCharsets.UTF_8).length)
                .append("\r\n");

        if (sessionId != null) {
            request.append("Cookie: JSESSIONID=").append(sessionId).append("\r\n");
        }

        return request.append("\r\n").append(requestBody).toString();
    }

    private String redirectResponse(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");
    }

    private String extractSessionId(final String response) {
        final Matcher matcher = SESSION_COOKIE_PATTERN.matcher(response);
        assertThat(matcher.find()).isTrue();

        final String sessionId = matcher.group(1);
        UUID.fromString(sessionId);
        return sessionId;
    }

    private String removeAndValidateSessionCookie(final String response) {
        extractSessionId(response);
        return SESSION_COOKIE_PATTERN.matcher(response).replaceFirst("");
    }
}
