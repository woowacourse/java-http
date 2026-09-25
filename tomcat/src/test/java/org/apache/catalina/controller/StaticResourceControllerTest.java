package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestHeaders;
import org.apache.coyote.http11.RequestLine;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceControllerTest {

    private final Controller controller = new StaticResourceController();

    @Test
    void 파일이_있으면_확장자에_맞는_Content_Type으로_응답한다() throws Exception {
        final HttpResponse response = new HttpResponse();

        controller.service(request("GET /css/styles.css HTTP/1.1"), response);

        final String message = toString(response);
        assertThat(message).startsWith("HTTP/1.1 200 OK ");
        assertThat(message).contains("Content-Type: text/css;charset=utf-8 ");
    }

    @Test
    void 파일이_없으면_404_페이지로_응답한다() throws Exception {
        final HttpResponse response = new HttpResponse();

        controller.service(request("GET /nothing.html HTTP/1.1"), response);

        final String message = toString(response);
        assertThat(message).startsWith("HTTP/1.1 404 Not Found ");
        assertThat(message).contains("<title>404 Error");
    }

    private HttpRequest request(final String requestLine) {
        return HttpRequest.of(RequestLine.from(requestLine), RequestHeaders.from(List.of()), "");
    }

    private String toString(final HttpResponse response) {
        return new String(response.getBytes(), UTF_8);
    }
}
