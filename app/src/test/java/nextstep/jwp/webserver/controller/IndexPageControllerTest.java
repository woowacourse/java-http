package nextstep.jwp.webserver.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.framework.http.*;
import nextstep.jwp.framework.http.template.ResourceResponseTemplate;

import static org.assertj.core.api.Assertions.assertThat;

public class IndexPageControllerTest {

    @Test
    @DisplayName("인덱스 페이지에 접근했을 때 HTTP 응답 테스트")
    void doGetTest() {

        // given
        final RequestLine requestLine = new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1);
        final HttpRequest httpRequest = new HttpRequest.Builder().requestLine(requestLine).build();
        final IndexPageController indexPageController = new IndexPageController();

        // when
        final HttpResponse httpResponse = indexPageController.handle(httpRequest);

        //then
        final HttpResponse expected = new ResourceResponseTemplate().ok("/index.html");
        assertThat(httpResponse).usingRecursiveComparison().isEqualTo(expected);
    }

}
