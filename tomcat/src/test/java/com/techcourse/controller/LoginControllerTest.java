package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.apache.coyote.http11.Http11Header;
import org.apache.coyote.http11.Http11ResourceFinder;
import org.apache.coyote.http11.request.Http11Method;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.Http11StatusCode;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class LoginControllerTest {

    private final LoginController loginController = Mockito.spy(new LoginController());

    @Test
    @DisplayName("로그인 페이지 조회한다.")
    void doGet() throws IOException {
        HttpRequest request = makeRequest(Http11Method.GET, new LinkedHashMap<>());
        HttpResponse response = new HttpResponse();

        loginController.service(request, response);

        String responseAsString = new String(response.toBytes());
        Http11ResourceFinder resourceFinder = new Http11ResourceFinder();
        Path path = resourceFinder.find("/login.html");
        String expected = new String(Files.readAllBytes(path));

        assertThat(responseAsString).contains(expected);
    }

    private HttpRequest makeRequest(Http11Method method, LinkedHashMap<String, String> body) {
        return new HttpRequest(
                method,
                "/login.html",
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                body
        );
    }

    @Test
    @DisplayName("로그인 상태에서 로그인 페이지 조회시 index 페이지로 리다이렉트 한다.")
    void doGetWhenLogin() throws IOException {
        HttpRequest request = makeRequest(Http11Method.GET, new LinkedHashMap<>());
        HttpResponse response = new HttpResponse();

        Mockito.when(loginController.isLogin(request)).thenReturn(true);

        loginController.service(request, response);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(Http11StatusCode.FOUND),
                () -> assertThat(response.findHeader("Location")).hasValue(new Http11Header("Location", "/index.html"))
        );
    }

    @Test
    @DisplayName("로그인 정보 맞으면 로그인 성공 후 index 페이지로 리다이렉트")
    void doPostSuccess() throws IOException {
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.putLast("account", "gugu");
        body.putLast("password", "password");

        HttpRequest request = makeRequest(Http11Method.POST, body);
        HttpResponse response = new HttpResponse();

        loginController.service(request, response);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(Http11StatusCode.FOUND),
                () -> assertThat(response.findHeader("Location")).hasValue(new Http11Header("Location", "/index.html"))
        );
    }

    @Test
    @DisplayName("로그인 정보 틀리면 로그인 실패 후 401 페이지로 리다이렉트")
    void doPostFail() throws IOException {
        LinkedHashMap<String, String> body = new LinkedHashMap<>();
        body.putLast("account", "gugu");
        body.putLast("password", "wrong");

        HttpRequest request = makeRequest(Http11Method.POST, body);
        HttpResponse response = new HttpResponse();

        loginController.service(request, response);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(Http11StatusCode.FOUND),
                () -> assertThat(response.findHeader("Location")).hasValue(new Http11Header("Location", "/401.html"))
        );
    }
}
