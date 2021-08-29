package nextstep.jwp.webserver.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.*;
import nextstep.jwp.framework.http.template.ResourceResponseTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterPageControllerTest {

    @Test
    @DisplayName("GET 회원가입 페이지 응답 테스트")
    public void accessRegisterPageTest() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/register", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final RegisterPageController registerPageController = new RegisterPageController();

        // when
        final HttpResponse httpResponse = registerPageController.handle(httpRequest);

        //then
        final HttpResponse expected = new ResourceResponseTemplate().ok("/register.html");
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }
}
