package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    private final FrontController frontController = new FrontController();

    @Test
    @DisplayName("요청에 매핑 되어 있는 컨트롤러를 반환한다.")
    void requestMappedController() throws Exception {
        // given
        final String requestString = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final HttpRequest request = HttpRequest.from(reader);
        final HttpResponse response = new HttpResponse();

        // when
        frontController.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(response.getHeaders().get(0).getHeaderAsString()).isEqualTo(
                        "Content-Type: text/html;charset=utf-8 "),
                () -> assertThat(response.getHeaders().get(1).getHeaderAsString()).isEqualTo("Content-Length: 3447 ")
        );
    }

    @Test
    @DisplayName("요청에 매핑 되어있지 않으면 정적 파일 컨트롤러를 반환한다.")
    void resourceController() throws Exception {
        // given
        final String requestString = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        final InputStream inputStream = new ByteArrayInputStream(requestString.getBytes());
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        final HttpRequest request = HttpRequest.from(reader);
        final HttpResponse response = new HttpResponse();

        // when
        frontController.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(response.getHeaders().get(0).getHeaderAsString()).isEqualTo(
                        "Content-Type: text/html;charset=utf-8 "),
                () -> assertThat(response.getHeaders().get(1).getHeaderAsString()).isEqualTo("Content-Length: 5564 ")
        );
    }
}