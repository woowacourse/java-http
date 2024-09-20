package org.apache.catalina.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.List;
import org.apache.catalina.manager.SessionManager;
import org.apache.catalina.request.HttpRequest;
import org.apache.catalina.response.HttpResponse;
import org.apache.catalina.session.Session;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final SessionManager sessionManager;

    public LoginControllerTest() {
        this.sessionManager = SessionManager.getInstance();
    }

    @BeforeEach
    void initializeDatabase() {
        InMemoryUserRepository.reset();
    }

    @Test
    void doPost_로그인성공() {
        // given
        LoginController loginController = new LoginController();
        String account = "account";
        String password = "password";
        String email = "email";
        InMemoryUserRepository.save(new User(account, password, email));

        HttpRequest request = new HttpRequest(
                List.of("POST / HTTP/1.1"),
                "account=" + account + "&password=" + password + "&email=" + email
        );
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);

        // then
        assertAll(
                () -> assertThat(response.getReponse()).contains("302 FOUND"),
                () -> assertThat(response.getReponse()).contains("Location: /index.html")
        );
    }

    @Test
    void doPost_로그인실패() {
        // given
        LoginController loginController = new LoginController();
        String account = "account";
        String password = "password";
        String email = "email";
        HttpRequest request = new HttpRequest(
                List.of("POST / HTTP/1.1"),
                "account=" + account + "&password=" + password + "&email=" + email
        );
        HttpResponse response = new HttpResponse();

        // when
        loginController.doPost(request, response);

        // then
        assertAll(
                () -> assertThat(response.getReponse()).contains("302 FOUND"),
                () -> assertThat(response.getReponse()).contains("Location: /401.html")
        );
    }

    @Test
    void doGet_로그인_안한_상태() {
        // given
        LoginController loginController = new LoginController();
        HttpRequest request = new HttpRequest(List.of("GET / HTTP/1.1"), "");
        HttpResponse response = new HttpResponse();

        // when
        loginController.doGet(request, response);

        // then
        assertAll(
                () -> assertThat(response.getReponse()).contains("200 OK"),
                () -> assertThat(response.getReponse()).contains("<title>로그인</title>")
        );
    }

    @Test
    void doGet_로그인_한_상태() {
        // given
        LoginController loginController = new LoginController();
        Session session = new Session(new User("account", "password", "email"));
        sessionManager.add(session);
        HttpRequest request = new HttpRequest(List.of(
                "GET / HTTP/1.1",
                "Cookie: JSESSIONID=" + session.getId() + "; "
        ), "");
        HttpResponse response = new HttpResponse();

        // when
        loginController.doGet(request, response);

        // then
        assertAll(
                () -> assertThat(response.getReponse()).contains("302 FOUND"),
                () -> assertThat(response.getReponse()).contains("Location: /index.html")
        );
    }
}
