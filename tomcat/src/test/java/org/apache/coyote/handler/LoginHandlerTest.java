package org.apache.coyote.handler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.http.HttpHeader;
import org.apache.http.request.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    private LoginHandler loginHandler;
    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        loginHandler = LoginHandler.getInstance();
        sessionManager = SessionManager.getInstance();
    }

    @Test
    @DisplayName("GET 요청 처리: 세션이 없는 경우 로그인 페이지를 반환")
    void handle_GetRequest_Without_Session() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/login.html");
        final String fileContent = Files.readString(Path.of(resourceURL.getPath()));

        final HttpRequest request = new HttpRequest("GET", "/login", "HTTP/1.1", null, null);

        assertTrue(loginHandler.handle(request).contains(fileContent));
    }

    @Test
    @DisplayName("GET 요청 처리: 세션이 있는 경우 index 페이지로 리다이렉트")
    void handle_GetRequest_With_ValidSession() {
        final String sessionId = UUID.randomUUID().toString();
        sessionManager.add(new Session(sessionId));
        final HttpRequest request = new HttpRequest("GET", "/login", "HTTP/1.1",
                new HttpHeader[]{new HttpHeader("Cookie", "JSESSIONID=" + sessionId)}, null);

        assertTrue(loginHandler.handle(request).contains("302 FOUND"));

        sessionManager.remove(new Session(sessionId));
    }

    @Test
    @DisplayName("POST 요청 처리: 유효한 계정 정보로 로그인 성공")
    void handle_PostRequest_With_ValidCredentials() {
        final HttpRequest request = new HttpRequest("POST", "/login", "HTTP/1.1", null,
                "account=gugu&password=password");

        final String result = loginHandler.handle(request);

        assertAll(
                () -> assertTrue(result.contains("302 FOUND")),
                () -> assertTrue(result.contains("http://localhost:8080/index.html")),
                () -> assertTrue(result.contains("Set-Cookie: JSESSIONID="))
        );
        assertTrue(result.contains("http://localhost:8080/index.html"));
        assertTrue(result.contains("Set-Cookie: JSESSIONID="));
    }

    @Test
    @DisplayName("POST 요청 처리: 비밀번호가 올바르지 않는 경우 로그인 실패")
    void handle_PostRequest_With_InvalidCredentials() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/401.html");
        final String fileContent = Files.readString(Path.of(resourceURL.getPath()));

        final HttpRequest request = new HttpRequest("POST", "/login", "HTTP/1.1", null,
                "account=gugu&password=wrongpassword");

        assertThat(loginHandler.handle(request)).contains(fileContent);
    }

    @Test
    @DisplayName("POST 요청 처리: 존재하지 않는 계정 정보로 로그인 실패")
    void handle_PostRequest_WithNonexistentUser() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/401.html");
        final String fileContent = Files.readString(Path.of(resourceURL.getPath()));

        final HttpRequest request = new HttpRequest("POST", "/login", "HTTP/1.1", null,
                "account=nonexistent&password=anypassword");

        assertThat(loginHandler.handle(request)).contains(fileContent);
    }

    @Test
    @DisplayName("지원하지 않는 메소드 처리: 404 페이지 반환")
    void handle_UnsupportedMethod() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/404.html");
        final String fileContent = Files.readString(Path.of(resourceURL.getPath()));

        final HttpRequest request = new HttpRequest("PUT", "/login", "HTTP/1.1", null, null);

        assertThat(loginHandler.handle(request)).contains(fileContent);
    }
}
