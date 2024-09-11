package org.apache.coyote.handler;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.NotFoundException;
import org.apache.coyote.UnauthorizedException;
import org.apache.http.header.HttpHeader;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    @Test
    @DisplayName("GET 요청 처리: 세션이 없는 경우 로그인 페이지를 반환")
    void handle_GetRequest_Without_Session() throws IOException {
        final URL resourceURL = getClass().getClassLoader().getResource("static/login.html");
        final String fileContent = Files.readString(Path.of(resourceURL.getPath()));

        final RequestLine requestLine = new RequestLine("GET", "/login", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null, null);

        assertAll(
                () -> assertTrue(LoginHandler.getInstance().handle(request).contains("302 Found")),
                () -> assertTrue(LoginHandler.getInstance().handle(request).contains("http://localhost:8080/login.html"))
        );
    }

    @Test
    @DisplayName("GET 요청 처리: 세션이 있는 경우 index 페이지로 리다이렉트")
    void handle_GetRequest_With_ValidSession() {
        final String sessionId = UUID.randomUUID().toString();
        SessionManager.getInstance().add(new Session(sessionId));
        final RequestLine requestLine = new RequestLine("GET", "/login", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine,
                new HttpHeader[]{new HttpHeader("Cookie", "JSESSIONID=" + sessionId)}, null);

        assertAll(
                () -> assertTrue(LoginHandler.getInstance().handle(request).contains("302 Found")),
                () -> assertTrue(LoginHandler.getInstance().handle(request).contains("http://localhost:8080/index.html"))
        );

        SessionManager.getInstance().remove(new Session(sessionId));
    }

    @Test
    @DisplayName("POST 요청 처리: 유효한 계정 정보로 로그인 성공")
    void handle_PostRequest_With_ValidCredentials() {
        final RequestLine requestLine = new RequestLine("POST", "/login", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null,
                "account=gugu&password=password");

        final String result = LoginHandler.getInstance().handle(request);

        assertAll(
                () -> assertTrue(result.contains("302 Found")),
                () -> assertTrue(result.contains("http://localhost:8080/index.html")),
                () -> assertTrue(result.contains("Set-Cookie: JSESSIONID="))
        );
    }

    @Test
    @DisplayName("POST 요청 처리: 비밀번호가 올바르지 않는 경우 로그인 실패")
    void handle_PostRequest_With_InvalidCredentials() {
        final RequestLine requestLine = new RequestLine("POST", "/login", "HTTP/1.1");

        final HttpRequest request = new HttpRequest(requestLine, null,
                "account=gugu&password=wrongpassword");

        assertThatThrownBy(
                () -> LoginHandler.getInstance().handle(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인에 실패하였습니다.");

    }

    @Test
    @DisplayName("POST 요청 처리: 존재하지 않는 계정 정보로 로그인 실패")
    void handle_PostRequest_WithNonexistentUser() {
        final RequestLine requestLine = new RequestLine("POST", "/login", "HTTP/1.1");

        final HttpRequest request = new HttpRequest(requestLine, null,
                "account=nonexistent&password=anypassword");

        assertThatThrownBy(
                () -> LoginHandler.getInstance().handle(request))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인에 실패하였습니다.");
    }

    @Test
    @DisplayName("지원하지 않는 메소드 처리: 예외 반환")
    void handle_UnsupportedMethod() {
        final RequestLine requestLine = new RequestLine("PUT", "/login", "HTTP/1.1");

        final HttpRequest request = new HttpRequest(requestLine, null, null);

        assertThatThrownBy(
                () -> LoginHandler.getInstance().handle(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("페이지를 찾을 수 없습니다.");

    }
}
