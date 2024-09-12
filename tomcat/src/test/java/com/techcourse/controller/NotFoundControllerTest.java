package com.techcourse.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotFoundControllerTest {

    @Test
    @DisplayName("Not Found 에러로 해당 컨트롤러 호출 시 해당하는 응답을 반환한다.")
    void doGet() throws Exception {
        // given
        NotFoundController controller = new NotFoundController();
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
                "HTTP/1.1 404 Not Found ",
                "Content-Length: 2426 ",
                "Content-Type: text/html;charset=utf-8 ");

        assertThat(response.toResponse()).contains(expected);
    }

}
