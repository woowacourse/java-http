package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import org.apache.catalina.session.HttpSession;
import org.apache.catalina.session.HttpSessionManger;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("로그인 컨트롤러")
class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        this.loginController = new LoginController();
    }

    @DisplayName("로그인 컨트롤러는 로그인 페이지를 반환한다.")
    @Test
    void doGet() throws IOException {
        // given
        List<String> headers = List.of(
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        );
        HttpRequest request = HttpRequest.of(headers, "");
        HttpResponse response = new HttpResponse();
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = Files.readString(new File(resource.getFile()).toPath());

        // when
        loginController.doGet(request, response);

        // then
        assertThat(response).extracting("body")
                .isEqualTo(expected);
    }

    @DisplayName("로그인 컨트롤러는 이미 로그인 된 사용자면 인덱스 페이지로 보낸다.")
    @Test
    void doGetWithAlreadyLogin() throws IOException {
        // given
        HttpSessionManger httpSessionManger = HttpSessionManger.getInstance();
        User user = new User("tester", "password", "test@gmail.com");
        HttpSession httpSession = new HttpSession("something");
        httpSession.setAttribute(HttpSession.USER_ATTRIBUTE, user);
        httpSessionManger.add(httpSession);

        List<String> headers = List.of(
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=something "
        );
        HttpRequest request = HttpRequest.of(headers, "");
        HttpResponse response = new HttpResponse();
        String expected = "/index.html";

        // when
        loginController.doGet(request, response);

        // then
        assertThat(response).extracting("httpHeader")
                .extracting("headers")
                .extracting("Location")
                .isEqualTo(expected);
    }

    @DisplayName("로그인 컨트롤러는 로그인 후 인덱스 페이지로 리다이렉트 한다.")
    @Test
    void doPost() {
        // given
        InMemoryUserRepository.save(new User("test", "test", "test@gmail.com"));
        String body = "account=test&password=test";
        List<String> headers = List.of(
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded "
        );
        HttpRequest request = HttpRequest.of(headers, body);
        HttpResponse response = new HttpResponse();
        String expected = "/index.html";

        // when
        loginController.doPost(request, response);

        // then
        assertThat(response).extracting("httpHeader")
                .extracting("headers")
                .extracting("Location")
                .isEqualTo(expected);
    }

    @DisplayName("로그인 컨트롤러는 로그인 실패 시 401 페이지로 리다이렉트 한다.")
    @Test
    void doPostWithLoginFail() {
        // given
        String body = "account=test&password=wrongPassword";
        List<String> headers = List.of(
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded "
        );
        HttpRequest request = HttpRequest.of(headers, body);
        HttpResponse response = new HttpResponse();
        String expected = "/401.html";

        // when
        loginController.doPost(request, response);

        // then
        assertThat(response).extracting("httpHeader")
                .extracting("headers")
                .extracting("Location")
                .isEqualTo(expected);
    }
}
