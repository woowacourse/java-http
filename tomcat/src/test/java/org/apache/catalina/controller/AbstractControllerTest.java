package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestHeaders;
import org.apache.coyote.http11.RequestLine;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    private final Controller getOnlyController = new AbstractController() {
        @Override
        protected void doGet(final HttpRequest request, final HttpResponse response) {
            response.setBody("text/plain", "get");
        }
    };

    private final Controller postOnlyController = new AbstractController() {
        @Override
        protected void doPost(final HttpRequest request, final HttpResponse response) {
            response.setBody("text/plain", "post");
        }
    };

    @Test
    void GET_요청은_doGet으로_처리한다() throws Exception {
        final HttpResponse response = new HttpResponse();

        getOnlyController.service(request("GET / HTTP/1.1"), response);

        assertThat(toString(response)).startsWith("HTTP/1.1 200 OK ").endsWith("get");
    }

    @Test
    void POST_요청은_doPost로_처리한다() throws Exception {
        final HttpResponse response = new HttpResponse();

        postOnlyController.service(request("POST / HTTP/1.1"), response);

        assertThat(toString(response)).startsWith("HTTP/1.1 200 OK ").endsWith("post");
    }

    @Test
    void 구현하지_않은_메서드로_요청하면_405를_응답한다() throws Exception {
        final HttpResponse response = new HttpResponse();

        getOnlyController.service(request("POST / HTTP/1.1"), response);

        assertThat(toString(response)).startsWith("HTTP/1.1 405 Method Not Allowed ");
    }

    private HttpRequest request(final String requestLine) {
        return HttpRequest.of(RequestLine.from(requestLine), RequestHeaders.from(List.of()), "");
    }

    private String toString(final HttpResponse response) {
        return new String(response.getBytes(), UTF_8);
    }
}
