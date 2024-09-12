package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeaders;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.util.StaticFileResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private RegisterController registerController;

    @BeforeEach
    void setUp() {
        registerController = new RegisterController();
    }

    @Test
    void doGetTest_whenSessionExists_shouldRedirectToIndex() throws Exception {
        // given
        Session session = new Session("session-id");
        session.setAttribute("user", new User("testAccount", "password", "email@example.com"));
        SessionManager.getInstance().add(session);

        HttpRequestStartLine startLine = new HttpRequestStartLine("GET", "/register", "HTTP/1.1");
        HttpRequestHeaders headers = new HttpRequestHeaders(Map.of("Cookie", "JSESSIONID=" + session.getId()));
        HttpRequestBody body = new HttpRequestBody();
        HttpRequest request = new HttpRequest(startLine, headers, body);
        HttpResponse response = new HttpResponse();

        // when
        registerController.doGet(request, response);

        // then
        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.getStatusCode()),
                () -> assertThat(response.getHeaders().get("Location")).contains("/index.html"));
    }

    @Test
    void doGetTest_whenSessionNotExists_shouldServeRegisterPage() throws Exception {
        // given
        HttpRequestStartLine startLine = new HttpRequestStartLine("GET", "/register", "HTTP/1.1");
        HttpRequestHeaders headers = new HttpRequestHeaders();
        HttpRequestBody body = new HttpRequestBody();
        HttpRequest request = new HttpRequest(startLine, headers, body);
        HttpResponse response = new HttpResponse();

        // when
        registerController.doGet(request, response);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getStatusCode());
        assertThat(response.getBody()).isEqualTo(StaticFileResponseUtils.readStaticFile("/register.html"));
    }

    @Test
    void doPostTest_whenRegistrationIsSuccessful_shouldRedirectToIndexAndSetSession() throws Exception {
        // given
        HttpRequestStartLine startLine = new HttpRequestStartLine("POST", "/register", "HTTP/1.1");

        HttpRequestHeaders headers = new HttpRequestHeaders(
                Map.of("Content-Type", "application/x-www-form-urlencoded"));
        HttpRequestBody body = new HttpRequestBody("account=testAccount&password=password&email=email@email.com");
        HttpRequest request = new HttpRequest(startLine, headers, body);
        HttpResponse response = new HttpResponse();

        // when
        registerController.doPost(request, response);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.getStatusCode());
        assertThat(response.getHeaders().get("Location")).isEqualTo("/index.html");
        assertThat(response.getHeaders().get("Set-Cookie")).isNotNull();  // 세션 쿠키가 설정되었는지 확인
    }

    @Test
    void doPostTest_whenMissingRequiredFields_shouldThrowUncheckedServletException() throws Exception {
        // given
        HttpRequestStartLine startLine = new HttpRequestStartLine("POST", "/register", "HTTP/1.1");
        HttpRequestHeaders headers = new HttpRequestHeaders(
                Map.of("Content-Type", "application/x-www-form-urlencoded"));
        HttpRequestBody body = new HttpRequestBody("account=testAccount&password=password");
        HttpRequest request = new HttpRequest(startLine, headers, body);
        HttpResponse response = new HttpResponse();

        // when & then
        assertThatThrownBy(() -> registerController.doPost(request, response))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("요청이 잘못되었습니다.");
    }
}
