package nextstep.jwp.controller;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.request.RequestLine;
import nextstep.jwp.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RegisterController 테스트")
class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    @DisplayName("회원가입 페이지 GET 요청 테스트")
    @Test
    void getRegister() {
        //given
        final RequestLine requestLine = new RequestLine("GET /register HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine, null, null);
        final HttpResponse response = new HttpResponse();

        //when
        registerController.service(request, response);

        //then
        assertThat(response.getBytes()).isNotEmpty();
    }
}