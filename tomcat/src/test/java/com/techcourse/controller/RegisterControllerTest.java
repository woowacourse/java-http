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

    @Test
    @DisplayName("회원가입을 한다.")
    void register() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        HttpRequest request = HttpRequest.parse(List.of(
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=seyang&email=seyang%40woowa.com&password=pw"
        ));
        Builder responseBuilder = HttpResponse.builder();

        // when
        requestMapping.getController(request)
                .service(request, responseBuilder);
        HttpResponse response = responseBuilder.build();

        // then
        assertThat(response.toMessage())
                .contains("HTTP/1.1 302 Found".getBytes())
                .contains("Location: /index.html".getBytes());
    }

    @Test
    @DisplayName("일부 정보 없이 회원가입 할 경우 400을 반환한다.")
    void registerWithoutInformation() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        HttpRequest request = HttpRequest.parse(List.of(
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=seyang&password=pw"
        ));
        Builder responseBuilder = HttpResponse.builder();

        // when
        requestMapping.getController(request)
                .service(request, responseBuilder);
        HttpResponse response = responseBuilder.build();

        // then
        assertThat(response.toMessage())
                .contains("HTTP/1.1 400 Bad Request".getBytes());
    }
}
