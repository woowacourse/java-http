package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @DisplayName("회원가입 GET 요청이면 /register.html 을 응답한다.")
    @Test
    void doGet() throws IOException {
        RegisterController registerController = new RegisterController();
        String getLogin = String.join(
                "\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                ""
        );
        InputStream inputStream = new ByteArrayInputStream(getLogin.getBytes());
        HttpRequest httpRequest = Http11Request.from(inputStream);
        HttpResponse httpResponse = Http11Response.create();

        registerController.doGet(httpRequest, httpResponse);

        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        assertThat(httpResponse.toString()).isEqualTo(
                String.join(
                        "\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Length: 4319 ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "",
                        new String(Files.readAllBytes(new File(resource.getFile()).toPath()))
                )
        );
    }

    @DisplayName("회원가입 성공후 /index.html 파일로 리다이렉트 된다.")
    @Test
    void doPost() throws IOException {
        RegisterController registerController = new RegisterController();
        String register = String.join(
                "\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 48 ",
                "",
                "account=aa&password=password&email=abc@naver.com"
        );
        InputStream inputStream = new ByteArrayInputStream(register.getBytes());
        HttpRequest httpRequest = Http11Request.from(inputStream);
        HttpResponse httpResponse = Http11Response.create();

        registerController.doPost(httpRequest, httpResponse);

        assertThat(httpResponse.toString()).isEqualTo(
                String.join(
                        "\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /index.html ",
                        ""
                )
        );
    }

    @DisplayName("중복된 account로 회원가입할 수 없다.")
    @Test
    void doPostDuplicate() throws IOException {
        RegisterController registerController = new RegisterController();
        String register = String.join(
                "\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 50 ",
                "",
                "account=gugu&password=password&email=abc@naver.com"
        );
        InputStream inputStream = new ByteArrayInputStream(register.getBytes());
        HttpRequest httpRequest = Http11Request.from(inputStream);
        HttpResponse httpResponse = Http11Response.create();

        assertThatThrownBy(() -> registerController.doPost(httpRequest, httpResponse))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
