package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class RegisterControllerTest {

    @Test
    @DisplayName("회원가입 요청을 보낼 수 있다.")
    void doPost() throws IOException {
        String register =
                "POST /register HTTP/1.1" + "\r\n" +
                "Host: localhost:8080" + "\r\n" +
                "Connection: keep-alive" + "\r\n" +
                "Content-Length: 51" + "\r\n" +
                "\r\n" +
                "account=gugu&password=password&email=abcd@naver.com";

        Http11Request request = Http11Request.from(new ByteArrayInputStream(register.getBytes()));
        Http11Response response = Http11Response.ok();

        LoginController loginController = new LoginController();
        loginController.doPost(request, response);

        assertThat(new String(response.getBytes()))
                .contains("302 Found")
                .contains("Set-Cookie");
    }

    @Test
    @DisplayName("회원가입 페이지 요청을 보낼 수 있다.")
    void doGet() throws IOException {
        String register =
                "GET /register HTTP/1.1" + "\r\n" +
                "Host: localhost:8080" + "\r\n" +
                "Connection: keep-alive" + "\r\n" + "\r\n";

        Http11Request request = Http11Request.from(new ByteArrayInputStream(register.getBytes()));
        Http11Response response = Http11Response.ok();

        LoginController loginController = new LoginController();
        loginController.doGet(request, response);

        assertThat(new String(response.getBytes()))
                .contains("200 OK");
    }
}
