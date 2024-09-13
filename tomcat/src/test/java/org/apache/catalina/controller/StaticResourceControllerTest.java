package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.request.RequestLine;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.Test;

class StaticResourceControllerTest {

    @Test
    void serviceTest() throws Exception {
        // given
        StaticResourceController controller = StaticResourceController.INSTANCE;
        HttpRequest request = new HttpRequest(new RequestLine("GET", "/hello.html", "HTTP/1.1"),
                                              null, null, null, null);
        HttpResponse response = HttpResponse.create();

        // when
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(request.getUri()).isEqualTo("/hello.html"),
                () -> assertThat(response.getHeader("Content-Type")).isEqualTo("text/html;charset=utf-8")
        );
    }
}
