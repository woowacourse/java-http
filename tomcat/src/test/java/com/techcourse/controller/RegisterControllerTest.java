package com.techcourse.controller;

import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    @Test
    @DisplayName("GET /register 요청 시 해당하는 응답을 반환한다.")
    void doGet() throws Exception {
        // given
        RegisterController registerController = new RegisterController();
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest request = HttpRequest.of(httpRequest);
        HttpResponse response = new HttpResponse();

        // when
        registerController.doGet(request, response);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 4319 ",
                "Content-Type: text/html;charset=utf-8 ",
                "");

        assertThat(response.toResponse()).contains(expected);
    }

    @Test
    @DisplayName("POST /register 요청 시 해당하는 응답을 반환한다.")
    void doPost() throws Exception {
        // given
        RegisterController registerController = new RegisterController();
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        HttpRequest request = HttpRequest.of(httpRequest);
        request.setBody("account=test&password=password&email=test@email.com");
        HttpResponse response = new HttpResponse();

        // when
        registerController.doPost(request, response);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Content-Length: 4319 ",
                "Content-Type: text/html;charset=utf-8 ",
                "Location: /index.html");

        assertThat(response.toResponse()).contains(expected);
    }
}
