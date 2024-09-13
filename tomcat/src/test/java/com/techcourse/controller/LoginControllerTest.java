package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.service.UserService;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http11.message.common.HttpBody;
import org.apache.coyote.http11.message.common.HttpHeaders;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.request.HttpRequestLine;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.util.ResourceReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private UserService userService;
    private SessionManager sessionManager;
    private LoginController loginController;

    @BeforeEach
    void setUp() {
        userService = new UserService();
        sessionManager = SessionManager.getInstance();
        loginController = new LoginController(userService);

        InMemoryUserRepository.truncate();
    }

    @DisplayName("사용자가 로그인에 성공하면 인덱스 페이지로 리다이렉트 되고 쿠키가 설정된다.")
    @Test
    void doPostSuccess() throws IOException, URISyntaxException {
        // given
        String account = "testUser";
        String password = "password123";
        String email = "test@example.com";
        userService.registerUser(account, password, email);

        HttpRequest request = new HttpRequest(
                new HttpRequestLine("POST /login HTTP/1.1"),
                new HttpHeaders("Content-Type: application/x-www-form-urlencoded"),
                new HttpBody("account=testUser&password=password123")
        );
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);

        // then
        Assertions.assertAll(
                () -> assertThat(response.toString()).contains("HTTP/1.1 302 Found "),
                () -> assertThat(response.toString()).contains("/index.html"),
                () -> assertThat(response.toString()).contains(Session.JSESSIONID)
        );
    }

    @DisplayName("로그인 실패 시 401 페이지가 반환된다.")
    @Test
    void doPostFailure() throws IOException, URISyntaxException {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine("POST /login HTTP/1.1"),
                new HttpHeaders("Content-Type: application/x-www-form-urlencoded"),
                new HttpBody("account=testUser&password=wrongPassword")
        );
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);

        // then
        assertThat(response.toString()).contains("HTTP/1.1 401 Unauthorized");
    }

    @DisplayName("세션이 없는 경우 로그인 페이지를 반환한다.")
    @Test
    void doGetWithoutSession() throws Exception {
        // given
        HttpRequest request = new HttpRequest(
                new HttpRequestLine("GET /login HTTP/1.1"),
                new HttpHeaders(),
                new HttpBody("")
        );
        HttpResponse response = new HttpResponse();

        URL url = ResourceReader.readResource("static/login.html");
        Path path = new File(url.getPath()).toPath();
        String body = new String(Files.readAllBytes(path));

        // when
        loginController.doGet(request, response);

        // then
        assertThat(response.toString()).contains("HTTP/1.1 200 OK ");
        assertThat(response.toString()).contains(body);
    }
}
