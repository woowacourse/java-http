package org.apache.coyote.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.UUID;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.NotFoundException;
import org.apache.coyote.UnauthorizedException;
import org.apache.http.header.HttpHeader;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginHandlerTest {

    @Test
    @DisplayName("GET 요청 처리: 세션이 없는 경우 로그인 페이지로 리다이렉트")
    void handle_GetRequest_Without_Session() {
        final RequestLine requestLine = new RequestLine("GET", "/login", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null, null);

        final HttpResponse httpResponse = HttpResponse.builder()
                .addLocation("/login.html")
                .foundBuild();
        assertThat(LoginHandler.getInstance().handle(request)).isEqualTo(httpResponse);
    }

    @Test
    @DisplayName("GET 요청 처리: 세션이 있는 경우 index 페이지로 리다이렉트")
    void handle_GetRequest_With_ValidSession() {
        final String sessionId = UUID.randomUUID().toString();
        SessionManager.getInstance().add(new Session(sessionId));
        final RequestLine requestLine = new RequestLine("GET", "/login", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine,
                new HttpHeader[]{new HttpHeader("Cookie", "JSESSIONID=" + sessionId)}, null);

        final HttpResponse httpResponse = HttpResponse.builder()
                .addLocation("/index.html")
                .foundBuild();
        assertThat(LoginHandler.getInstance().handle(request)).isEqualTo(httpResponse);

        SessionManager.getInstance().remove(new Session(sessionId));
    }

    @Test
    @DisplayName("POST 요청 처리: 유효한 계정 정보로 로그인 성공 및 /index.html로 리다이렉트")
    void handle_PostRequest_With_ValidCredentials() {
        final RequestLine requestLine = new RequestLine("POST", "/login", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null,
                "account=gugu&password=password");

        final HttpResponse actual = LoginHandler.getInstance().handle(request);
        assertThat(actual.toString()).contains("JSESSIONID=", "Location: /index.html", "HTTP/1.1 302 Found");
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
