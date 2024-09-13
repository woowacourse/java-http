package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginControllerTest {

    @Test
    @DisplayName("로그인 요청을 보낼 수 있다.")
    void doPost() throws IOException {
        String loginRequest =
                "POST /login HTTP/1.1" + "\r\n" +
                "Host: localhost:8080" + "\r\n" +
                "Connection: keep-alive" + "\r\n" +
                "Content-Length: 30" + "\r\n" +
                "\r\n" +
                "account=gugu&password=password";

        Http11Request request = Http11Request.from(new ByteArrayInputStream(loginRequest.getBytes()));
        Http11Response response = Http11Response.ok();

        LoginController loginController = new LoginController();
        loginController.doPost(request, response);

        assertThat(new String(response.getBytes()))
                .contains("302 Found")
                .contains("Set-Cookie");
    }

    @Test
    @DisplayName("password가 잘못되면 로그인 할 수 없다.")
    void doPost_invalidPassword_exception() throws IOException {
        String loginRequest =
                "POST /login HTTP/1.1" + "\r\n" +
                        "Host: localhost:8080" + "\r\n" +
                        "Connection: keep-alive" + "\r\n" +
                        "Content-Length: 34" + "\r\n" +
                        "\r\n" +
                        "account=gugu&password=not_password";

        Http11Request request = Http11Request.from(new ByteArrayInputStream(loginRequest.getBytes()));
        Http11Response response = Http11Response.ok();

        LoginController loginController = new LoginController();
        loginController.doPost(request, response);

        assertThat(new String(response.getBytes()))
                .contains("302 Found")
                .doesNotContain("Set-Cookie");
    }

    @Test
    @DisplayName("로그인 페이지 요청을 보낼 수 있다.")
    void doGet() throws IOException {
        String loginRequest =
                "GET /login HTTP/1.1" + "\r\n" +
                "Host: localhost:8080" + "\r\n" +
                "Connection: keep-alive" + "\r\n\r\n";

        Http11Request request = Http11Request.from(new ByteArrayInputStream(loginRequest.getBytes()));
        Http11Response response = Http11Response.ok();

        LoginController loginController = new LoginController();
        loginController.doGet(request, response);

        assertThat(new String(response.getBytes()))
                .contains("200 OK");
    }
}
