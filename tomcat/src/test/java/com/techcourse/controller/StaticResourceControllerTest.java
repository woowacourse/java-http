package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceControllerTest {

    private final StaticResourceController controller = new StaticResourceController();

    @Test
    void 클래스패스의_정적_리소스를_응답한다() throws Exception {
        // given
        HttpRequest request = new HttpRequest("GET /index.html HTTP/1.1\r\n\r\n");
        HttpResponse response = new HttpResponse();
        String expectedBody = readResource("static/index.html");

        // when
        controller.service(request, response);

        // then
        String message = response.toResponse();
        assertThat(message).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(message).contains("Content-Type: text/html;charset=utf-8\r\n");
        assertThat(message).contains(
                "Content-Length: " + expectedBody.getBytes(StandardCharsets.UTF_8).length + "\r\n"
        );
        assertThat(responseBody(message)).isEqualTo(expectedBody);
    }

    @Test
    void 존재하지_않는_리소스는_404로_응답한다() throws Exception {
        // given
        HttpRequest request = new HttpRequest("GET /missing.html HTTP/1.1\r\n\r\n");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(response.toResponse()).startsWith("HTTP/1.1 404 Not Found\r\n");
    }

    private String readResource(String path) throws IOException {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path)) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private static String responseBody(String response) {
        return response.substring(response.indexOf("\r\n\r\n") + 4);
    }
}
