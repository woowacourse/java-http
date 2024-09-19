package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FrontControllerTest {
    private FrontController frontController;

    @BeforeEach
    void setUp() {
        frontController = FrontController.getInstance();
    }

    @DisplayName("존재하지 않는 리소스에 접근하면 404 응답을 반환한다.")
    @Test
    void wrongURL() throws Exception {
        // given
        RequestLine requestLine = RequestLine.from("GET /unknown.html HTTP/1.1 ");
        HttpRequest httpRequest = new HttpRequest(requestLine, createHeaders(), new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        frontController.service(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 404 NOT FOUND \r\n";
        String expectedContentType = "Content-Type: text/html;charset=utf-8 \r\n";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedContentType
        );
    }

    @DisplayName("잘못된 요청을 하면 500 응답을 반환한다.")
    @Test
    void wrongMethod() throws Exception {
        // given
        RequestLine requestLine = RequestLine.from("POST /index.html HTTP/1.1 ");
        HttpRequest httpRequest = new HttpRequest(requestLine, createHeaders(), new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        frontController.service(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 500 INTERNAL SERVER ERROR \r\n";
        String expectedContentType = "Content-Type: text/html;charset=utf-8 \r\n";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedContentType
        );
    }

    private HttpHeaders createHeaders() {
        return HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        ));
    }
}
