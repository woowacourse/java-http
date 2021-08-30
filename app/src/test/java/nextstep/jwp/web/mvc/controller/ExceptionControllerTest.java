package nextstep.jwp.web.mvc.controller;

import static nextstep.jwp.web.http.request.HttpMethod.GET;
import static nextstep.jwp.web.http.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.application.controller.RegisterController;
import nextstep.jwp.resource.FileType;
import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.MethodUrl;
import nextstep.jwp.web.http.request.body.FormDataHttpRequestBody;
import nextstep.jwp.web.http.request.body.TextHttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.http.response.body.TextHttpResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionControllerTest {

    @DisplayName("예외 발생 시 처리할 컨트롤러 테스트")
    @Test
    void exceptionServiceTest() {
        //given
        HttpRequest httpRequest = new HttpRequest(new HttpHeaders(),
            HttpProtocol.HTTP1_1,
            new MethodUrl(GET, "/"),
            new TextHttpRequestBody(""));

        HttpResponse response = HttpResponse.ok(
            HttpProtocol.HTTP1_1,
            new TextHttpResponseBody("", FileType.HTML)
        );

        //when
        ExceptionController exceptionController = new ExceptionController();
        exceptionController.service(httpRequest, response);

        //then
        assertThat(response.status()).isEqualTo(HttpStatus.INTERNAL_ERROR);
    }
}