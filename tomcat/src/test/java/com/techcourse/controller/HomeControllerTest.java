package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponse.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @Test
    @DisplayName("안녕세상 페이지를 조회한다.")
    void viewHomePage() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        HttpRequest request = HttpRequest.parse(List.of(
                "GET / HTTP/1.1"
        ));
        Builder responseBuilder = HttpResponse.builder();

        // when
        requestMapping.getController(request)
                .service(request, responseBuilder);
        HttpResponse response = responseBuilder.build();

        // then
        assertThat(response.toMessage())
                .contains("HTTP/1.1 200 OK".getBytes())
                .contains("Content-Type: text/html".getBytes())
                .contains("Content-Length: 12".getBytes())
                .contains("Hello world!".getBytes());
    }
}
