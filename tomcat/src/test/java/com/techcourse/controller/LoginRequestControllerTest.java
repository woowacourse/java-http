package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.NotFoundException;
import com.techcourse.exception.UncheckedServletException;
import java.util.List;
import org.apache.coyote.http.HttpVersion;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestBody;
import org.apache.coyote.http.request.RequestHeader;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.HttpResponse;
import org.apache.coyote.http.session.Session;
import org.apache.coyote.http.session.SessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginRequestControllerTest {

    private LoginRequestController loginRequestController;

    @BeforeEach
    void setUp() {
        loginRequestController = new LoginRequestController(HttpVersion.HTTP_1_1);
    }

    @DisplayName("세션 없는 GET 요청")
    @Test
    void serviceTest1() {
        // given
        String requestLineString = "GET /login.html HTTP/1.1";
        RequestLine requestLine = RequestLine.from(requestLineString);
        RequestHeader requestHeader = RequestHeader.from(List.of("Host: localhost:8080"));
        RequestBody requestBody = RequestBody.empty();
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // when
        HttpResponse response = loginRequestController.service(httpRequest);

        // then
        String responseString = new String(response.toBytes());
        assertThat(responseString).contains("HTTP/1.1 200 OK");
        assertThat(responseString).contains("Content-Type: text/html;charset=utf-8");
    }

    @DisplayName("세션 있는 GET 요청")
    @Test
    void serviceTest2() {
        // given
        Session session = Session.newSession();
        SessionRepository.save(session);

        String requestLineString = "GET /login.html HTTP/1.1";
        RequestLine requestLine = RequestLine.from(requestLineString);
        RequestHeader requestHeader = RequestHeader.from(List.of(
                "Host: localhost:8080",
                "Cookie: JSESSIONID=" + session.getId()
        ));
        RequestBody requestBody = RequestBody.empty();
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // when
        HttpResponse response = loginRequestController.service(httpRequest);

        // then
        String responseString = new String(response.toBytes());
        assertThat(responseString).contains("HTTP/1.1 302 Found");
        assertThat(responseString).contains("Location: /index.html");
    }

    @DisplayName("유효한 로그인 요청인 경우")
    @Test
    void serviceTest3() {
        // given
        String requestLineString = "POST /login HTTP/1.1";
        RequestLine requestLine = RequestLine.from(requestLineString);
        RequestHeader requestHeader = RequestHeader.from(List.of(
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 25"
        ));
        RequestBody requestBody = RequestBody.from("account=gugu&password=password");
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // when
        HttpResponse response = loginRequestController.service(httpRequest);

        // then
        String responseString = new String(response.toBytes());

        assertAll(
                () -> assertThat(responseString).contains("HTTP/1.1 302 Found"),
                () -> assertThat(responseString).contains("Location: /index.html"),
                () -> assertThat(responseString).contains("Set-Cookie: JSESSIONID=")
        );
    }

    @DisplayName("잘못된 비밀번호를 입력한 경우")
    @Test
    void serviceTest4() {
        // given
        String requestLineString = "POST /login HTTP/1.1";
        RequestLine requestLine = RequestLine.from(requestLineString);
        RequestHeader requestHeader = RequestHeader.from(List.of(
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 25"
        ));
        RequestBody requestBody = RequestBody.from("account=gugu&password=wrongpassword");
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // when
        HttpResponse response = loginRequestController.service(httpRequest);

        // then
        String responseString = new String(response.toBytes());
        assertThat(responseString).contains("HTTP/1.1 302 Found");
        assertThat(responseString).contains("Location: /401.html");
    }

    @DisplayName("존재하지 않는 사용자의 경우")
    @Test
    void serviceTest5() {
        // given
        String requestLineString = "POST /login HTTP/1.1";
        RequestLine requestLine = RequestLine.from(requestLineString);
        RequestHeader requestHeader = RequestHeader.from(List.of(
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 25"
        ));
        RequestBody requestBody = RequestBody.from("account=nonexistent&password=password");
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // when & then
        assertThatThrownBy(() -> loginRequestController.service(httpRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("존재하지 않는 유저입니다.");
    }

    @DisplayName("지원하지 않는 http method의 경우")
    @Test
    void serviceTest6() {
        // given
        String requestLineString = "PUT /login HTTP/1.1";
        RequestLine requestLine = RequestLine.from(requestLineString);
        RequestHeader requestHeader = RequestHeader.from(List.of("Host: localhost:8080"));
        RequestBody requestBody = RequestBody.empty();
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // when & then
        assertThatThrownBy(() -> loginRequestController.service(httpRequest))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("지원하는 Http Method가 아닙니다.");
    }
}
