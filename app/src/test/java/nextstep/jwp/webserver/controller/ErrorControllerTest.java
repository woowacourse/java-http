package nextstep.jwp.webserver.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.*;
import nextstep.jwp.framework.http.template.ResourceResponseTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorControllerTest {

    @Test
    @DisplayName("에러 페이지에 접근했을 때 HTTP 응답 테스트")
    void doGetTest() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/error", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final ErrorController errorController = new ErrorController();

        // when
        final HttpResponse httpResponse = errorController.handle(httpRequest);

        //then
        final HttpResponse expected = new ResourceResponseTemplate().ok("/404.html");
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }
}
