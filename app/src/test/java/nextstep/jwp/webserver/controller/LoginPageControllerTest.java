package nextstep.jwp.webserver.controller;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.*;
import nextstep.jwp.framework.util.Resources;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginPageControllerTest {

    @Test
    @DisplayName("로그인 페이지에 쿼리 없이 요청했을 때 로그인 페이지를 응답 테스트")
    public void accessLoginPageTest() throws IOException {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/login", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final LoginPageController loginPageController = new LoginPageController();

        // when
        final HttpResponse httpResponse = loginPageController.handle(httpRequest);

        //then
        final String response = Resources.readString("/login.html");
        final HttpResponse expected = HttpResponse.ok()
                                                  .body(response)
                                                  .contentLength(response.getBytes().length)
                                                  .build();

        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 페이지에 저장된 ID와 PASSWORD로 요청했을 때 인덱스 페이지를 응답 테스트")
    public void redirectIndexPageTest() throws IOException {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/login?account=gugu&password=password", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final LoginPageController loginPageController = new LoginPageController();

        // when
        final HttpResponse httpResponse = loginPageController.handle(httpRequest);

        //then
        final String response = Resources.readString("/index.html");
        final HttpResponse expected = HttpResponse.found("/index")
                                                  .body(response)
                                                  .contentLength(response.getBytes().length)
                                                  .build();

        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    @DisplayName("로그인 페이지에 저장되지 않은 ID와 PASSWORD로 요청했을 때 인덱스 페이지를 응답 테스트")
    public void redirectUnauthorizedPageTest() throws IOException {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/login?account=seed&password=password", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final LoginPageController loginPageController = new LoginPageController();

        // when
        final HttpResponse httpResponse = loginPageController.handle(httpRequest);

        //then
        final String response = Resources.readString("/401.html");
        final HttpResponse expected = HttpResponse.found("/401.html")
                                                  .body(response)
                                                  .contentLength(response.getBytes().length)
                                                  .build();

        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }
}
