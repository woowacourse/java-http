package nextstep.jwp.webserver.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.*;
import nextstep.jwp.framework.http.template.RedirectResponseTemplate;
import nextstep.jwp.framework.http.template.ResourceResponseTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class LoginPageControllerTest {

    @Test
    @DisplayName("세션이 존재하지 않을 경우 로그인 페이지 응답 테스트")
    void accessLoginPageTest() {

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
    @DisplayName("세션이 존재할 경우 인덱스 페이지 응답 테스트")
    void redirectIndexPageIfSessionExistsTest() {

        // given
        final HttpSession httpSession = new HttpSession();
        httpSession.setAttribute("user", "user");

        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/login", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest =
                new HttpRequest.Builder().requestLine(requestLine)
                                         .cookie(new Cookie(HttpSession.JSESSIONID, httpSession.getId()).toString())
                                         .build();

        // when
        final HttpResponse httpResponse = new LoginPageController().handle(httpRequest);

        //then
        final HttpResponse expected = new RedirectResponseTemplate().found("/index.html");
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("올바른 ID와 PASSWORD로 로그인 요청했을 때, 리다이렉트 테스트")
    void redirectIndexPageTest() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.POST, "/login", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).body("account=gugu&password=password").build();
        final LoginPageController loginPageController = new LoginPageController();

        // when
        final HttpResponse httpResponse = loginPageController.handle(httpRequest);

        //then
        final HttpResponse expected = new RedirectResponseTemplate().found("/index.html");
        assertThat(httpResponse).usingRecursiveComparison()
                                .ignoringFields("httpHeaders.headers")
                                .isEqualTo(expected);

        final String cookie = httpResponse.getHttpHeaders().getHeaders().get(HttpHeaders.SET_COOKIE);
        assertThat(cookie).isNotNull();
    }

    @Test
    @DisplayName("저장되지 않은 ID로 요청했을 때 401 페이지를 응답 테스트")
    void redirectUnauthorizedPageIfIdIsNotSavedTest() {

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
    void redirectUnauthorizedPageIfPasswordMismatchTest() {

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
