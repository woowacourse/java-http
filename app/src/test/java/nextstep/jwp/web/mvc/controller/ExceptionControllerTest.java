package nextstep.jwp.web.mvc.controller;

import static nextstep.jwp.web.http.request.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.MethodUrl;
import nextstep.jwp.web.http.request.body.TextHttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpResponseImpl.Builder;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.http.session.HttpCookie;
import nextstep.jwp.web.http.session.HttpSessions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionControllerTest {

    @DisplayName("예외 발생 시 처리할 컨트롤러 테스트")
    @Test
    void exceptionServiceTest() {
        //given
        HttpRequest request = new HttpRequest(new HttpHeaders(),
            HttpProtocol.HTTP1_1,
            new HttpCookie(""),
            HttpSessions.createSession(),
            new MethodUrl(GET, "/"),
            new TextHttpRequestBody(""));

        HttpResponse response = new Builder(request, HttpStatus.OK).build();

        //when
        ExceptionController exceptionController = new ExceptionController();
        exceptionController.service(request, response);

        //then
        assertThat(response.status()).isEqualTo(HttpStatus.INTERNAL_ERROR);
    }
}