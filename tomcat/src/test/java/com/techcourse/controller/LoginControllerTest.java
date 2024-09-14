package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController sut = new LoginController();

    @Test
    @DisplayName("로그인 성공: index로 리다이렉트")
    void login() throws Exception {
        // given
        var account = "gugu";
        var password = "password";
        var requestBody = "account=%s&password=%s".formatted(account, password);
        var request = httpRequest(String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody));
        var response = new HttpResponse();

        // when
        sut.service(request, response);

        // then
        assertThat(response.asString())
                .contains("HTTP/1.1 302 FOUND ")
                .contains("Location: /index.html ");
    }

    @Test
    @DisplayName("로그인 실패: 401로 리다이렉트")
    void login401() throws Exception {
        // given
        var account = "nobody";
        var password = "wrong password";
        var requestBody = "account=%s&password=%s".formatted(account, password);
        var request = httpRequest(String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody));
        var response = new HttpResponse();

        // when
        sut.service(request, response);

        // then
        assertThat(response.asString())
                .contains("HTTP/1.1 302 FOUND ")
                .contains("Location: /401.html ");
    }

    private HttpRequest httpRequest(final String input) throws IOException {
        byte[] inputBytes = input.getBytes();
        var inputStream = new ByteArrayInputStream(inputBytes);
        return new HttpRequest(inputStream);
    }
}
