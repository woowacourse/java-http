package nextstep.jwp.controller;

import nextstep.jwp.exception.CustomNotFoundException;
import org.apache.coyote.HttpHeader;
import org.apache.coyote.HttpMime;
import org.apache.coyote.support.Request;
import org.apache.coyote.support.RequestInfo;
import org.apache.coyote.Headers;
import nextstep.jwp.http.MockOutputStream;
import org.apache.coyote.support.Response;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.HttpMethod.GET;
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
        final Response response = new Response(new MockOutputStream());

        final Response expected = new Response(new MockOutputStream()).header(HttpHeader.CONTENT_TYPE, HttpMime.TEXT_HTML.getValue())
                .header(HttpHeader.CONTENT_LENGTH, "5564");

        // when
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(response).usingRecursiveComparison()
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
        assertThatThrownBy(() -> controller.service(request, new Response(new MockOutputStream())))
                .isInstanceOf(CustomNotFoundException.class);
    }
}
