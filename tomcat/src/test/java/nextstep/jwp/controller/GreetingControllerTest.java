package nextstep.jwp.controller;

import nextstep.jwp.http.Headers;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.RequestInfo;
import nextstep.jwp.http.Response;
import org.apache.http.HttpHeader;
import org.apache.http.HttpMime;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;

class GreetingControllerTest {

    private final Controller controller = new GreetingController();

    @Test
    void 성공코드를_반환한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(GET, "/");
        final Request request = new Request(requestInfo, new Headers(), null);
        final Response response = new Response();

        final Response expected = new Response().header(HttpHeader.CONTENT_LENGTH, "12")
                .header(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue());

        // when
        controller.service(request, response);

        // then
        assertThat(response).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expected);
    }
}
