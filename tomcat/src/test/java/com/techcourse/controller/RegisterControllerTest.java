package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httprequest.HttpRequestConvertor;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @DisplayName("회원가입이 성공하면 /index.html 페이지로 이동한다")
    @Test
    void register() throws IOException {
        String body = "account=f&password=12&email=bito@wooea.net";
        final String register = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
        InputStream inputStream = new ByteArrayInputStream(register.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequestConvertor.convertHttpRequest(bufferedReader);

        RegisterController registerController = new RegisterController();
        HttpResponse httpResponse = registerController.doPost(httpRequest);

        assertThat(httpResponse.getBytes())
                .contains("HTTP/1.1 302 Found ".getBytes())
                .contains("Location: /index".getBytes());
    }

    @DisplayName("일부 항목을 입력하지 않고 회원가입을 시도하면 /register 페이지로 이동한다")
    @Test
    void notExistUserInput() throws IOException {
        String body = "account=f&password=&email=bito@wooea.net";
        final String register = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
        InputStream inputStream = new ByteArrayInputStream(register.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequestConvertor.convertHttpRequest(bufferedReader);

        RegisterController registerController = new RegisterController();
        HttpResponse httpResponse = registerController.doPost(httpRequest);

        assertThat(httpResponse.getBytes())
                .contains("HTTP/1.1 302 Found ".getBytes())
                .contains("Location: /register".getBytes());
    }

    @DisplayName("이미 회원가입이 되어있는 account로 회원가입을 시도할 경우 /register 페이지로 이동한다")
    @Test
    void existUserRegister() throws IOException {
        String body = "account=gugu&password=1&email=bito@wooea.net";
        final String register = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
        InputStream inputStream = new ByteArrayInputStream(register.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequestConvertor.convertHttpRequest(bufferedReader);

        RegisterController registerController = new RegisterController();
        HttpResponse httpResponse = registerController.doPost(httpRequest);

        assertThat(httpResponse.getBytes())
                .contains("HTTP/1.1 302 Found ".getBytes())
                .contains("Location: /register".getBytes());
    }

    @DisplayName("GET으로 /register를 접근하면 static/register.html을 띄운다")
    @Test
    void doGet() throws IOException {
        final String login = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        InputStream inputStream = new ByteArrayInputStream(login.getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequest httpRequest = HttpRequestConvertor.convertHttpRequest(bufferedReader);

        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        RegisterController registerController = new RegisterController();
        HttpResponse httpResponse = registerController.doGet(httpRequest);

        assertThat(httpResponse.getBytes())
                .contains("HTTP/1.1 200 OK ".getBytes())
                .contains("Content-Type: text/html;charset=utf-8 ".getBytes())
                .contains(("Content-Length: " + body.getBytes().length + " ").getBytes())
                .contains(body.getBytes());
    }
}
