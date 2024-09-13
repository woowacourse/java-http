package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponse.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    @DisplayName("로그인 페이지를 조회한다.")
    void viewLoginPage() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        Builder responseBuilder = HttpResponse.builder();
        HttpRequest request = HttpRequest.parse(List.of(
                "GET /login HTTP/1.1"
        ));

        // when
        requestMapping.getController(request)
                .service(request, responseBuilder);
        HttpResponse response = responseBuilder.build();

        // then
        assertThat(response.toMessage())
                .containsSequence("HTTP/1.1 200 OK".getBytes())
                .containsSequence("Content-Type: text/html".getBytes())
                .containsSequence("<title>로그인</title>".getBytes());
    }

    @Test
    @DisplayName("로그인을 하면 홈 화면으로 이동한다.")
    void loginAndRedirect() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        Builder responseBuilder = HttpResponse.builder();
        HttpRequest request = HttpRequest.parse(List.of(
                "POST /login HTTP/1.1",
                "",
                "account=gugu&password=password"
        ));

        // when
        requestMapping.getController(request)
                .service(request, responseBuilder);
        HttpResponse response = responseBuilder.build();

        // then
        assertThat(response.toMessage())
                .containsSequence("HTTP/1.1 302 Found".getBytes())
                .containsSequence("Location: /index.html".getBytes())
                .containsSequence("Set-Cookie: ".getBytes());
    }

    @Test
    @DisplayName("존재하지 않는 사용자로 로그인을 하면 401 페이지로 이동한다.")
    void loginWithNonAccountAndRedirect() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        Builder responseBuilder = HttpResponse.builder();
        HttpRequest request = HttpRequest.parse(List.of(
                "POST /login HTTP/1.1",
                "",
                "account=seyang&password=password"
        ));

        // when
        requestMapping.getController(request)
                .service(request, responseBuilder);
        HttpResponse response = responseBuilder.build();

        // then
        System.out.println("response = " + response);
        assertThat(response.toMessage())
                .containsSequence("HTTP/1.1 302 Found".getBytes())
                .containsSequence("Location: /401.html".getBytes());
    }

    @Test
    @DisplayName("잘못된 비밀번호로 로그인을 하면 401 페이지로 이동한다.")
    void loginWithDifferentPasswordAndRedirect() {
        // given
        RequestMapping requestMapping = new RequestMapping();
        Builder responseBuilder = HttpResponse.builder();
        HttpRequest request = HttpRequest.parse(List.of(
                "POST /login HTTP/1.1",
                "",
                "account=gugu&password=pw"
        ));

        // when
        requestMapping.getController(request)
                .service(request, responseBuilder);
        HttpResponse response = responseBuilder.build();

        // then
        assertThat(response.toMessage())
                .containsSequence("HTTP/1.1 302 Found".getBytes())
                .containsSequence("Location: /401.html".getBytes());
    }
}
