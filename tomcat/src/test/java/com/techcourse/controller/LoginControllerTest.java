package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.manager.Session;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private LoginController loginController;
    private HttpResponse httpResponse;
    private SessionManager sessionManager;

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
        httpResponse = new HttpResponse();
        sessionManager = SessionManager.getInstance();
    }

    @DisplayName("로그인하지 않은 상태로 GET /login 요청을 하면 로그인 페이지를 응답한다.")
    @Test
    void doGet_whenNotLogin() throws IOException {
        String request = "GET /login HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        loginController.doGet(httpRequest, httpResponse);
        String actual = new String(httpResponse.buildResponse());
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Length: 3797 \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("로그인한 상태로 GET /login 요청을 하면 인덱스 페이지로 리다이렉트한다.")
    @Test
    void doGet_whenLogin() throws IOException {
        Session session = new Session();
        session.setAttribute("user", new User("test", "password", "test@example.com"));
        sessionManager.add(session);
        String request = "GET /login HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Cookie: JSESSIONID=" + session.getId() + "\r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        loginController.doGet(httpRequest, httpResponse);
        String actual = new String(httpResponse.buildResponse());
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("유효한 유저의 정보로 POST /login 요청을 하면 인덱스 페이지로 리다이렉트한다.")
    @Test
    void doPost_whenValidUser() throws IOException {
        InMemoryUserRepository.save(new User("atto", "attoatto", "atto@atto.com"));
        String request = "POST /login HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Content-Length: 30 \r\n\r\n" +
                "account=atto&password=attoatto";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        loginController.doPost(httpRequest, httpResponse);
        String actual = new String(httpResponse.buildResponse());

        assertAll(
                () -> assertThat(actual).contains("HTTP/1.1 302 Found"),
                () -> assertThat(actual).contains("Location: /index.html"),
                () -> assertThat(actual).contains("Set-Cookie: JSESSIONID=")
        );
    }

    @DisplayName("잘못된 유저의 정보로 POST /login 요청을 하면 401 페이지로 리다이렉트한다.")
    @Test
    void doPost_whenInvalidUser() throws IOException {
        String request = "POST /login HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n" +
                "Content-Length: 34 \r\n\r\n" +
                "account=notExist&password=password";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        loginController.doPost(httpRequest, httpResponse);
        String actual = new String(httpResponse.buildResponse());
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /401.html \r\n" +
                "\r\n";

        assertThat(actual).isEqualTo(expected);
    }
}
