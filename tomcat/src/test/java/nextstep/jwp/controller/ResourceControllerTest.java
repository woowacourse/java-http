package nextstep.jwp.controller;

import nextstep.jwp.exception.CustomNotFoundException;
import nextstep.jwp.http.Headers;
import nextstep.jwp.http.Request;
import nextstep.jwp.http.RequestInfo;
import nextstep.jwp.http.Response;
import org.apache.http.*;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpMethod.GET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

class ResourceControllerTest {

    private final Controller controller = new ResourceController();

    @Test
    void 해당하는_자원을_찾으면_OK_응답을_반환한다() throws Exception {
        // given
        final RequestInfo requestInfo = new RequestInfo(GET, "/index.html");
        final Request request = new Request(requestInfo, new Headers(), null);
        final Headers headers = new Headers();
        headers.put(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue());
        headers.put(HttpHeader.CONTENT_LENGTH, "5564");
        final Response expected = new Response(headers);

        // when
        final Response actual = controller.execute(request);

        // then
        assertAll(
                () -> assertThat(actual).usingRecursiveComparison()
                        .ignoringFields("content")
                        .isEqualTo(expected)
        );
    }

    @Test
    void 해당하는_자원을_찾지_못하면_예외가_발생한다() {
        // given
        final RequestInfo requestInfo = new RequestInfo(GET, "/notfound.html");
        final Request request = new Request(requestInfo, new Headers(), null);

        // when, then
        assertThatThrownBy(() -> controller.execute(request))
                .isInstanceOf(CustomNotFoundException.class);
    }
}
