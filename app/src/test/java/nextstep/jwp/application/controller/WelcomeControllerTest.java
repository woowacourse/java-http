package nextstep.jwp.application.controller;

import static nextstep.jwp.web.http.request.HttpMethod.GET;
import static nextstep.jwp.web.http.request.HttpMethod.POST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import nextstep.jwp.application.db.InMemoryUserRepository;
import nextstep.jwp.application.domain.User;
import nextstep.jwp.resource.FileType;
import nextstep.jwp.web.http.HttpHeaders;
import nextstep.jwp.web.http.HttpProtocol;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.request.MethodUrl;
import nextstep.jwp.web.http.request.body.FormDataHttpRequestBody;
import nextstep.jwp.web.http.request.body.HttpRequestBody;
import nextstep.jwp.web.http.request.body.TextHttpRequestBody;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.HttpStatus;
import nextstep.jwp.web.http.response.body.TextHttpResponseBody;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class WelcomeControllerTest {

    @DisplayName("인덱스 페이지로 이동 기능")
    @Test
    void welcomeTest() {
        //given
        HttpRequest request = new HttpRequest(new HttpHeaders(),
            HttpProtocol.HTTP1_1,
            new MethodUrl(GET, "/null"),
            new TextHttpRequestBody(""));

        HttpResponse response = HttpResponse.ok(
            HttpProtocol.HTTP1_1,
            new TextHttpResponseBody("", FileType.HTML)
        );

        //when
        WelcomeController welcomeController = new WelcomeController();
        welcomeController.doGet(request, response);

        //then
        assertThat(request.methodUrl().method()).isEqualTo(GET);
        assertThat(request.methodUrl().url()).isEqualTo("/index");
    }
}