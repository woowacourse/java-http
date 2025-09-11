package com.techcourse.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.http.HttpVersion;
import com.techcourse.http.request.HttpRequest;
import com.techcourse.http.request.RequestBody;
import com.techcourse.http.request.RequestHeader;
import com.techcourse.http.response.HttpResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterRequestHandlerTest {

    private RegisterRequestHandler registerRequestHandler;

    @BeforeEach
    void setUp() {
        registerRequestHandler = new RegisterRequestHandler(HttpVersion.HTTP_1_1);
    }

    @DisplayName("Get 요청")
    @Test
    void serviceTest1() {
        // given
        String requestLine = "GET /register.html HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of("Host: localhost:8080"));
        RequestBody requestBody = RequestBody.empty();
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // when
        HttpResponse response = registerRequestHandler.service(httpRequest);

        // then
        String responseString = new String(response.toBytes());
        assertThat(responseString).contains("HTTP/1.1 200 OK");
        assertThat(responseString).contains("Content-Type: text/html;charset=utf-8");
    }

    @DisplayName("Post 요청: 새로운 회원을 등록하는 경우")
    @Test
    void serviceTest2() {
        // given
        String requestLine = "POST /register HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of(
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 50"
        ));
        RequestBody requestBody = RequestBody.from("account=newuser&password=newpass&email=new@example.com");
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // when
        HttpResponse response = registerRequestHandler.service(httpRequest);

        // then
        String responseString = new String(response.toBytes());
        assertAll(
                () -> assertThat(responseString).contains("HTTP/1.1 302 Found"),
                () -> assertThat(responseString).contains("Location: /index.html"),
                () -> assertThat(responseString).contains("Set-Cookie: JSESSIONID="),
                () -> assertThat(InMemoryUserRepository.findByAccount("newuser")).isPresent()
        );
    }

    @DisplayName("지원하지 않는 http 메서드의 경우")
    @Test
    void serviceTest3() {
        // given
        String requestLine = "PUT /register HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of("Host: localhost:8080"));
        RequestBody requestBody = RequestBody.empty();
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // when & then
        assertThatThrownBy(() -> registerRequestHandler.service(httpRequest))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("지원하지 않는 Http Method 입니다.");
    }

    @DisplayName("Post 요청 : 기존 세션 ID가 존재하는 경우")
    @Test
    void serviceTest4() {
        // given
        String requestLine = "POST /register HTTP/1.1";
        RequestHeader requestHeader = RequestHeader.from(List.of(
                "Host: localhost:8080",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: 50",
                "Cookie: JSESSIONID=existing-session-id"
        ));
        RequestBody requestBody = RequestBody.from("account=testuser2&password=testpass2&email=test2@example.com");
        HttpRequest httpRequest = HttpRequest.of(requestLine, requestHeader, requestBody);

        // when
        HttpResponse response = registerRequestHandler.service(httpRequest);

        // then
        String responseString = new String(response.toBytes());
        assertThat(responseString).doesNotContain("Set-Cookie: JSESSIONID=");
    }
}
