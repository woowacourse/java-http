package org.apache.coyote.controller;

import org.apache.coyote.controller.exception.UnsupportedRequestMethodException;
import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.apache.coyote.httpresponse.handler.HandlerTestSupport;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class AbstractControllerTest extends HandlerTestSupport {

    @Test
    void 지원하지_않는_메소드에_대해_예외를_터트린다() {
        // given
        final String input = String.join("\r\n",
                "DELETE /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*");
        final HttpRequest httpRequest = super.makeHttpRequest(input);
        final HttpResponse httpResponse = HttpResponse.init(httpRequest.getHttpVersion());

        // when
        final AbstractController abstractController = new AbstractController() {
            @Override
            protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
            }

            @Override
            protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
            }
        };

        // then
        assertThatThrownBy(() -> abstractController.service(httpRequest, httpResponse))
                .isInstanceOf(UnsupportedRequestMethodException.class);
    }
}
