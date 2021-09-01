package nextstep.jwp.application.controller;

import static nextstep.jwp.web.http.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.MethodUrl;
import nextstep.jwp.web.http.request.body.FormDataHttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpResponseImpl.Builder;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.http.session.HttpCookie;
import nextstep.jwp.web.http.session.HttpSessions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("로그인 기능을 테스트")
    @Test
    void loginTest() {
        //given
        HttpRequest request = new HttpRequest(new HttpHeaders(),
            HttpProtocol.HTTP1_1,
            new HttpCookie(""),
            HttpSessions.createSession(),
            new MethodUrl(POST, "/login"),
            new FormDataHttpRequestBody("account=gugu&password=password"));

        HttpResponse response = new Builder(request, HttpStatus.OK).build();

        //when
        LoginController loginController = new LoginController();
        loginController.doPost(request, response);

        //then
        assertThat(response.status()).isEqualTo(HttpStatus.FOUND);
    }
}