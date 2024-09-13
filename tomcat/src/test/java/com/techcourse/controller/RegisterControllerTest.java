package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private RegisterController registerController;
    private HttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        registerController = new RegisterController();
        httpResponse = new HttpResponse();
    }

    @DisplayName("GET /register 요청을 하면 회원가입 페이지를 응답한다.")
    @Test
    void doGet() throws IOException {
        String request = "GET /register HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        registerController.doGet(httpRequest, httpResponse);
        String actual = new String(httpResponse.buildResponse());
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 4319 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("POST /register 요청을 하면 회원가입 후 인덱스 페이지로 리다이렉트한다.")
    @Test
    void doPost() throws IOException {
        String request = "POST /register HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Content-Length: 50 \r\n\r\n" +
                "account=atto&password=attoatto&email=atto@atto.com";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        registerController.doPost(httpRequest, httpResponse);
        String actual = new String(httpResponse.buildResponse());
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("정보 없이 POST /register 요청을 하면 회원가입 페이지로 리다이렉트한다.")
    @Test
    void doPost_whenNotBody() throws IOException {
        String request = "POST /register HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Content-Length: 24 \r\n\r\n" +
                "account=&password=&email=";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        registerController.doPost(httpRequest, httpResponse);
        String actual = new String(httpResponse.buildResponse());
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /register.html \r\n" +
                "\r\n";

        assertThat(actual).isEqualTo(expected);
    }
}
