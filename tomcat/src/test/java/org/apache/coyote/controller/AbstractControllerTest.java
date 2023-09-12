package org.apache.coyote.controller;

import org.apache.coyote.httprequest.HttpRequest;
import org.apache.coyote.httpresponse.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

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
        final HttpResponse httpResponse = HttpResponse.init(httpRequest.getHttpVersion());

        // when
        final AbstractController abstractController = new AbstractController() {
            @Override
            protected void doPost(final HttpRequest httpRequest, final HttpResponse httpResponse) {
                HttpResponse.init(httpRequest.getHttpVersion());
            }

            @Override
            protected void doGet(final HttpRequest httpRequest, final HttpResponse httpResponse) {
                HttpResponse.init(httpRequest.getHttpVersion());
            }
        };
        abstractController.service(httpRequest, httpResponse);
        final String actual = super.bytesToText(httpResponse.getBytes());

        // then
        assertThat(actual).contains(
                "HTTP/1.1 405 Method Not Allowed",
                "Content-Type: text/html;charset=utf-8");
    }
}
