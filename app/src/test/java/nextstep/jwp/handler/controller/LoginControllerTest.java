package nextstep.jwp.handler.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import nextstep.jwp.handler.modelandview.ModelAndView;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestHeaders;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.http.response.HttpStatus;
import nextstep.jwp.http.session.HttpSession;
import nextstep.jwp.http.session.HttpSessions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @Test
    void doGet() {
        RequestLine requestLine = RequestLine.of("GET /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Collections.emptyList());

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        HttpResponse httpResponse = new HttpResponse();

        ModelAndView modelAndView = loginController.doGet(httpRequest, httpResponse);
        assertThat(modelAndView.getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("세션에 로그인 내역이 존재시 index 페이지로 리다이렉트한다")
    @Test
    void doGetWithLoginSession() {
        HttpResponse loginResponse = doLoginSuccessfully();
        String sessionItem = loginResponse.getHeader("Set-Cookie");

        RequestLine requestLine = RequestLine.of("GET /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(
                Arrays.asList("Cookie: " + sessionItem)
        );

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, "");
        HttpResponse httpResponse = new HttpResponse();

        ModelAndView modelAndView = loginController.doGet(httpRequest, httpResponse);
        assertThat(modelAndView.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(httpResponse.getHeader("Location")).isEqualTo("index.html");
    }

    @DisplayName("로그인 성공 시, 세션에 사용자 정보 저장")
    @Test
    void doPost() {
        final String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("POST /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        ModelAndView modelAndView = loginController.doPost(httpRequest, httpResponse);

        String sessionItem = httpResponse.getHeader("Set-Cookie");
        String sessionId = sessionItem.split("=")[1];
        HttpSession session = HttpSessions.getSession(sessionId);

        assertThat(modelAndView.getHttpStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(session.getAttribute("user")).isNotNull();
    }

    @DisplayName("로그인 실패 시, 401 응답")
    @Test
    void doPostWithInvalidUser() {
        final String requestBody = "account=corgi&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("POST /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        ModelAndView modelAndView = loginController.doPost(httpRequest, httpResponse);

        assertThat(modelAndView.getHttpStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private HttpResponse doLoginSuccessfully() {
        final String requestBody = "account=gugu&password=password&email=hkkang%40woowahan.com";

        RequestLine requestLine = RequestLine.of("POST /login HTTP/1.1");
        RequestHeaders requestHeaders = RequestHeaders.of(Arrays.asList("Content-Length: " + requestBody.length()));

        HttpRequest httpRequest = new HttpRequest(requestLine, requestHeaders, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        loginController.doPost(httpRequest, httpResponse);
        return httpResponse;
    }
}
