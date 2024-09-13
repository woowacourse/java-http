package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponse.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResourceControllerTest {

    @Test
    @DisplayName("메인 페이지를 조회한다.")
    void viewMainPage() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        Builder responseBuilder = HttpResponse.builder();
        HttpRequest request = HttpRequest.parse(List.of(
                "GET /index.html HTTP/1.1"
        ));

        // when
        requestMapping.getController(request)
                .service(request, responseBuilder);
        HttpResponse response = responseBuilder.build();

        // then
        assertThat(response.toMessage())
                .contains("HTTP/1.1 200 OK".getBytes())
                .contains("Content-Type: text/html".getBytes())
                .contains("<title>대시보드</title>".getBytes());
    }
}
