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
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_LENGTH, "12");
        headers.put(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue());
        final Response expected = new Response(headers);

        // when
        final Response actual = controller.execute(request);

        // then
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("content")
                .isEqualTo(expected);
    }
}
