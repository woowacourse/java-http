package com.techcourse.controller;

import com.techcourse.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private final SessionManager sessionManager = new SessionManager();

    @DisplayName("쿠키 없이 로그인 페이지로 Get 요청 시 200 반환")
    @Test
    void login_noCookie() throws Exception {
        //given
        LoginController controller = new LoginController();
        HttpRequestLine requestLine = HttpRequestLine.from("GET /login HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        HttpHeaders httpHeaders = HttpHeaders.from(headers);
        HttpRequest request = new HttpRequest(requestLine, httpHeaders, null);
        HttpResponse response = new HttpResponse();

        //when
        controller.doGet(request, response);

        //then
        assertThat(response.getHttpStatus()).isEqualTo(200);
    }

    @DisplayName("쿠키와 함께 로그인 페이지로 Get 요청 시 302 반환")
    @Test
    void login_Cookie() throws Exception {
        //given
        LoginController controller = new LoginController();
        Session session = new Session("sessionId");
        session.setAttribute("user", new User(1L, "account", "password", "email"));
        sessionManager.add(session);

        HttpRequestLine requestLine = HttpRequestLine.from("GET /login HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", "JSESSIONID=sessionId");
        HttpHeaders httpHeaders = HttpHeaders.from(headers);
        HttpRequest request = new HttpRequest(requestLine, httpHeaders, null);
        HttpResponse response = new HttpResponse();

        //when
        controller.doGet(request, response);

        //then
        assertThat(response.getHttpStatus()).isEqualTo(302);
    }

    @DisplayName("다른 세션을 저장한 쿠키와 함께 로그인 페이지로 Get 요청 시 200 반환")
    @Test
    void login_differentCookie() throws Exception {
        //given
        LoginController controller = new LoginController();
        Session session = new Session("sessionId");
        session.setAttribute("user", new User(1L, "account", "password", "email"));
        sessionManager.add(session);

        HttpRequestLine requestLine = HttpRequestLine.from("GET /login HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        headers.put("Cookie", "JSESSIONID=differentSessionId");
        HttpHeaders httpHeaders = HttpHeaders.from(headers);
        HttpRequest request = new HttpRequest(requestLine, httpHeaders, null);
        HttpResponse response = new HttpResponse();

        //when
        controller.doGet(request, response);

        //then
        assertThat(response.getHttpStatus()).isEqualTo(200);
    }

    @DisplayName("로그인 Post 요청 로그인 성공 시 302 반환")
    @Test
    void login_success() throws Exception {
        //given
        LoginController controller = new LoginController();
        HttpRequestLine requestLine = HttpRequestLine.from("POST /login HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        HttpHeaders httpHeaders = HttpHeaders.from(headers);
        HttpRequestBody httpRequestBody = HttpRequestBody.from(
                "account=gugu&password=password&email=hkkang%40woowahan.com");

        HttpRequest request = new HttpRequest(requestLine, httpHeaders, httpRequestBody);
        HttpResponse response = new HttpResponse();

        //when
        controller.doPost(request, response);

        //then
        assertThat(response.getHttpStatus()).isEqualTo(302);
    }

    @DisplayName("로그인 잘못된 비밀번호 요청 시 302 반환")
    @Test
    void login_failure() throws Exception {
        //given
        LoginController controller = new LoginController();
        HttpRequestLine requestLine = HttpRequestLine.from("POST /login HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        HttpHeaders httpHeaders = HttpHeaders.from(headers);
        HttpRequestBody httpRequestBody = HttpRequestBody.from(
                "account=gugu&password=wrong&email=hkkang%40woowahan.com");

        HttpRequest request = new HttpRequest(requestLine, httpHeaders, httpRequestBody);
        HttpResponse response = new HttpResponse();

        //when
        controller.doPost(request, response);

        //then
        assertThat(response.getHttpStatus()).isEqualTo(302);
    }
}