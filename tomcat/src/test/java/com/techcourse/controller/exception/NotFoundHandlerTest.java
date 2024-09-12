package com.techcourse.controller.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NotFoundHandlerTest {

    @Test
    @DisplayName("모든 요청 처리: 404 응답 반환")
    void handle() throws Exception {
        final RequestLine requestLine = new RequestLine("GET", "/index.html", "HTTP/1.1");
        final HttpRequest httpRequest = new HttpRequest(requestLine, null, null);
        final HttpResponse actual = HttpResponse.builder().okBuild();

        NotFoundHandler.getInstance().service(httpRequest, actual);

        final HttpResponse expected = HttpResponse.builder().notFoundBuild();
        assertThat(actual).isEqualTo(expected);
    }
}
