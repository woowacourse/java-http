package nextstep.jwp.webserver.controller;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.*;
import nextstep.jwp.framework.util.Resources;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorControllerTest {

    @Test
    @DisplayName("에러 페이지에 접근했을 때 HTTP 응답 테스트")
    void handleTest() throws IOException {

        //expected
        final String response = Resources.readString("/404.html");
        final HttpResponse expected = HttpResponse.ok()
                                                  .body(response)
                                                  .contentLength(response.getBytes().length)
                                                  .build();

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/error", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final ErrorController errorController = new ErrorController();

        // when
        final HttpResponse httpResponse = errorController.handle(httpRequest);

        //then
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }
}
