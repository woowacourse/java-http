package nextstep.jwp.webserver.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginPageControllerTest {

    @Test
    @DisplayName("GET 로그인 페이지 응답 테스트")
    public void accessLoginPageTest() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/login", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final LoginPageController loginPageController = new LoginPageController();

        // when
        final HttpResponse httpResponse = loginPageController.handle(httpRequest);

        //then
        final HttpResponse expected = new ResourceResponseTemplate().ok("/login.html");
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("올바른 ID와 PASSWORD로 로그인 요청했을 때, 리다이렉트 테스트")
    public void redirectIndexPageTest() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.POST, "/login", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).body("account=gugu&password=password").build();
        final LoginPageController loginPageController = new LoginPageController();

        // when
        final HttpResponse httpResponse = loginPageController.handle(httpRequest);

        //then
        final HttpResponse expected = new StringResponseTemplate().found("/index.html");
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("저장되지 않은 ID로 요청했을 때 401 페이지를 응답 테스트")
    public void redirectUnauthorizedPageIfIdIsNotSavedTest() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.POST, "/login", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).body("account=seed&password=password").build();
        final LoginPageController loginPageController = new LoginPageController();

        // when
        final HttpResponse httpResponse = loginPageController.handle(httpRequest);

        //then
        final HttpResponse expected = new ResourceResponseTemplate().unauthorized("/401.html");
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("틀린 비밀번호로 요청했을 때 401 페이지를 응답 테스트")
    public void redirectUnauthorizedPageIfPasswordMismatchTest() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.POST, "/login", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).body("account=gugu&password=wrong").build();
        final LoginPageController loginPageController = new LoginPageController();

        // when
        final HttpResponse httpResponse = loginPageController.handle(httpRequest);

        //then
        final HttpResponse expected = new ResourceResponseTemplate().unauthorized("/401.html");
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }
}
