package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceControllerTest {

    private final StaticResourceController controller = new StaticResourceController();

    @Test
    void HTML_정적_파일을_응답한다() throws Exception {
        final HttpRequest request = HttpRequest.from("GET /index.html HTTP/1.1");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/html;charset=utf-8\r\n");
    }

    @Test
    void CSS_정적_파일을_응답한다() throws Exception {
        final HttpRequest request = HttpRequest.from("GET /css/styles.css HTTP/1.1");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/css;charset=utf-8\r\n");
    }

    @Test
    void 존재하지_않는_정적_파일은_404를_응답한다() throws Exception {
        final HttpRequest request = HttpRequest.from("GET /missing.html HTTP/1.1");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 404 Not Found\r\n");
    }
}
