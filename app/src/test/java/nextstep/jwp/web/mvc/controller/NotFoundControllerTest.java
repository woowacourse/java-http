package nextstep.jwp.web.mvc.controller;

import static nextstep.jwp.web.http.request.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.resource.FileType;
import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.MethodUrl;
import nextstep.jwp.web.http.request.body.TextHttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.http.response.body.TextHttpResponseBody;
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

        HttpResponse response = HttpResponse.ok(
            HttpProtocol.HTTP1_1,
            new TextHttpResponseBody("", FileType.HTML)
        );

        //when
        NotFoundController notFoundController = new NotFoundController();
        notFoundController.service(request, response);

        //then
        assertThat(response.status()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}