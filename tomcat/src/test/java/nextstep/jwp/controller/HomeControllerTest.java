package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HomeControllerTest {

    private static final String EXPECTED_RESPONSE_MESSAGE = String.join(
            System.lineSeparator(),
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 12 ",
            "",
            "Hello world!"
    );

    @Test
    void uri가_일치하면_GET_요청을_처리한다() {
        // given
        HomeController homeController = new HomeController();

        String uri = "/";
        HttpRequest httpRequest = HttpRequest.of("GET " + uri + " HTTP/1.1 ", "Host: localhost:8080 ");
        HttpResponse httpResponse = new HttpResponse(httpRequest.httpVersion());

        // when
        homeController.service(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.responseMessage()).isEqualTo(EXPECTED_RESPONSE_MESSAGE);
    }

    @Test
    void uri가_일치하지_않으면_요청을_처리하지_않는다() {
        // given
        HomeController homeController = new HomeController();

        String wrongUri = "/wrong";
        HttpRequest httpRequest = HttpRequest.of("GET " + wrongUri + " HTTP/1.1 ", "Host: localhost:8080 ");
        HttpResponse httpResponse = new HttpResponse(httpRequest.httpVersion());

        // when
        homeController.service(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.responseMessage()).isNotEqualTo(EXPECTED_RESPONSE_MESSAGE);
    }
}
