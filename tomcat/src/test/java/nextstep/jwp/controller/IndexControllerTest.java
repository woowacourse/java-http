package nextstep.jwp.controller;

import static org.apache.coyote.http.HttpStatusCode.OK;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.http.HttpStatusCode;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IndexControllerTest extends ControllerTest {

    @Test
    @DisplayName("GET /index.html 요청을 처리한다.")
    void doGet() throws IOException {
        // given
        final HttpResponse expectedResponse = HttpResponse.init(OK)
                .setBodyByPath("/index.html");
        final String expected = new String(expectedResponse.toResponseBytes());

        final Controller indexController = IndexController.getInstance();
        final HttpRequest httpRequest = getHttpRequest("GET", "/index.html");

        // when
        final HttpResponse httpResponse = indexController.doService(httpRequest);

        // then
        final String actual = new String(httpResponse.toResponseBytes());

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST /index.html 요청은 405를 응답한다.")
    void doPost() throws IOException {
        // given
        final HttpResponse expectedResponse = HttpResponse.init(HttpStatusCode.METHOD_NOT_ALLOWED)
                .setBody(HttpStatusCode.METHOD_NOT_ALLOWED.getMessage());
        final String expected = new String(expectedResponse.toResponseBytes());

        final Controller indexController = IndexController.getInstance();
        final HttpRequest httpRequest = getHttpRequest("POST", "/index.html");

        // when
        final HttpResponse httpResponse = indexController.doService(httpRequest);

        // then
        final String actual = new String(httpResponse.toResponseBytes());

        assertThat(actual).isEqualTo(expected);
    }
}
