package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.coyote.http11.message.parser.HttpRequestParser;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @DisplayName("GET /register -> 200 회원가입 페이지")
    @Test
    void doGet() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
        HttpResponse httpResponse = new HttpResponse();

        // when
        new RegisterController().doGet(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.convertMessage()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "/register.html"
        );
    }

    @DisplayName("POST /register")
    @Nested
    class doPost {

        @DisplayName("회원가입에 성공하면 index 페이지로 이동한다.")
        @Test
        void registerSuccess() throws IOException {
            String request = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 80 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=jojo&password=1234&email=jojo@email.com ");

            HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
            HttpResponse httpResponse = new HttpResponse();

            // when
            new RegisterController().doPost(httpRequest, httpResponse);

            // then
            assertThat(httpResponse.convertMessage()).contains(
                    "HTTP/1.1 302 Found",
                    "Location: http://localhost:8080/index.html",
                    "Content-Type: text/html;charset=utf-8",
                    "Content-Length: 0"
            );
        }

        @DisplayName("계정이 중복되면 예외가 발생한다.")
        @Test
        void registerFailure() throws IOException {
            String request = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: 80 ",
                    "Content-Type: application/x-www-form-urlencoded ",
                    "Accept: */* ",
                    "",
                    "account=gugu&password=password&email=hkkang@woowahan.com ");

            HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
            HttpResponse httpResponse = new HttpResponse();

            // when & then
            assertThatThrownBy(() -> new RegisterController().doPost(httpRequest, httpResponse))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
