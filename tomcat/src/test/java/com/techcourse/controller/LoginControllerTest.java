package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.apache.coyote.http11.httpresponse.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestMaker;

class LoginControllerTest {

    @DisplayName("회원가입 되어있는 id와 password로 로그인을 하면 쿠키를 세팅하고 index.html로 이동한다")
    @Test
    void login() {
        String body = "account=gugu&password=1";
        final String login = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(login);
        String id = httpRequest.getSession().getId();

        LoginController loginController = new LoginController();
        HttpResponse httpResponse = loginController.doPost(httpRequest);

        assertThat(httpResponse.getBytes())
                .contains("HTTP/1.1 302 Found ".getBytes())
                .contains("Location: /index.html ".getBytes())
                .contains(("Set-Cookie: JSESSIONID=" + id + " ").getBytes());
    }

    @DisplayName("id와 비밀번호가 일치하지 않으면 예외를 발생시킨다")
    @Test
    void notMatchAccountAndPassword() {
        String body = "account=gugu&password=2";
        final String login = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(login);

        LoginController loginController = new LoginController();

        assertThatThrownBy(() -> loginController.doPost(httpRequest))
                .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("회원가입 되어있지 않은 account로 로그인을 하면 예외를 발생시킨다")
    @Test
    void notExistByAccount() {
        String body = "account=gugu2&password=1";
        final String login = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(login);

        LoginController loginController = new LoginController();

        assertThatThrownBy(() -> loginController.doPost(httpRequest))
                .isInstanceOf(UnauthorizedException.class);
    }

    @DisplayName("유저가 일부 값을 입력하지 않을 시 로그인 페이지로 이동한다")
    @Test
    void notExistUserInput() {
        String body = "account=&password=1";
        final String login = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body);
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(login);

        LoginController loginController = new LoginController();
        HttpResponse httpResponse = loginController.doPost(httpRequest);

        assertThat(httpResponse.getBytes())
                .contains("HTTP/1.1 302 Found ".getBytes())
                .contains("Location: /login".getBytes());
    }

    @DisplayName("쿠키에 유저 정보를 의미하는 세션이 없으면 로그인 페이지로 이동한다")
    @Test
    void notExistSessionLogin() throws IOException {
        final String login = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(login);

        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        LoginController loginController = new LoginController();
        HttpResponse httpResponse = loginController.doGet(httpRequest);

        assertThat(httpResponse.getBytes())
                .contains("HTTP/1.1 200 OK ".getBytes())
                .contains("Content-Type: text/html;charset=utf-8 ".getBytes())
                .contains(("Content-Length: " + body.getBytes().length + " ").getBytes())
                .contains(body.getBytes());
    }

    @DisplayName("쿠키에 유저 정보가 저장된 세션이 있으면 index.html 페이지로 이동한다")
    @Test
    void existSessionLogin() {
        SessionManager sessionManager = new SessionManager();
        Session session = new Session("abcdefg");
        sessionManager.add(session);
        final String login = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=abcdefg",
                "Connection: keep-alive ",
                "");
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(login);

        LoginController loginController = new LoginController();
        HttpResponse httpResponse = loginController.doGet(httpRequest);

        assertThat(httpResponse.getBytes())
                .contains("HTTP/1.1 200 OK ".getBytes())
                .contains("Location: /login".getBytes());
    }
}
