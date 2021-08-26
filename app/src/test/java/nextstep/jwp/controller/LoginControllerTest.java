package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("LoginController 테스트")
class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @DisplayName("로그인 페이지 GET 요청 테스트")
    @Test
    void getLogin() {
        //given
        final RequestLine requestLine = new RequestLine("GET /login HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null, null);
        final HttpResponse response = new HttpResponse();

        //when
        loginController.service(request, response);

        //then
        assertThat(response.getBytes().length).isGreaterThan(0);
    }
}