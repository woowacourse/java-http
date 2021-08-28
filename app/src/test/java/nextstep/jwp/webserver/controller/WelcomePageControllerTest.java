package nextstep.jwp.webserver.controller;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

public class WelcomePageControllerTest {

    @Test
    @DisplayName("메인 페이지에 접근했을 때 HTTP 응답 테스트")
    void handleTest() throws IOException {

        //expected
        final String response = "Hello world!";
        final HttpResponse expected = HttpResponse.ok()
                                                  .body(response)
                                                  .contentLength(response.getBytes().length)
                                                  .build();

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final WelcomePageController welcomePageController = new WelcomePageController();

        // when
        final HttpResponse httpResponse = welcomePageController.handle(httpRequest);

        //then
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }
}
