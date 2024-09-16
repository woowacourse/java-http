package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    @DisplayName("로그인 페이지로 이동한다.")
    void login() {
        HttpRequest request = new HttpRequest(
                new HttpRequestLine("GET /login HTTP/1.1"),
                new HttpRequestHeader(
                        List.of("Host: localhost:8080", "Connection: keep-alive", "Content-Length: 80",
                                "Content-Type: application/x-www-form-urlencoded", "Accept: */*")),
                new HttpRequestBody("")
        );
        HttpResponse response = HttpResponse.defaultResponse();
        Controller controller = new LoginController();
        controller.service(request, response);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(302);
        assertThat(response.getResponseHeader().get("Location")).isEqualTo("/login.html");
    }

    @Test
    @DisplayName("로그인을 시도하면 Set-Cookie 헤더가 추가된다.")
    void loginSetCookie() {
        HttpRequest request = new HttpRequest(
                new HttpRequestLine("POST /login HTTP/1.1"),
                new HttpRequestHeader(
                        List.of("Host: localhost:8080", "Connection: keep-alive", "Content-Length: 80",
                                "Content-Type: application/x-www-form-urlencoded", "Accept: */*")),
                new HttpRequestBody("account=gugu&password=password")
        );
        HttpResponse response = HttpResponse.defaultResponse();
        Controller controller = new LoginController();
        controller.service(request, response);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(302);
        assertThat(response.getResponseHeader().keySet()).contains("Set-Cookie");
    }

    @Test
    @DisplayName("이미 로그인이 되어 있으면 index.html로 이동한다.")
    void loginRedirectIndex() {
        HttpRequest postRequest = new HttpRequest(
                new HttpRequestLine("POST /login HTTP/1.1"),
                new HttpRequestHeader(
                        List.of("Host: localhost:8080", "Connection: keep-alive", "Content-Length: 80",
                                "Content-Type: application/x-www-form-urlencoded", "Accept: */*")),
                new HttpRequestBody("account=gugu&password=password")
        );
        HttpResponse postResponse = HttpResponse.defaultResponse();
        Controller getController = new LoginController();
        getController.service(postRequest, postResponse);
        String cookie = postResponse.getResponseHeader().get("Set-Cookie").split("=")[1];

        HttpRequest getRequest = new HttpRequest(
                new HttpRequestLine("GET /login HTTP/1.1"),
                new HttpRequestHeader(
                        List.of("Host: localhost:8080", "Connection: keep-alive", "Content-Length: 80",
                                "Content-Type: application/x-www-form-urlencoded", "Accept: */*",
                                "Cookie: JSESSIONID=" + cookie)),
                new HttpRequestBody("")
        );
        HttpResponse getResponse = HttpResponse.defaultResponse();
        Controller postController = new LoginController();
        postController.service(getRequest, getResponse);

        assertThat(getResponse.getStatusLine().getStatusCode()).isEqualTo(302);
        assertThat(getResponse.getResponseHeader().get("Location")).contains("/index.html");
    }
}
