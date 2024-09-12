package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import java.util.UUID;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.exception.UnauthorizedException;
import org.apache.http.header.HttpHeader;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.header.HttpHeaders;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    @DisplayName("GET 요청 처리: 세션이 없는 경우 로그인 페이지로 리다이렉트")
    void handle_GetRequest_Without_Session() throws Exception {
        // given
        final RequestLine requestLine = new RequestLine("GET", "/login", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, new HttpHeaders(), null);
        final HttpResponse actual = HttpResponse.builder().okBuild();

        // when
        LoginController.getInstance().service(request, actual);

        // then
        final HttpResponse expected = HttpResponse.builder().foundBuild("/login.html");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET 요청 처리: 세션이 있는 경우 index 페이지로 리다이렉트")
    void handle_GetRequest_With_ValidSession() throws Exception {
        // given
        final String sessionId = UUID.randomUUID().toString();
        SessionManager.getInstance().add(new Session(sessionId));
        final RequestLine requestLine = new RequestLine("GET", "/login", "HTTP/1.1");
        final HttpHeaders httpHeaders = new HttpHeaders(new HttpHeader("Cookie", "JSESSIONID=" + sessionId));
        final HttpRequest request = new HttpRequest(requestLine, httpHeaders, null);
        final HttpResponse actual = HttpResponse.builder().okBuild();

        // when
        LoginController.getInstance().service(request, actual);

        // then
        final HttpResponse expected = HttpResponse.builder().foundBuild("/index.html");
        assertThat(actual).isEqualTo(expected);

        // cleanup
        SessionManager.getInstance().remove(new Session(sessionId));
    }

    @Test
    @DisplayName("POST 요청 처리: 유효한 계정 정보로 로그인 성공 및 /index.html로 리다이렉트")
    void handle_PostRequest_With_ValidCredentials() throws Exception {
        // given
        final RequestLine requestLine = new RequestLine("POST", "/login", "HTTP/1.1");
        final HttpHeaders headers = new HttpHeaders(new HttpHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded"));
        final HttpRequest request = new HttpRequest(requestLine, headers,
                "account=gugu&password=password");
        final HttpResponse actual = HttpResponse.builder().okBuild();

        // when
        LoginController.getInstance().service(request, actual);

        // then
        assertThat(actual.toString()).contains("JSESSIONID=", "Location: /index.html", "HTTP/1.1 302 Found");
    }

    @Test
    @DisplayName("POST 요청 처리: 비밀번호가 올바르지 않는 경우 로그인 실패")
    void handle_PostRequest_With_InvalidCredentials() throws Exception {
        final RequestLine requestLine = new RequestLine("POST", "/login", "HTTP/1.1");
        final HttpHeaders headers = new HttpHeaders(new HttpHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded"));
        final HttpRequest request = new HttpRequest(requestLine, headers, "account=gugu&password=wrongpassword");
        final HttpResponse response = HttpResponse.builder().okBuild();

        assertThatThrownBy(() -> LoginController.getInstance().service(request, response))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인에 실패하였습니다.");
    }

    @Test
    @DisplayName("POST 요청 처리: 존재하지 않는 계정 정보로 로그인 실패")
    void handle_PostRequest_WithNonexistentUser() throws Exception {
        final RequestLine requestLine = new RequestLine("POST", "/login", "HTTP/1.1");
        final HttpHeaders httpHeaders = new HttpHeaders(new HttpHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded"));
        final HttpRequest request = new HttpRequest(requestLine, httpHeaders, "account=nonexistent&password=anypassword");
        final HttpResponse response = HttpResponse.builder().okBuild();

        assertThatThrownBy(() -> LoginController.getInstance().service(request, response))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessageContaining("로그인에 실패하였습니다.");
    }

    @Test
    @DisplayName("지원하지 않는 메소드 처리: 예외 반환")
    void handle_UnsupportedMethod() throws Exception {
        final RequestLine requestLine = new RequestLine("PUT", "/login", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null, null);
        final HttpResponse response = HttpResponse.builder().okBuild();

        assertThatThrownBy(() -> LoginController.getInstance().service(request, response))
                .isInstanceOf(UnsupportedOperationException.class)
                .hasMessageContaining("지원하지 않는 HTTP Method 입니다");
    }
}
