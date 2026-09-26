package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class RootControllerTest {

    @Test
    void 루트_GET_요청에_Hello_world를_응답한다() throws Exception {
        final HttpRequest request = HttpRequest.from("GET / HTTP/1.1");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);
        final RootController controller = new RootController();

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .isEqualTo(String.join("\r\n",
                        "HTTP/1.1 200 OK",
                        "Content-Type: text/html;charset=utf-8",
                        "Content-Length: 12",
                        "",
                        "Hello world!"));
    }

    @Test
    void 루트_POST_요청은_405를_응답한다() throws Exception {
        final HttpRequest request = HttpRequest.from("POST / HTTP/1.1");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);
        final RootController controller = new RootController();

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 405 Method Not Allowed\r\n")
                .contains("Allow: GET\r\n");
    }
}
