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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotFoundControllerTest {

    @DisplayName("화면을 찾지 못했을 때 처리")
    @Test
    void notFoundServiceTest() {
        //given
        HttpRequest request = new HttpRequest(new HttpHeaders(),
            HttpProtocol.HTTP1_1,
            new MethodUrl(GET, "/"),
            new TextHttpRequestBody(""));

        HttpResponse response = new Builder(request, HttpStatus.OK).build();

        //when
        NotFoundController notFoundController = new NotFoundController();
        notFoundController.service(request, response);

        //then
        assertThat(response.status()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.headers().get("Location").toValuesString()).isEqualTo("/404.html");
    }
}