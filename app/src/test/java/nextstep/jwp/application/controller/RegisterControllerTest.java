package nextstep.jwp.application.controller;

import static nextstep.jwp.web.http.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.resource.FileType;
import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.MethodUrl;
import nextstep.jwp.web.http.request.body.FormDataHttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.http.response.body.TextHttpResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class RegisterControllerTest {

    @DisplayName("유저 등록 기능을 테스트")
    @Test
    void registerTest() {
        //given
        HttpRequest httpRequest = new HttpRequest(new HttpHeaders(),
            HttpProtocol.HTTP1_1,
            new MethodUrl(POST, "/register"),
            new FormDataHttpRequestBody("account=oguogu&password=password"));

        HttpResponse response = HttpResponse.ok(
            HttpProtocol.HTTP1_1,
            new TextHttpResponseBody("", FileType.HTML)
        );

        //when
        RegisterController registerController = new RegisterController();
        registerController.doPost(httpRequest, response);

        //then
        assertThat(response.status()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.headers().get("Location").toValuesString()).isEqualTo("/login");
    }
}
