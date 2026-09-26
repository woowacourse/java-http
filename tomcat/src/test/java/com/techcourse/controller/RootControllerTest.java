package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static support.HttpRequestFixtures.httpRequest;
import static support.HttpResponseFixtures.responseText;

class RootControllerTest {

    private final RootController controller = new RootController();

    @Test
    void GET_요청에_Hello_world를_응답한다() throws Exception {
        // given
        HttpRequest request = httpRequest("GET / HTTP/1.1\r\n\r\n");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        String message = responseText(response);
        assertThat(message).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(message).contains("Content-Type: text/html;charset=utf-8\r\n");
        assertThat(message).contains("Content-Length: 12\r\n");
        assertThat(message).endsWith("\r\n\r\nHello world!");
    }

    @Test
    void POST_요청은_405로_응답한다() throws Exception {
        // given
        HttpRequest request = httpRequest("POST / HTTP/1.1\r\n\r\n");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(responseText(response)).startsWith("HTTP/1.1 405 Method Not Allowed\r\n");
    }
}
