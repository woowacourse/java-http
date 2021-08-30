package nextstep.jwp.application.controller;

import static nextstep.jwp.web.http.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import nextstep.jwp.resource.FileType;
import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.MethodUrl;
import nextstep.jwp.web.http.request.body.FormDataHttpRequestBody;
import nextstep.jwp.web.http.request.body.HttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.http.response.body.TextHttpResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("로그인 기능을 테스트")
    @Test
    void loginTest() {
        //given
        HttpRequest httpRequest = new HttpRequest(new HttpHeaders(),
            HttpProtocol.HTTP1_1,
            new MethodUrl(POST, "/login"),
            new FormDataHttpRequestBody("account=gugu&password=password"));

        HttpResponse response = HttpResponse.ok(
            HttpProtocol.HTTP1_1,
            new TextHttpResponseBody("", FileType.HTML)
        );

        //when
        LoginController loginController = new LoginController();
        loginController.doPost(httpRequest, response);

        //then
        assertThat(response.status()).isEqualTo(HttpStatus.FOUND);
    }
}