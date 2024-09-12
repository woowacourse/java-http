package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestTestSupport;

class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @Test
    @DisplayName("쿠키가 없고, /login 경로로 GET 요청하면 login 페이지를 반환한다.")
    void getRequestWithoutCookie() throws Exception {
        HttpRequest request = HttpRequestTestSupport.loginGet();
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();

        loginController.service(request, builder);
        HttpResponse response = builder.build();

        String expectedResponseBody = HttpRequestTestSupport.loadResourceContent("/login.html");
        assertEquals(expectedResponseBody, response.getResponseBody());
    }

    @Test
    @DisplayName("쿠키가 있고, /login 경로로 GET 요청하면 index 페이지를 Location으로 설정한다.")
    void getRequestWithCookie() throws Exception {
        HttpRequest request = HttpRequestTestSupport.loginGetWithCookie();
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();

        loginController.service(request, builder);
        HttpResponse response = builder.build();

        assertEquals(response.getHeaders().get("Location"), "/index.html");
    }

    @Test
    @DisplayName("가입이 되어있고, /login 경로로 POST 요청하면 알맞은 헤더를 응답한다.")
    void postRequestSavedUser() throws Exception {
        HttpRequest request = HttpRequestTestSupport.loginPost();
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();

        loginController.service(request, builder);
        HttpResponse response = builder.build();

        assertAll(
                () -> assertEquals(response.getHeaders().get("Location"), "/index.html"),
                () -> assertThat(response.getHeaders().get("Set-Cookie")).isNotEmpty()
        );
    }

    @Test
    @DisplayName("가입이 되어있지 않고, /login 경로로 POST 요청하면 알맞은 헤더를 응답한다.")
    void postRequestNotSavedUser() throws Exception {
        HttpRequest request = HttpRequestTestSupport.loginPostNotRegistered();
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();

        loginController.service(request, builder);
        HttpResponse response = builder.build();

        assertEquals(response.getHeaders().get("Location"), "/401.html");
    }
}
