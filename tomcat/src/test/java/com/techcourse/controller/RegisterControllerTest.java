package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpResponse.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    @DisplayName("회원가입 페이지를 조회한다.")
    void viewRegisterPage() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        HttpRequest request = HttpRequest.parse(List.of(
                "GET /register HTTP/1.1"
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
                .contains("<title>회원가입</title>".getBytes());
    }
}
