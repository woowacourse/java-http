package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestFixture;

class FrontControllerTest {

    private final FrontController frontController = new FrontController();

    @Test
    @DisplayName("요청에 매핑 되어 있는 컨트롤러를 반환한다.")
    void requestMappedController() throws Exception {
        // given
        final HttpRequest request = HttpRequestFixture.loginGet();
        final HttpResponse response = new HttpResponse();

        // when
        frontController.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(response.getHeaders().getFirst().getHeaderAsString()).isEqualTo(
                        "Content-Type: text/html;charset=utf-8 "),
                () -> assertThat(response.getHeaders().getLast().getHeaderAsString()).isEqualTo("Content-Length: 3447 ")
        );
    }

    @Test
    @DisplayName("요청에 매핑 되어있지 않으면 정적 파일 컨트롤러를 반환한다.")
    void resourceController() throws Exception {
        // given
        final HttpRequest request = HttpRequestFixture.indexPageGet();
        final HttpResponse response = new HttpResponse();

        // when
        frontController.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getStatusLine()).isEqualTo("HTTP/1.1 200 OK "),
                () -> assertThat(response.getHeaders().getFirst().getHeaderAsString()).isEqualTo(
                        "Content-Type: text/html;charset=utf-8 "),
                () -> assertThat(response.getHeaders().getLast().getHeaderAsString()).isEqualTo("Content-Length: 5564 ")
        );
    }
}
