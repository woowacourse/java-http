package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.model.User;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.response.Http11Response;
import org.apache.coyote.http11.response.Http11ResponseStartLine;
import org.apache.coyote.http11.response.HttpStatusCode;
import org.apache.coyote.session.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @DisplayName("로그인에 성공하면 /index.html 파일로 리다이렉트 된다.")
    @Test
    void doPostSuccess() throws IOException {
        String httpRequestString = String.join(
                "\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30 ",
                "",
                "account=gugu&password=password"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes());
        Http11Request httpRequest = Http11Request.from(inputStream);
        Http11Response httpResponse = Http11Response.create();

        LoginController loginController = new LoginController();
        loginController.doPost(httpRequest, httpResponse);

        assertAll(
                () -> assertThat(httpResponse.getStartLine())
                        .isEqualTo(new Http11ResponseStartLine(HttpStatusCode.FOUND)),
                () -> assertThat(httpResponse.getHeaders().toString())
                        .contains("Location: /index.html ")
        );
    }

    @DisplayName("로그인에 실패하면 /401.html 파일로 리다이렉트 된다.")
    @Test
    void doPostFail() throws IOException {
        String httpRequestString = String.join(
                "\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 29 ",
                "",
                "account=gugu&password=passwod"
        );
        InputStream inputStream = new ByteArrayInputStream(httpRequestString.getBytes());
        Http11Request httpRequest = Http11Request.from(inputStream);
        Http11Response httpResponse = Http11Response.create();

        LoginController loginController = new LoginController();
        loginController.doPost(httpRequest, httpResponse);

        assertAll(
                () -> assertThat(httpResponse.getStartLine())
                        .isEqualTo(new Http11ResponseStartLine(HttpStatusCode.FOUND)),
                () -> assertThat(httpResponse.getHeaders().toString())
                        .contains("Location: /401.html ")
        );
    }

    @DisplayName("이미 로그인 중이면 /index.html 파일로 리다이렉트 된다.")
    @Test
    void doGetAlreadyLogin() throws IOException {
        LoginController loginController = new LoginController();

        String getLogin = String.join(
                "\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=abcdefg ",
                ""
        );
        InputStream inputStream = new ByteArrayInputStream(getLogin.getBytes());
        Http11Request httpRequest = Http11Request.from(inputStream);
        Http11Response httpResponse = Http11Response.create();
        Session session = httpRequest.getSession();
        session.setAttribute("user", new User("gugu", "password", "email"));

        loginController.doGet(httpRequest, httpResponse);

        assertAll(
                () -> assertThat(httpResponse.getStartLine())
                        .isEqualTo(new Http11ResponseStartLine(HttpStatusCode.FOUND)),
                () -> assertThat(httpResponse.getHeaders().toString())
                        .contains("Location: /index.html ")
        );
    }

    @DisplayName("로그인 GET 요청이면 /login.html 을 응답한다.")
    @Test
    void doGet() throws IOException {
        LoginController loginController = new LoginController();
        String getLogin = String.join(
                "\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                ""
        );
        InputStream inputStream = new ByteArrayInputStream(getLogin.getBytes());
        Http11Request httpRequest = Http11Request.from(inputStream);
        Http11Response httpResponse = Http11Response.create();

        loginController.doGet(httpRequest, httpResponse);

        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertAll(
                () -> assertThat(httpResponse.getStartLine()).isEqualTo(Http11ResponseStartLine.defaultLine()),
                () -> assertThat(httpResponse.getHeaders().toString()).contains("Content-Length: 3797 "),
                () -> assertThat(httpResponse.getHeaders().toString()).contains("Content-Type: text/html;charset=utf-8 "),
                () -> assertThat(httpResponse.getBody()).isEqualTo(body)
        );
    }
}
