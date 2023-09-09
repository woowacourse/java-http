package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class AbstractControllerTest extends ControllerTestSupport {

    @Test
    void 지원하지_않는_메소드에_대해_405_페이지를_보여준다() {
        // given
        final String input = String.join("\r\n",
                "DELETE /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Accept: */*");
        final HttpRequest httpRequest = super.makeHttpRequest(input);

        // when
        final AbstractController abstractController = new AbstractController() {
            @Override
            protected HttpResponse doPost(final HttpRequest httpRequest) {
                return HttpResponse.init(httpRequest.getHttpVersion());
            }

            @Override
            protected HttpResponse doGet(final HttpRequest httpRequest) {
                return HttpResponse.init(httpRequest.getHttpVersion());
            }
        };
        final HttpResponse httpResponse = abstractController.service(httpRequest);
        final String actual = super.bytesToText(httpResponse.getBytes());

        // then
        assertThat(actual).contains(
                "HTTP/1.1 405 Method Not Allowed",
                "Content-Type: text/html;charset=utf-8");
    }
}
