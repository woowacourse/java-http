package com.techcourse.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    @Test
    @DisplayName("GET /login 요청 시 해당하는 응답을 반환한다.")
    void doGet() throws Exception {
        // given
        LoginController loginController = new LoginController();
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest request = HttpRequest.of(httpRequest);
        HttpResponse response = new HttpResponse();

        // when
        loginController.doGet(request, response);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 3797 ",
                "Content-Type: text/html;charset=utf-8 ",
                "");

        assertThat(response.toResponse()).contains(expected);
    }

    @Test
    @DisplayName("POST /login 요청 시 해당하는 응답을 반환한다.")
    void doPost() throws Exception {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        LoginController loginController = new LoginController();
        HttpRequest request = HttpRequest.of(httpRequest);
        request.setBody("account=gugu&password=password");
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);

        // then
        var expected = List.of(
                "HTTP/1.1 302 Found ",
                "Content-Length: 3797 ",
                "Content-Type: text/html;charset=utf-8 ",
                "");

        assertThat(response.toResponse()).contains(expected);
    }

    @Test
    @DisplayName("POST /login 잘못된 로그인 정보로 요청 시 해당하는 응답을 반환한다.")
    void doPostException() throws Exception {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        LoginController loginController = new LoginController();
        HttpRequest request = HttpRequest.of(httpRequest);
        request.setBody("account=wrong&password=wrong");
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Length: 2426 ",
                "Content-Type: text/html;charset=utf-8 ",
                "");

        assertThat(response.toResponse()).contains(expected);
    }

    @Test
    @DisplayName("POST /login 요청 시 이미 로그인 되어있다면 해당하는 응답을 반환한다.")
    void doPostAlreadyLoggedIn() throws Exception {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=1234",
                "");
        LoginController loginController = new LoginController();
        HttpRequest request = HttpRequest.of(httpRequest);
        request.setBody("account=gugu&password=password");
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);

        // then
        var expected = List.of(
                "HTTP/1.1 302 Found ",
                "Content-Length: 3797 ",
                "Content-Type: text/html;charset=utf-8 ",
                "");

        assertThat(response.toResponse()).contains(expected);
    }
}
