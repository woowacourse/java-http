package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AbstractControllerTest {

    @Test
    void GET_요청은_doGet으로_분기한다() throws Exception {
        // given
        HttpRequest request = new HttpRequest("GET / HTTP/1.1\r\n\r\n");
        HttpResponse response = new HttpResponse();
        AbstractController controller = new AbstractController() {
            @Override
            protected void doGet(HttpRequest request, HttpResponse response) {
                response.ok("text/plain;charset=utf-8", "GET");
            }
        };

        // when
        controller.service(request, response);

        // then
        assertThat(response.toResponse()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(response.toResponse()).endsWith("\r\n\r\nGET");
    }

    @Test
    void POST_요청은_doPost로_분기한다() throws Exception {
        // given
        HttpRequest request = new HttpRequest("POST / HTTP/1.1\r\n\r\n");
        HttpResponse response = new HttpResponse();
        AbstractController controller = new AbstractController() {
            @Override
            protected void doPost(HttpRequest request, HttpResponse response) {
                response.ok("text/plain;charset=utf-8", "POST");
            }
        };

        // when
        controller.service(request, response);

        // then
        assertThat(response.toResponse()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(response.toResponse()).endsWith("\r\n\r\nPOST");
    }

    @Test
    void 구현하지_않은_HTTP_메서드는_405로_응답한다() throws Exception {
        // given
        HttpRequest request = new HttpRequest("GET / HTTP/1.1\r\n\r\n");
        HttpResponse response = new HttpResponse();
        AbstractController controller = new AbstractController() {
        };

        // when
        controller.service(request, response);

        // then
        assertThat(response.toResponse()).startsWith("HTTP/1.1 405 Method Not Allowed\r\n");
    }
}
