package org.apache.coyote.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.message.common.HttpBody;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestLine;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    private final HomeController homeController = new HomeController();

    @DisplayName("HomeController는 GET 요청에 대해 올바른 응답을 반환한다.")
    @Test
    void doGet() {
        // given
        HttpRequest request = new HttpRequest(new HttpRequestLine("GET / HTTP/1.1"), new HttpHeaders(),
                new HttpBody(""));
        HttpResponse response = new HttpResponse();

        // when
        homeController.doGet(request, response);

        // then
        assertAll(
                () -> assertThat(response.toString()).contains("HTTP/1.1 200 OK"),
                () -> assertThat(response.toString()).contains("Content-Type: text/html;charset=utf-8"),
                () -> assertThat(response.toString()).contains("Hello world!")
        );
    }
}
