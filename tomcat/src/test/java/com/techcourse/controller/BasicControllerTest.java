package com.techcourse.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BasicControllerTest {

    @Test
    @DisplayName("/index.html 요청 시 해당하는 응답을 반환한다.")
    void doGet() throws Exception {
        // given
        BasicController controller = new BasicController();
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest request = HttpRequest.of(httpRequest);
        HttpResponse response = new HttpResponse();

        // when
        controller.doGet(request, response);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 12 ",
                "Content-Type: text/html;charset=utf-8 ",
                "",
                "Hello world!");

        assertThat(response.toResponse()).contains(expected);
    }
}
